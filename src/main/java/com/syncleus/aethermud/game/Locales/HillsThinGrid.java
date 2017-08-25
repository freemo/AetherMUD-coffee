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

import java.util.List;


public class HillsThinGrid extends StdThinGrid {
    public HillsThinGrid() {
        super();
        name = "the hills";
        basePhyStats.setWeight(3);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "HillsThinGrid";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_HILLS;
    }

    @Override
    public CMObject newInstance() {
        if (!CMSecurity.isDisabled(CMSecurity.DisFlag.THINGRIDS))
            return super.newInstance();
        return new HillsGrid().newInstance();
    }

    @Override
    public String getGridChildLocaleID() {
        return "Hills";
    }

    @Override
    public List<Integer> resourceChoices() {
        return Hills.roomResources;
    }
}
