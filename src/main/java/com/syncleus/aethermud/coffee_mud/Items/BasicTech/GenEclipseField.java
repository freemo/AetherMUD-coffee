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
package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class GenEclipseField extends GenTickerShield {

    public GenEclipseField() {
        super();
        setName("a personal eclipse field generator");
        setDisplayText("a personal eclipse field generator sits here.");
        setDescription("");
    }

    @Override
    public String ID() {
        return "GenEclipseField";
    }

    @Override
    protected String fieldOnStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "An eclipsing field surrounds <O-NAME>." :
            "An eclipsing field surrounds <T-NAME>.");
    }

    @Override
    protected String fieldDeadStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "The eclipsing field around <O-NAME> flickers and dies out as <O-HE-SHE> fade(s) back into view." :
            "The eclipsing field around <T-NAME> flickers and dies out as <T-HE-SHE> fade(s) back into view.");
    }

    @Override
    public void affectPhyStats(final Physical affected, final PhyStats affectableStats) {
        if (activated() && (affected == owner()) && (owner() instanceof MOB) && (!amWearingAt(Wearable.IN_INVENTORY)) && (powerRemaining() > 0))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
        super.affectPhyStats(affected, affectableStats);
    }
}
