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

import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;


public class BandedArmor extends StdArmor {
    public BandedArmor() {
        super();
        setName("a suit of banded armor");
        setDisplayText("a suit of armor made from metal bands fastened to leather");
        setDescription("This suit of armor is made from metal bands fastened to leather and will provide protection for the torso, arms, and legs.");
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        basePhyStats().setArmor(44);
        basePhyStats().setWeight(55);
        basePhyStats().setAbility(0);
        baseGoldValue = 400;
        material = RawMaterial.RESOURCE_IRON;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "BandedArmor";
    }

}
