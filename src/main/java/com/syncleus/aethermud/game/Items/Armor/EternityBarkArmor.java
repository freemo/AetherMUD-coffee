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
package com.planet_ink.game.Items.Armor;

import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;


public class EternityBarkArmor extends StdArmor {
    public EternityBarkArmor() {
        super();

        setName("a suit of Eternity Tree Bark Armor");
        setDisplayText("a suit of Eternity tree bark armor sits here.");
        setDescription("This suit of armor is made from the bark of the Fox god\\`s Eternity Tree(armor:  100 and as light as leather armor--wearable by theives)");
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        baseGoldValue += 25000;
        basePhyStats().setArmor(100);
        basePhyStats().setAbility(0);
        basePhyStats().setWeight(15);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();
        material = RawMaterial.RESOURCE_WOOD;
    }

    @Override
    public String ID() {
        return "EternityBarkArmor";
    }

}
