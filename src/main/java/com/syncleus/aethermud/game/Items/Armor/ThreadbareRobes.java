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

public class ThreadbareRobes extends StdArmor {
    public ThreadbareRobes() {
        super();
        setName("a set of worn robes");
        setDisplayText("a set of worn robes");
        setDescription("These robes are patched, yet still gape with ragged holes. Evidently having seen years of use, they are vitualy worthless");
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        basePhyStats().setArmor(5);
        basePhyStats().setWeight(2);
        basePhyStats().setAbility(0);
        baseGoldValue = 1;
        material = RawMaterial.RESOURCE_COTTON;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "ThreadbareRobes";
    }

}
