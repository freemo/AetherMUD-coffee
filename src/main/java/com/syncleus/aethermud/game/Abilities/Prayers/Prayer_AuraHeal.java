/**
 * Copyright 2017 Syncleus, Inc.
 * with portions copyright 2004-2017 Bo Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.List;


public class Prayer_AuraHeal extends Prayer {
    private final static String localizedName = CMLib.lang().L("Aura of Healing");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Heal Aura)");
    private int ratingTickDown = 4;

    public Prayer_AuraHeal() {
        super();

        ratingTickDown = 4;
    }

    @Override
    public String ID() {
        return "Prayer_AuraHeal";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HEALING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY | Ability.FLAG_HEALINGMAGIC;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if ((affected == null) || (!(affected instanceof Room)))
            return;
        final Room R = (Room) affected;

        super.unInvoke();

        if (canBeUninvoked())
            R.showHappens(CMMsg.MSG_OK_VISUAL, L("The healing aura around you fades."));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected == null) || (!(affected instanceof Room)))
            return super.tick(ticking, tickID);

        if ((--ratingTickDown) >= 0)
            return super.tick(ticking, tickID);
        ratingTickDown = 4;

        HashSet<MOB> H = null;
        if ((invoker() != null) && (invoker().location() == affected)) {
            H = new HashSet<MOB>();
            invoker().getGroupMembers(H);
        }
        final Room R = (Room) affected;
        for (int i = 0; i < R.numInhabitants(); i++) {
            final MOB M = R.fetchInhabitant(i);
            if ((M != null)
                && (M.curState().getHitPoints() < M.maxState().getHitPoints())
                && ((H == null)
                || (M.getVictim() == null)
                || (!H.contains(M.getVictim())))) {
                final int oldHP = M.curState().getHitPoints();
                if (invoker() != null) {
                    final int healing = CMLib.dice().roll(2, adjustedLevel(invoker(), 0), 4);
                    CMLib.combat().postHealing(invoker(), M, this, healing, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
                } else {
                    final int healing = CMLib.dice().roll(2, CMLib.ableMapper().lowestQualifyingLevel(ID()), 4);
                    CMLib.combat().postHealing(M, M, this, healing, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
                }
                if (M.curState().getHitPoints() > oldHP)
                    M.tell(L("You feel a little better!"));
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (CMLib.flags().isUndead((MOB) target))
                    return Ability.QUALITY_INDIFFERENT;
                if (target != mob) {
                    if (CMLib.flags().isUndead((MOB) target))
                        return super.castingQuality(mob, target, Ability.QUALITY_MALICIOUS);
                }
            }
            return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("The aura of healing is already here."));
            return false;
        }

        final Ability oldPrayerA = target.fetchEffect("Prayer_AuraHarm");
        if (oldPrayerA != null) {
            oldPrayerA.unInvoke();
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 for all to feel better.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("A healing aura descends over the area!"));
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for an aura of healing, but <S-HIS-HER> plea is not answered.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
