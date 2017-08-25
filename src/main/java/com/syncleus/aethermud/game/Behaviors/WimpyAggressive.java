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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class WimpyAggressive extends Aggressive {
    public WimpyAggressive() {
        super();

        tickWait = 0;
        tickDown = 0;
    }

    public static void pickAWimpyFight(MOB observer, boolean mobKiller, boolean misBehave, String attackMsg, String zapStr) {
        if (!canFreelyBehaveNormal(observer))
            return;
        final Room R = observer.location();
        if (R != null)
            for (int i = 0; i < R.numInhabitants(); i++) {
                final MOB mob = R.fetchInhabitant(i);
                if ((mob != null)
                    && (mob != observer)
                    && (CMLib.flags().isSleeping(mob))
                    && (CMLib.masking().maskCheck(zapStr, observer, false))) {
                    startFight(observer, mob, mobKiller, misBehave, attackMsg);
                    if (observer.isInCombat())
                        break;
                }
            }
    }

    public static void tickWimpyAggressively(Tickable ticking, boolean mobKiller, boolean misBehave, int tickID, String attackMsg, String zapStr) {
        if (tickID != Tickable.TICKID_MOB)
            return;
        if (ticking == null)
            return;
        if (!(ticking instanceof MOB))
            return;

        pickAWimpyFight((MOB) ticking, mobKiller, misBehave, attackMsg, zapStr);
    }

    @Override
    public String ID() {
        return "WimpyAggressive";
    }

    @Override
    public long flags() {
        return Behavior.FLAG_POTENTIALLYAGGRESSIVE | Behavior.FLAG_TROUBLEMAKING;
    }

    @Override
    public String accountForYourself() {
        if (getParms().trim().length() > 0)
            return "wimpy aggression against " + CMLib.masking().maskDesc(getParms(), true).toLowerCase();
        else
            return "wimpy aggressiveness";
    }

    @Override
    public boolean grantsAggressivenessTo(MOB M) {
        return ((M != null) && (CMLib.flags().isSleeping(M))) &&
            CMLib.masking().maskCheck(getParms(), M, false);
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        tickWait = CMParms.getParmInt(newParms, "delay", 0);
        tickDown = tickWait;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID != Tickable.TICKID_MOB)
            return true;
        if ((--tickDown) < 0) {
            tickDown = tickWait;
            tickWimpyAggressively(ticking, mobkill, misbehave, tickID, attackMessage, getParms());
        }
        return true;
    }
}
