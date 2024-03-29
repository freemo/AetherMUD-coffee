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
package com.syncleus.aethermud.game.MOBS;

import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.interfaces.CMObject;

/*
   Copyright 2012-2017 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, e\ither express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class StdFactoryMOB extends StdMOB {
    @Override
    public String ID() {
        return "StdFactoryMOB";
    }

    @Override
    public CMObject newInstance() {
        try {
            return this.getClass().newInstance();
        } catch (final Exception e) {
            Log.errOut(ID(), e);
        }
        return new StdFactoryMOB();
    }

    @Override
    protected void finalize() throws Throwable {
        if (!amDestroyed)
            destroy();
        amDestroyed = false;
        if (!CMClass.returnMob(this)) {
            amDestroyed = true;
            super.finalize();
        }
    }

    @Override
    public void destroy() {
        try {
            CharStats savedCStats = charStats;
            if (charStats == baseCharStats)
                savedCStats = (CharStats) CMClass.getCommon("DefaultCharStats");
            PhyStats savedPStats = phyStats;
            if (phyStats == basePhyStats)
                savedPStats = (PhyStats) CMClass.getCommon("DefaultPhyStats");
            final CharState savedCState = curState;
            if ((curState == baseState) || (curState == maxState))
                curState = (CharState) CMClass.getCommon("DefaultCharState");
            super.destroy();
            removeFromGame = false;
            charStats = savedCStats;
            phyStats = savedPStats;
            curState = savedCState;
            baseCharStats.reset();
            basePhyStats.reset();
            baseState.reset();
            maxState.reset();
            curState.reset();
            phyStats.reset();
            charStats.reset();
            finalize();
        } catch (final Throwable t) {
            Log.errOut(ID(), t);
        }
    }
}
