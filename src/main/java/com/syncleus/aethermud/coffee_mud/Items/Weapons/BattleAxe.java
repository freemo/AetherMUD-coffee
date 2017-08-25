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
package com.planet_ink.coffee_mud.Items.Weapons;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;


public class BattleAxe extends Sword {
    public BattleAxe() {
        super();

        setName("a battle axe");
        setDisplayText("a heavy battle axe sits here");
        setDescription("It has a stout pole, about 4 feet in length with a trumpet shaped blade.");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(15);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(8);
        baseGoldValue = 35;
        wornLogicalAnd = true;
        material = RawMaterial.RESOURCE_STEEL;
        properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
        recoverPhyStats();
        weaponDamageType = Weapon.TYPE_SLASHING;
        weaponClassification = Weapon.CLASS_AXE;
    }

    @Override
    public String ID() {
        return "BattleAxe";
    }

}
