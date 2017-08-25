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

import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.core.interfaces.Drink;
import com.planet_ink.game.core.interfaces.Places;


public class IndoorWaterSurfaceColumn extends WaterSurfaceColumn implements Drink {
    public IndoorWaterSurfaceColumn() {
        super();
        name = "the water";
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "IndoorWaterSurfaceColumn";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_WATERSURFACE;
    }

    @Override
    protected String UnderWaterLocaleID() {
        return "IndoorUnderWaterColumnGrid";
    }

    @Override
    protected int UnderWaterDomainType() {
        return Room.DOMAIN_INDOORS_UNDERWATER;
    }

    @Override
    public int maxRange() {
        return 5;
    }
}
