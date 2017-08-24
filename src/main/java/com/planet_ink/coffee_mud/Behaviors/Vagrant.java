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
package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Vagrant extends StdBehavior {
    protected int sleepForTicks = 0;
    protected int wakeForTicks = 0;

    @Override
    public String ID() {
        return "Vagrant";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "vagrant sleepiness";
    }

    @Override
    public boolean okMessage(Environmental oking, CMMsg msg) {
        if ((oking == null) || (!(oking instanceof MOB)))
            return super.okMessage(oking, msg);
        final MOB mob = (MOB) oking;
        if (msg.amITarget(mob)
            && (((msg.sourceMajor() & CMMsg.MASK_MOVE) > 0) || ((msg.sourceMajor() & CMMsg.MASK_HANDS) > 0))) {
            if (!msg.amISource(mob))
                sleepForTicks = 0;
            else if (sleepForTicks > 0) {
                mob.phyStats().setDisposition(mob.phyStats().disposition() | PhyStats.IS_SLEEPING);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);

        if (tickID != Tickable.TICKID_MOB)
            return true;
        final MOB mob = (MOB) ticking;
        if ((wakeForTicks <= 0) && (sleepForTicks <= 0)) {
            if ((CMLib.dice().rollPercentage() > 50) || (mob.isInCombat())) {
                CMLib.commands().postStand(mob, true);
                wakeForTicks = CMLib.dice().roll(1, 30, 0);
            } else {
                if (CMLib.flags().isAliveAwakeMobile(mob, true))
                    mob.location().show(mob, mob.location(), CMMsg.MSG_SLEEP, L("<S-NAME> curl(s) on the ground and go(es) to sleep."));
                if (CMLib.flags().isSleeping(mob))
                    sleepForTicks = CMLib.dice().roll(1, 10, 0);
            }
        } else if (wakeForTicks > 0)
            wakeForTicks--;
        else if (sleepForTicks > 0)
            sleepForTicks--;
        return true;
    }
}
