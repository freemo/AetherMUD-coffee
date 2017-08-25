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


public class LeatherCap extends StdArmor {
    public LeatherCap() {
        super();

        setName("a leather cap");
        setDisplayText("a round leather cap sits here.");
        setDescription("It looks like its made of cured leather hide, with metal bindings.");
        properWornBitmap = Wearable.WORN_HEAD;
        wornLogicalAnd = false;
        basePhyStats().setArmor(4);
        basePhyStats().setWeight(1);
        basePhyStats().setAbility(0);
        baseGoldValue = 5;
        recoverPhyStats();
        material = RawMaterial.RESOURCE_LEATHER;
    }

    @Override
    public String ID() {
        return "LeatherCap";
    }

}
