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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.BoardableShip;
import com.syncleus.aethermud.game.Items.interfaces.SailingShip;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_HideShip extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Hide Ship");
    private static final String[] triggerStrings = I(new String[]{"SHIPHIDE"});
    public int code = 0;
    private int bonus = 0;
    private int prof = 0;

    @Override
    public String ID() {
        return "Thief_HideShip";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
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
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALTHY;
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
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        final Physical affected = this.affected;
        if (affected != null) {
            if ((msg.source().riding() == affected)
                && (msg.source().isMonster())
                && (msg.source().Name().equals(affected.Name()))) {
                unInvoke();
                affected.recoverPhyStats();
            }
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_DETECTION, prof + bonus + affectableStats.getStat(CharStats.STAT_SAVE_DETECTION));
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
        if (CMLib.flags().isSneaking(affected))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_SNEAKING);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.isInCombat()) {
            mob.tell(L("Not while in combat!"));
            return false;
        }
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
            mob.tell(L("You must be on a ship to hide it!"));
            return false;
        }

        if (ship.fetchEffect(ID()) != null) {
            mob.tell(L("Your ship is already hidden!"));
            return false;
        }

        final Room shipR = CMLib.map().roomLocation(ship);
        if ((shipR == null) || (!CMLib.flags().isWaterySurfaceRoom(shipR)) || (!ship.subjectToWearAndTear())) {
            mob.tell(L("You must be on a sailing ship to hide it!"));
            return false;
        }

        if (ship.isInCombat()) {
            mob.tell(L("Your ship must not be in combat to hide it!"));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (!success) {
            beneficialVisualFizzle(mob, ship, L("<S-NAME> attempt(s) to hide <T-NAMESELF> in the mists and fail(s)."));
        } else {
            final CMMsg msg = CMClass.getMsg(mob, ship, this, auto ? CMMsg.MSG_OK_ACTION : (CMMsg.MSG_DELICATE_HANDS_ACT | CMMsg.MASK_MOVE), L("<S-NAME> hide(s) <T-NAMESELF> by sailing her into the mists and tall waves on the horizon."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                super.beneficialAffect(mob, ship, asLevel, Ability.TICKS_ALMOST_FOREVER);
                final Thief_HideShip newOne = (Thief_HideShip) ship.fetchEffect(ID());
                if (newOne != null) {
                    newOne.bonus = getXLEVELLevel(mob) * 2;
                    newOne.prof = proficiency();
                }
                mob.recoverPhyStats();
            } else
                success = false;
        }
        return success;
    }
}
