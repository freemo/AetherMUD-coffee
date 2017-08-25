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
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Song_Lullibye extends Song {
    private final static String localizedName = CMLib.lang().L("Lullaby");
    boolean asleep = false;

    @Override
    public String ID() {
        return "Song_Lullibye";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (invoker == null)
            return;
        if (affected == invoker)
            return;
        if (asleep)
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SLEEPING);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        final MOB mob = (MOB) affected;
        if (mob == null)
            return true;
        if (mob == invoker)
            return true;
        final boolean oldasleep = asleep;
        if (CMLib.dice().rollPercentage() > (50 - (2 * getXLEVELLevel(invoker()))))
            asleep = true;
        else
            asleep = false;
        if (asleep != oldasleep) {
            if (oldasleep) {
                if (CMLib.flags().isSleeping(mob))
                    mob.phyStats().setDisposition(mob.phyStats().disposition() - PhyStats.IS_SLEEPING);
                mob.location().show(mob, null, CMMsg.MSG_QUIETMOVEMENT, L("<S-NAME> wake(s) up."));
            } else {
                mob.location().show(mob, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> fall(s) asleep."));
                mob.phyStats().setDisposition(mob.phyStats().disposition() | PhyStats.IS_SLEEPING);
            }
        }

        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (msg.source() == invoker)
            return true;

        if (msg.source() != affected)
            return true;

        if ((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
            && ((msg.targetMinor() == CMMsg.TYP_STAND) || (msg.sourceMinor() == CMMsg.TYP_SIT)) && (asleep))
            return false;
        return true;
    }
}
