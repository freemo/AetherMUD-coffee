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


public class Plains extends StdRoom {
    public static final Integer[] resourceList = {
        Integer.valueOf(RawMaterial.RESOURCE_WHEAT),
        Integer.valueOf(RawMaterial.RESOURCE_HOPS),
        Integer.valueOf(RawMaterial.RESOURCE_BARLEY),
        Integer.valueOf(RawMaterial.RESOURCE_CORN),
        Integer.valueOf(RawMaterial.RESOURCE_RICE),
        Integer.valueOf(RawMaterial.RESOURCE_SMURFBERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_GREENS),
        Integer.valueOf(RawMaterial.RESOURCE_CARROTS),
        Integer.valueOf(RawMaterial.RESOURCE_TOMATOES),
        Integer.valueOf(RawMaterial.RESOURCE_BEANS),
        Integer.valueOf(RawMaterial.RESOURCE_ONIONS),
        Integer.valueOf(RawMaterial.RESOURCE_GARLIC),
        Integer.valueOf(RawMaterial.RESOURCE_FLINT),
        Integer.valueOf(RawMaterial.RESOURCE_COTTON),
        Integer.valueOf(RawMaterial.RESOURCE_MEAT),
        Integer.valueOf(RawMaterial.RESOURCE_HERBS),
        Integer.valueOf(RawMaterial.RESOURCE_EGGS),
        Integer.valueOf(RawMaterial.RESOURCE_BEEF),
        Integer.valueOf(RawMaterial.RESOURCE_HIDE),
        Integer.valueOf(RawMaterial.RESOURCE_FUR),
        Integer.valueOf(RawMaterial.RESOURCE_HONEY),
        Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
        Integer.valueOf(RawMaterial.RESOURCE_LEATHER),
        Integer.valueOf(RawMaterial.RESOURCE_WOOL)};
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));

    public Plains() {
        super();
        name = "the grass";
        basePhyStats.setWeight(2);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Plains";
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
