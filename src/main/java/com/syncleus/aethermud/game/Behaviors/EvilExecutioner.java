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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.Enumeration;
import java.util.Vector;


public class EvilExecutioner extends StdBehavior {
    protected boolean doPlayers = false;
    protected long deepBreath = System.currentTimeMillis();
    protected boolean noRecurse = true;

    @Override
    public String ID() {
        return "EvilExecutioner";
    }

    @Override
    public long flags() {
        return Behavior.FLAG_POTENTIALLYAGGRESSIVE;
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        newParms = newParms.toUpperCase();
        final Vector<String> V = CMParms.parse(newParms);
        doPlayers = V.contains("PLAYERS") || V.contains("PLAYER");
    }

    @Override
    public String accountForYourself() {
        return "aggression to goodness and paladins";
    }

    @Override
    public boolean grantsAggressivenessTo(MOB M) {
        if (M == null)
            return false;
        if (CMLib.flags().isBoundOrHeld(M))
            return false;
        if ((!M.isMonster()) && (!doPlayers))
            return false;
        for (final Enumeration<Behavior> e = M.behaviors(); e.hasMoreElements(); ) {
            final Behavior B = e.nextElement();
            if ((B != null) && (B != this) && (B.grantsAggressivenessTo(M)))
                return true;
        }
        return ((CMLib.flags().isGood(M)) || (M.baseCharStats().getCurrentClass().baseClass().equalsIgnoreCase("Paladin")));
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        final MOB source = msg.source();
        if (!canFreelyBehaveNormal(affecting)) {
            deepBreath = System.currentTimeMillis();
            return;
        }
        if ((deepBreath == 0) || ((System.currentTimeMillis() - deepBreath) > 60000) && (!noRecurse)) {
            noRecurse = true;
            deepBreath = 0;
            final MOB observer = (MOB) affecting;
            // base 90% chance not to be executed
            if ((source.isMonster() || doPlayers) && (source != observer) && (grantsAggressivenessTo(source))) {
                String reason = "GOOD";
                if (source.baseCharStats().getCurrentClass().baseClass().equalsIgnoreCase("Paladin"))
                    reason = "A PALADIN";
                final MOB oldFollowing = source.amFollowing();
                source.setFollowing(null);
                final boolean yep = Aggressive.startFight(observer, source, true, false, source.name().toUpperCase() + " IS " + reason + ", AND MUST BE DESTROYED!");
                if (!yep)
                    if (oldFollowing != null)
                        source.setFollowing(oldFollowing);
            }
            noRecurse = false;
        }
    }
}
