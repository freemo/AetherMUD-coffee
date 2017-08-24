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

import com.planet_ink.coffee_mud.Locales.interfaces.Room;

import java.util.List;


public class PlainsGrid extends StdGrid {
    public PlainsGrid() {
        super();
        basePhyStats.setWeight(2);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "PlainsGrid";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_PLAINS;
    }

    @Override
    public String getGridChildLocaleID() {
        return "Plains";
    }

    @Override
    public List<Integer> resourceChoices() {
        return Plains.roomResources;
    }
}
