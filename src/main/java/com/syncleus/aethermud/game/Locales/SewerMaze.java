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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.interfaces.Places;

import java.util.List;
import java.util.Vector;


public class SewerMaze extends StdMaze {
    public SewerMaze() {
        super();
        myID = this.getClass().getName().substring(this.getClass().getName().lastIndexOf('.') + 1);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_DARK);
        basePhyStats.setWeight(2);
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "SewerMaze";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_CAVE;
    }

    @Override
    public String getGridChildLocaleID() {
        return "SewerRoom";
    }

    @Override
    public List<Integer> resourceChoices() {
        return new Vector<Integer>();
    }
}