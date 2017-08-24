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

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class GlovesSpeed extends StdArmor {
    public GlovesSpeed() {
        super();

        setName("a pair of gloves");
        setDisplayText("a pair of finely crafted gloves is found on the ground.");
        setDescription("This is a pair of very nice gloves.");
        secretIdentity = "Gloves of the blinding strike (Double attack speed, truly usable only by fighters.)";
        baseGoldValue += 10000;
        properWornBitmap = Wearable.WORN_HANDS;
        wornLogicalAnd = false;
        basePhyStats().setArmor(15);
        basePhyStats().setAbility(0);
        basePhyStats().setWeight(1);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();

    }

    @Override
    public String ID() {
        return "GlovesSpeed";
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((!this.amWearingAt(Wearable.IN_INVENTORY)) && (!this.amWearingAt(Wearable.WORN_HELD))) {
            affectableStats.setSpeed(affectableStats.speed() * 2.0);
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + 10);
        }
    }

}
