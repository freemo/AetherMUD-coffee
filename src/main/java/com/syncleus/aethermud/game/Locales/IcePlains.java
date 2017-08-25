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
import com.planet_ink.game.core.interfaces.Places;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class IcePlains extends StdRoom {
    public static final Integer[] resourceList = {
        Integer.valueOf(RawMaterial.RESOURCE_ELM),
        Integer.valueOf(RawMaterial.RESOURCE_MAPLE),
        Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_CARROTS),
        Integer.valueOf(RawMaterial.RESOURCE_GREENS),
        Integer.valueOf(RawMaterial.RESOURCE_ONIONS),
        Integer.valueOf(RawMaterial.RESOURCE_FLINT),
        Integer.valueOf(RawMaterial.RESOURCE_COTTON),
        Integer.valueOf(RawMaterial.RESOURCE_MEAT),
        Integer.valueOf(RawMaterial.RESOURCE_EGGS),
        Integer.valueOf(RawMaterial.RESOURCE_BEEF),
        Integer.valueOf(RawMaterial.RESOURCE_HIDE),
        Integer.valueOf(RawMaterial.RESOURCE_FUR),
        Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
        Integer.valueOf(RawMaterial.RESOURCE_LEATHER),
        Integer.valueOf(RawMaterial.RESOURCE_WOOL)};
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));

    public IcePlains() {
        super();
        name = "the snow";
        basePhyStats.setWeight(2);
        recoverPhyStats();
        climask = Places.CLIMASK_COLD | Places.CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "IcePlains";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_PLAINS;
    }

    @Override
    public List<Integer> resourceChoices() {
        return Plains.roomResources;
    }
}
