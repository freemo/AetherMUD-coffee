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
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Places;


public class SaltWaterThinSurface extends SaltWaterSurface {
    public SaltWaterThinSurface() {
        super();
        name = "the water";
        basePhyStats.setWeight(2);
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "SaltWaterThinSurface";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_WATERSURFACE;
    }

    @Override
    protected String UnderWaterLocaleID() {
        return "UnderSaltWaterThinGrid";
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
    public CMObject newInstance() {
        if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
            return super.newInstance();
        return new SaltWaterSurface().newInstance();
    }
}
