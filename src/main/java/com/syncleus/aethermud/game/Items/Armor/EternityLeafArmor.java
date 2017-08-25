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
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class EternityLeafArmor extends StdArmor {
    public EternityLeafArmor() {
        super();

        setName("a suit of Eternity Tree Leaf Armor");
        setDisplayText("a suit of Eternity tree leaf armor sits here.");
        setDescription("This suit of armor is made from the leaves of the Eternity Tree, a true gift from the Fox god himself.  (armor:  50, grants a modest degree of stealth, and is as light as cloth.)");
        properWornBitmap = Wearable.WORN_TORSO | Wearable.WORN_ARMS | Wearable.WORN_LEGS;
        wornLogicalAnd = true;
        baseGoldValue += 25000;
        basePhyStats().setArmor(50);
        basePhyStats().setAbility(0);
        basePhyStats().setWeight(15);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();
        material = RawMaterial.RESOURCE_SEAWEED;
    }

    @Override
    public String ID() {
        return "EternityLeafArmor";
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((!this.amWearingAt(Wearable.IN_INVENTORY)) && (!this.amWearingAt(Wearable.WORN_HELD)))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SNEAKING);
    }
}
