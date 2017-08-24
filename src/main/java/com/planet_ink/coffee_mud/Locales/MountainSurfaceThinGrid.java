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

import java.util.List;


public class MountainSurfaceThinGrid extends StdThinGrid {
    public MountainSurfaceThinGrid() {
        super();
        name = "the mountains";
        basePhyStats.setWeight(5);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "MountainSurfaceThinGrid";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_MOUNTAINS;
    }

    @Override
    public CMObject newInstance() {
        if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
            return super.newInstance();
        return new MountainSurfaceGrid().newInstance();
    }

    @Override
    public String getGridChildLocaleID() {
        return "MountainSurface";
    }

    @Override
    public List<Integer> resourceChoices() {
        return Mountains.roomResources;
    }
}
