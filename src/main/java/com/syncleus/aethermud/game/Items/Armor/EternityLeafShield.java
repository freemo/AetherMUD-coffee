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

import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;


public class EternityLeafShield extends StdShield {
    public EternityLeafShield() {
        super();

        setName("a huge leaf");
        setDisplayText("a huge and very rigid leaf lays on the ground.");
        setDescription("a very huge and very rigid leaf");
        secretIdentity = "A shield made from one of the leaves of the Fox god\\`s Eternity Trees.  (Armor:  30)";
        properWornBitmap = Wearable.WORN_HELD;
        wornLogicalAnd = true;
        baseGoldValue += 15000;
        basePhyStats().setArmor(30);
        basePhyStats().setAbility(0);
        basePhyStats().setWeight(15);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();
        material = RawMaterial.RESOURCE_SEAWEED;
    }

    @Override
    public String ID() {
        return "EternityLeafShield";
    }
}
