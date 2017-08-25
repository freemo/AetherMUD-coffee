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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Chant_VolcanicChasm extends Chant {
    private final static String localizedName = CMLib.lang().L("Volcanic Chasm");
    protected boolean checked = false;

    @Override
    public String ID() {
        return "Chant_VolcanicChasm";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_DEEPMAGIC;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof Room)) {
            final Room R = (Room) affected;
            for (int i = 0; i < R.numInhabitants(); i++) {
                final MOB M = R.fetchInhabitant(i);
                if ((M != null) && (CMLib.dice().rollPercentage() > M.charStats().getSave(CharStats.STAT_SAVE_FIRE))) {
                    final MOB invoker = (invoker() != null) ? invoker() : M;
                    CMLib.combat().postDamage(invoker, M, this, CMLib.dice().roll(1, M.phyStats().level() + (2 * getXLEVELLevel(invoker())), 1), CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_MELTING, L("The extreme heat <DAMAGES> <T-NAME>!"));
                    CMLib.combat().postRevengeAttack(M, invoker);
                }
            }
            for (int i = 0; i < R.numItems(); i++) {
                final Item I = R.getItem(i);
                if ((I != null) && (!CMLib.flags().isOnFire(I))) {
                    final Ability A = CMClass.getAbility("Burning");
                    if (A != null)
                        A.invoke(invoker(), I, true, 0);
                }
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if ((!checked)
            && (msg.targetMinor() == CMMsg.TYP_ENTER)
            && (affected instanceof Room)) {
            checked = true;
            if (!CMLib.threads().isTicking(this, -1))
                CMLib.threads().startTickDown(this, Tickable.TICKID_SPELL_AFFECT, 1);
        }
        super.executeMsg(host, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Room R = mob.location();
            if (R != null) {
                if ((R.domainType() != Room.DOMAIN_INDOORS_CAVE)
                    && ((R.getAtmosphere() & RawMaterial.MATERIAL_ROCK) == 0))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;
        if ((!auto)
            && (mob.location().domainType() != Room.DOMAIN_INDOORS_CAVE)
            && ((mob.location().getAtmosphere() & RawMaterial.MATERIAL_ROCK) == 0)) {
            mob.tell(L("This chant only works in caves."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to the walls.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    for (int i = 0; i < target.numInhabitants(); i++) {
                        final MOB M = target.fetchInhabitant(i);
                        if ((M != null) && (mob != M))
                            mob.location().show(mob, M, CMMsg.MASK_MALICIOUS | CMMsg.TYP_OK_VISUAL, null);
                    }
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Flames and sulfurous steam leap from cracks opening around you!"));
                    maliciousAffect(mob, target, asLevel, 0, -1);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) the walls, but the magic fades."));
        // return whether it worked
        return success;
    }
}
