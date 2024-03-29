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

import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.CMObject;
import com.syncleus.aethermud.game.core.interfaces.Places;


public class IndoorWaterThinSurface extends IndoorWaterSurface {
    public IndoorWaterThinSurface() {
        super();
        name = "the water";
        basePhyStats.setWeight(2);
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "IndoorWaterThinSurface";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_WATERSURFACE;
    }

    @Override
    protected String UnderWaterLocaleID() {
        return "IndoorWaterThinGrid";
    }

    @Override
    protected int UnderWaterDomainType() {
        return Room.DOMAIN_INDOORS_UNDERWATER;
    }

    @Override
    protected boolean IsUnderWaterFatClass(Room thatSea) {
        return (thatSea instanceof IndoorUnderWaterGrid)
            || (thatSea instanceof IndoorUnderWaterThinGrid)
            || (thatSea instanceof IndoorUnderWaterColumnGrid);
    }

    @Override
    public int maxRange() {
        return 5;
    }

    @Override
    public CMObject newInstance() {
        if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
            return super.newInstance();
        return new IndoorWaterSurface().newInstance();
    }
}
