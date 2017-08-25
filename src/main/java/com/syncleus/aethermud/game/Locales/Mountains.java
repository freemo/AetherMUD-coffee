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

import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class Mountains extends StdRoom {
    public static final Integer[] resourceList = {
        Integer.valueOf(RawMaterial.RESOURCE_STONE),
        Integer.valueOf(RawMaterial.RESOURCE_IRON),
        Integer.valueOf(RawMaterial.RESOURCE_ALABASTER),
        Integer.valueOf(RawMaterial.RESOURCE_LEAD),
        Integer.valueOf(RawMaterial.RESOURCE_SILVER),
        Integer.valueOf(RawMaterial.RESOURCE_COPPER),
        Integer.valueOf(RawMaterial.RESOURCE_TIN),
        Integer.valueOf(RawMaterial.RESOURCE_AMETHYST),
        Integer.valueOf(RawMaterial.RESOURCE_GARNET),
        Integer.valueOf(RawMaterial.RESOURCE_AMBER),
        Integer.valueOf(RawMaterial.RESOURCE_HERBS),
        Integer.valueOf(RawMaterial.RESOURCE_OPAL),
        Integer.valueOf(RawMaterial.RESOURCE_TOPAZ),
        Integer.valueOf(RawMaterial.RESOURCE_BASALT),
        Integer.valueOf(RawMaterial.RESOURCE_SHALE),
        Integer.valueOf(RawMaterial.RESOURCE_SPICE),
        Integer.valueOf(RawMaterial.RESOURCE_PUMICE),
        Integer.valueOf(RawMaterial.RESOURCE_SANDSTONE),
        Integer.valueOf(RawMaterial.RESOURCE_SOAPSTONE),
        Integer.valueOf(RawMaterial.RESOURCE_AQUAMARINE),
        Integer.valueOf(RawMaterial.RESOURCE_CRYSOBERYL),
        Integer.valueOf(RawMaterial.RESOURCE_ONYX),
        Integer.valueOf(RawMaterial.RESOURCE_TURQUOISE),
        Integer.valueOf(RawMaterial.RESOURCE_DIAMOND),
        Integer.valueOf(RawMaterial.RESOURCE_CRYSTAL),
        Integer.valueOf(RawMaterial.RESOURCE_QUARTZ),
        Integer.valueOf(RawMaterial.RESOURCE_PLATINUM)};
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));

    public Mountains() {
        super();
        name = "the mountain";
        basePhyStats.setWeight(5);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Mountains";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_MOUNTAINS;
    }

    @Override
    public List<Integer> resourceChoices() {
        return Mountains.roomResources;
    }
}
