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
package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.HashSet;


public class Trap_Darkfall extends StdTrap {
    private final static String localizedName = CMLib.lang().L("darkfall");

    @Override
    public String ID() {
        return "Trap_Darkfall";
    }

    @Override
    public String name() {
        return localizedName;
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
    protected int trapLevel() {
        return 2;
    }

    @Override
    public String requiresToSet() {
        return "";
    }

    @Override
    public int baseRejuvTime(int level) {
        return 20;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (sprung) {
            if (!disabled)
                affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_DARK);
        } else
            disabled = false;
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null)) {
            if ((doesSaveVsTraps(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) setting off a trap!"));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> set(s) off a darkness trap!"))) {
                super.spring(target);
                target.location().recoverRoomStats();
            }
        }
    }
}
