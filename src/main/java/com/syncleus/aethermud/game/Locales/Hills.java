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


public class Hills extends StdRoom {
    public static final Integer[] resourceList = {
        Integer.valueOf(RawMaterial.RESOURCE_GRAPES),
        Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_BLUEBERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_BLACKBERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_STRAWBERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_RASPBERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_BOYSENBERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_GREENS),
        Integer.valueOf(RawMaterial.RESOURCE_OLIVES),
        Integer.valueOf(RawMaterial.RESOURCE_BEANS),
        Integer.valueOf(RawMaterial.RESOURCE_RICE),
        Integer.valueOf(RawMaterial.RESOURCE_LEATHER),
        Integer.valueOf(RawMaterial.RESOURCE_FEATHERS),
        Integer.valueOf(RawMaterial.RESOURCE_MESQUITE),
        Integer.valueOf(RawMaterial.RESOURCE_WOOL),
        Integer.valueOf(RawMaterial.RESOURCE_EGGS),
        Integer.valueOf(RawMaterial.RESOURCE_HERBS),
        Integer.valueOf(RawMaterial.RESOURCE_POTATOES)
    };
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));

    public Hills() {
        super();
        name = "the hills";
        basePhyStats.setWeight(3);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Hills";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_HILLS;
    }

    @Override
    public List<Integer> resourceChoices() {
        return Hills.roomResources;
    }
}
