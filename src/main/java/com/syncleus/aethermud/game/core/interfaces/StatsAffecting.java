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
package com.syncleus.aethermud.game.core.interfaces;

import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;


/**
 * An interface for classes and objects which may affect mobs, rooms, items, and other Environmental types
 * By altering their stats and state objects using the layer system.
 * @author Bo Zimmerman
 *
 */
public interface StatsAffecting {
    /**
     * This method is called by the recoverPhyStats() method on other Environmental objects.  It is used
     * to transform the Environmental basePhyStats() object into a finished phyStats() object,  both of
     * which are objects implementing the PhyStats interface.  See those methods for more information.
     * @see com.syncleus.aethermud.game.Common.interfaces.PhyStats
     * @see Environmental
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#basePhyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#phyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @param affected the host of the PhyStats object being affected
     * @param affectableStats the particular PhyStats object being affected
     */
    public void affectPhyStats(Physical affected, PhyStats affectableStats);

    /**
     * This method is called by the recoverCharStats() method on other MOB objects.  It is used
     * to transform the MOB baseCharStats() object into a finished charStats() object,  both of
     * which are objects implementing the CharStats interface.  See those methods for more information.
     * @see com.syncleus.aethermud.game.Common.interfaces.CharStats
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB#baseCharStats()
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB#charStats()
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB#recoverCharStats()
     * @param affectedMob the host of the CharStats object being affected
     * @param affectableStats the particular CharStats object being affected
     */
    public void affectCharStats(MOB affectedMob, CharStats affectableStats);

    /**
     * This method is called by the recoverCharState() method on other MOB objects.  It is used
     * to transform the MOB baseCharState() object into a finished charState() object,  both of
     * which are objects implementing the CharState interface.  See those methods for more information.
     * @see com.syncleus.aethermud.game.Common.interfaces.CharState
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB#baseState()
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB#curState()
     * @see com.syncleus.aethermud.game.MOBS.interfaces.MOB#recoverMaxState()
     * @param affectedMob the host of the CharState object being affected
     * @param affectableMaxState the particular CharState object being affected
     */
    public void affectCharState(MOB affectedMob, CharState affectableMaxState);
}
