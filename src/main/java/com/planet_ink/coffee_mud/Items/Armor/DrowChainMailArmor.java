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


public class DrowChainMailArmor extends StdArmor {
    public DrowChainMailArmor() {
        super();

        setName("a suit of dark chain mail armor");
        setDisplayText("a suit of chain mail armor made of dark material sits here.");
        setDescription("This suit includes a fairly solid looking hauberk with leggings and a coif, all constructed from a strong, dark metal.");
        secretIdentity = "A suit of Drow Chain Mail Armor";
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        basePhyStats().setArmor(65);
        basePhyStats().setWeight(60);
        basePhyStats().setAbility(0);
        baseGoldValue = 1500;
        recoverPhyStats();
        material = RawMaterial.RESOURCE_STEEL;
    }

    @Override
    public String ID() {
        return "DrowChainMailArmor";
    }

}
