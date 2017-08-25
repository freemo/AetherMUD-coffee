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

import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class SaltWaterSurface extends WaterSurface {
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(UnderSaltWater.resourceList));

    static {
        roomResources.add(Integer.valueOf(RawMaterial.RESOURCE_SALMON));
        roomResources.add(Integer.valueOf(RawMaterial.RESOURCE_FISH));
    }

    public SaltWaterSurface() {
        super();
        liquidType = RawMaterial.RESOURCE_SALTWATER;
    }

    @Override
    public String ID() {
        return "SaltWaterSurface";
    }

    @Override
    protected String UnderWaterLocaleID() {
        return "UnderSaltWaterGrid";
    }

    @Override
    protected int UnderWaterDomainType() {
        return Room.DOMAIN_OUTDOORS_UNDERWATER;
    }

    @Override
    protected boolean IsUnderWaterFatClass(Room thatSea) {
        return (thatSea instanceof UnderSaltWaterGrid)
            || (thatSea instanceof UnderSaltWaterThinGrid)
            || (thatSea instanceof UnderSaltWaterColumnGrid);
    }

    @Override
    public List<Integer> resourceChoices() {
        return roomResources;
    }
}
