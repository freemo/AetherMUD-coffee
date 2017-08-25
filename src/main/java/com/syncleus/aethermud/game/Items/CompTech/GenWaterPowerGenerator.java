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
package com.planet_ink.game.Items.CompTech;

import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;


public class GenWaterPowerGenerator extends GenFuellessGenerator {
    public GenWaterPowerGenerator() {
        super();
        setName("a water power generator");
        setDisplayText("a water power generator sits here.");
        setDescription("");
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_SWIMMING);
        phyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_SWIMMING);
    }

    @Override
    public String ID() {
        return "GenWaterPowerGenerator";
    }

    @Override
    protected boolean canGenerateRightNow() {
        if (activated()) {
            final Room R = CMLib.map().roomLocation(this);
            if ((R != null) && (CMLib.flags().isWaterySurfaceRoom(R)))
                return true;
        }
        return false;
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof GenWaterPowerGenerator))
            return false;
        return super.sameAs(E);
    }
}
