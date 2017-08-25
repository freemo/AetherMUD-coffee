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
package com.syncleus.aethermud.game.Items.Armor;

import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;


public class RingMail extends StdArmor {
    public RingMail() {
        super();

        setName("suit of ring mail");
        setDisplayText("a suit of armor made with large metal rings fastened to leather");
        setDescription("A suit of ring mail including everything to protect the body, legs and arms.");
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        basePhyStats().setArmor(22);
        basePhyStats().setWeight(50);
        basePhyStats().setAbility(0);
        baseGoldValue = 200;
        recoverPhyStats();
        material = RawMaterial.RESOURCE_STEEL;
    }

    @Override
    public String ID() {
        return "RingMail";
    }

}
