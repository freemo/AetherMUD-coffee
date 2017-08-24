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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Prayer_AuraIntolerance extends Prayer {
    private final static String localizedName = CMLib.lang().L("Aura of Intolerance");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Intolerance Aura)");

    @Override
    public String ID() {
        return "Prayer_AuraIntolerance";
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
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB M = (MOB) affected;

        super.unInvoke();

        if ((canBeUninvoked()) && (M != null) && (!M.amDead()) && (M.location() != null))
            M.location().show(M, null, CMMsg.MSG_OK_VISUAL, L("The intolerant aura around <S-NAME> fades."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        if ((msg.source() == affected)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.target() instanceof MOB)
            && (msg.source().getWorshipCharID().length() > 0)
            && (!((MOB) msg.target()).getWorshipCharID().equals(msg.source().getWorshipCharID()))) {
            if (((MOB) msg.target()).getWorshipCharID().length() > 0)
                msg.setValue(msg.value() * 2);
            else
                msg.setValue(msg.value() + (msg.value() / 2));
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);

        if (!super.tick(ticking, tickID))
            return false;

        final Room R = ((MOB) affected).location();
        for (int i = 0; i < R.numInhabitants(); i++) {
            final MOB M = R.fetchInhabitant(i);
            if ((M != null)
                && (M != ((MOB) affected))
                && ((M.getWorshipCharID().length() == 0)
                || (((MOB) affected).getWorshipCharID().length() > 0) && (!M.getWorshipCharID().equals(((MOB) affected).getWorshipCharID())))) {
                if (M.getWorshipCharID().length() > 0)
                    CMLib.combat().postDamage(((MOB) affected), M, this, 3, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD, Weapon.TYPE_BURSTING, L("The intolerant aura around <S-NAME> <DAMAGES> <T-NAMESELF>!"));
                else
                    CMLib.combat().postDamage(((MOB) affected), M, this, 1, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD, Weapon.TYPE_BURSTING, L("The intolerant aura around <S-NAME> <DAMAGES> <T-NAMESELF>!"));
                CMLib.combat().postRevengeAttack(M, invoker);
            }
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (((mob.getWorshipCharID().length() == 0)
                || (CMLib.map().getDeity(mob.getWorshipCharID()) == null)))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("The aura of intolerance is already with <S-NAME>."));
            return false;
        }
        if ((!auto) && ((mob.getWorshipCharID().length() == 0)
            || (CMLib.map().getDeity(mob.getWorshipCharID()) == null))) {
            mob.tell(L("You must worship a god to be intolerant."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 for the aura of intolerance.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for an aura of intolerance, but <S-HIS-HER> plea is not answered.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
