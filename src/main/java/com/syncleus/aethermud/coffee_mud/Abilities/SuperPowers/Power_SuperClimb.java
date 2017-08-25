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
package com.planet_ink.coffee_mud.Abilities.SuperPowers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Power_SuperClimb extends SuperPower {
    private final static String localizedName = CMLib.lang().L("Super Climb");
    private static final String[] triggerStrings = I(new String[]{"CLIMB", "SUPERCLIMB"});

    @Override
    public String ID() {
        return "Power_SuperClimb";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
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
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MANA | USAGE_MOVEMENT;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_CLIMBING);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final int dirCode = CMLib.directions().getDirectionCode(CMParms.combine(commands, 0));
        if (dirCode < 0) {
            mob.tell(L("Climb where?"));
            return false;
        }
        if ((mob.location().getRoomInDir(dirCode) == null)
            || (mob.location().getExitInDir(dirCode) == null)) {
            mob.tell(L("You can't climb that way."));
            return false;
        }
        if (CMLib.flags().isSitting(mob) || CMLib.flags().isSleeping(mob)) {
            mob.tell(L("You need to stand up first!"));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_NOISYMOVEMENT, null);
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            success = proficiencyCheck(mob, 0, auto);

            if (mob.fetchEffect(ID()) == null) {
                mob.addEffect(this);
                mob.recoverPhyStats();
            }

            CMLib.tracking().walk(mob, dirCode, false, false);
            mob.delEffect(this);
            mob.recoverPhyStats();
            if (!success)
                mob.location().executeMsg(mob, CMClass.getMsg(mob, mob.location(), CMMsg.MASK_MOVE | CMMsg.TYP_GENERAL, null));
        }
        return success;
    }
}
