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
package com.planet_ink.coffee_mud.Items.Armor;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;


public class LeatherArmor extends StdArmor {
    public LeatherArmor() {
        super();

        setName("a suit of leather armor");
        setDisplayText("a suit of leather armor including a breastplate, arms, and legs.");
        setDescription("This is a fairly decent looking suit of leather armor.");
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        basePhyStats().setArmor(12);
        basePhyStats().setWeight(15);
        basePhyStats().setAbility(0);
        baseGoldValue = 10;
        recoverPhyStats();
        material = RawMaterial.RESOURCE_LEATHER;
    }

    @Override
    public String ID() {
        return "LeatherArmor";
    }

}
