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
package com.planet_ink.coffee_mud.Items.MiscMagic;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.Basic.StdItem;
import com.planet_ink.coffee_mud.Items.interfaces.MiscMagic;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;


public class DruidicMonument extends StdItem implements MiscMagic {
    public DruidicMonument() {
        super();

        setName("the druidic stones");
        setDisplayText("druidic stones are arrayed here.");
        setDescription("These large mysterious monuments have a power and purpose only the druid understands.");
        secretIdentity = "DRUIDIC STONES";
        basePhyStats().setLevel(1);
        setMaterial(RawMaterial.RESOURCE_STONE);
        basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
        basePhyStats().setWeight(1000);
        baseGoldValue = 0;
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "DruidicMonument";
    }

}
