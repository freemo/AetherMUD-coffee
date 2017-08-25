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
package com.planet_ink.game.Locales;

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Places;

import java.util.List;


public class UnderWaterMaze extends StdMaze {
    public UnderWaterMaze() {
        super();
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_SWIMMING);
        basePhyStats.setWeight(3);
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
        atmosphere = RawMaterial.RESOURCE_FRESHWATER;
    }

    @Override
    public String ID() {
        return "UnderWaterMaze";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_UNDERWATER;
    }

    @Override
    protected int baseThirst() {
        return 0;
    }

    @Override
    public String getGridChildLocaleID() {
        return "UnderWater";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        switch (UnderWater.isOkUnderWaterAffect(this, msg)) {
            case -1:
                return false;
            case 1:
                return true;
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        UnderWater.sinkAffects(this, msg);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SWIMMING);
    }

    @Override
    public List<Integer> resourceChoices() {
        return UnderWater.roomResources;
    }
}
