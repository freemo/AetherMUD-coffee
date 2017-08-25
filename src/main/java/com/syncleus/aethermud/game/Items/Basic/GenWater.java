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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;


public class GenWater extends GenDrink {
    public GenWater() {
        super();
        readableText = "";

        setName("a generic puddle of water");
        basePhyStats.setWeight(2);
        setDisplayText("a generic puddle of water sits here.");
        setDescription("");
        baseGoldValue = 0;
        capacity = 0;
        amountOfThirstQuenched = 250;
        amountOfLiquidHeld = 10000;
        amountOfLiquidRemaining = 10000;
        setMaterial(RawMaterial.RESOURCE_FRESHWATER);
        basePhyStats().setSensesMask(PhyStats.SENSE_ITEMNOTGET);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenWater";
    }

}
