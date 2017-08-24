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
package com.planet_ink.coffee_mud.Locales;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.interfaces.Places;

import java.util.List;


public class UnderSaltWaterGrid extends UnderWaterGrid {
    public UnderSaltWaterGrid() {
        super();
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_SWIMMING);
        basePhyStats.setWeight(3);
        setDisplayText("Under the water");
        setDescription("");
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
        atmosphere = RawMaterial.RESOURCE_SALTWATER;
    }

    @Override
    public String ID() {
        return "UnderSaltWaterGrid";
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
        return "UnderSaltWater";
    }

    @Override
    public List<Integer> resourceChoices() {
        return UnderSaltWater.roomResources;
    }
}
