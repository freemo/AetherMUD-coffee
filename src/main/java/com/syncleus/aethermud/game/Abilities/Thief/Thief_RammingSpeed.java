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
package com.planet_ink.game.Abilities.Thief;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.BoardableShip;
import com.planet_ink.game.Items.interfaces.SailingShip;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Thief_RammingSpeed extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Rig Ramming Speed");
    private static final String[] triggerStrings = I(new String[]{"RIGRAMMINGSPEED", "RIGRAMMING", "RAMMINGSPEED"});
    protected int code = 0;

    @Override
    public String ID() {
        return "Thief_RammingSpeed";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_SEATRAVEL;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public int abilityCode() {
        return code;
    }

    @Override
    public void setAbilityCode(int newCode) {
        code = newCode;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setAbility(affectableStats.ability() + 1 + abilityCode());
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.source().riding() == affected)
            && (msg.sourceMinor() == CMMsg.TYP_ADVANCE)) {
            final Ability unInvokeMe = this;
            msg.addTrailerRunnable(new Runnable() {
                @Override
                public void run() {
                    unInvokeMe.unInvoke();
                }
            });
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        final Physical affected = this.affected;
        if (affected instanceof SailingShip) {
            if ((!((SailingShip) affected).isInCombat())
                || (CMLib.flags().isFalling(affected))) {
                unInvoke();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((CMLib.flags().isSitting(mob) || CMLib.flags().isSleeping(mob))) {
            mob.tell(L("You are on the floor!"));
            return false;
        }

        if (!CMLib.flags().isAliveAwakeMobileUnbound(mob, false))
            return false;

        final Room R = mob.location();
        if (R == null)
            return false;

        final SailingShip ship;
        if ((R.getArea() instanceof BoardableShip)
            && (((BoardableShip) R.getArea()).getShipItem() instanceof SailingShip)) {
            ship = (SailingShip) ((BoardableShip) R.getArea()).getShipItem();
        } else {
            mob.tell(L("You must be on a ship to move to ramming speed!"));
            return false;
        }

        if (ship.fetchEffect(ID()) != null) {
            mob.tell(L("Your ship is already prepared for ramming speed!"));
            return false;
        }

        final Room shipR = CMLib.map().roomLocation(ship);
        if ((shipR == null) || (!CMLib.flags().isWaterySurfaceRoom(shipR)) || (!ship.subjectToWearAndTear())) {
            mob.tell(L("You must be on a sailing ship to move to ramming speed!"));
            return false;
        }

        if (!ship.isInCombat()) {
            mob.tell(L("Your ship must be in combat to move to ramming speed!"));
            return false;
        }

        final int directionToTarget = ship.getDirectionToTarget();
        final int directionFacing = ship.getDirectionFacing();
        if (directionToTarget != directionFacing) {
            mob.tell(L("You ship must be facing @x1, towards your target, to ram at them.", CMLib.directions().getDirectionName(directionToTarget)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, ship, this, CMMsg.MASK_MALICIOUS | CMMsg.MSG_NOISYMOVEMENT, auto ? L("<T-NAME> is at full sail!") : L("<S-NAME> prepare(s) <T-NAME> for ramming speed!"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                Ability A = beneficialAffect(mob, ship, asLevel, 0);
                if (A != null)
                    A.setAbilityCode((super.getXLEVELLevel(mob) + 2) / 3);
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to move to ramming speed, but <S-IS-ARE> too slow."));
        return success;
    }
}
