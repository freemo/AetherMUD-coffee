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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Dance_Swing extends Dance {
    private final static String localizedName = CMLib.lang().L("Swing");
    protected boolean doneThisRound = false;

    @Override
    public String ID() {
        return "Dance_Swing";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected String danceOf() {
        return name() + " Dancing";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB)
            doneThisRound = false;
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (CMLib.flags().isAliveAwakeMobile(mob, true))
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (!doneThisRound)
            && (mob.rangeToTarget() == 0)) {
            if (msg.tool() instanceof Item) {
                final Item attackerWeapon = (Item) msg.tool();
                if ((attackerWeapon != null)
                    && (attackerWeapon instanceof Weapon)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_FLAILED)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_NATURAL)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_RANGED)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_THROWN)) {
                    if (proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) + (getXLEVELLevel(invoker()) * 2) - 70, false)) {
                        final CMMsg msg2 = CMClass.getMsg(mob, msg.source(), null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> parr(ys) @x1 attack from <T-NAME>!", attackerWeapon.name()));
                        if (mob.location().okMessage(mob, msg2)) {
                            doneThisRound = true;
                            mob.location().send(mob, msg2);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

}
