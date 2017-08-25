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


public class Quarterstaff extends StdWeapon {
    public Quarterstaff() {
        super();

        setName("a wooden quarterstaff");
        setDisplayText("a wooden quarterstaff lies in the corner of the room.");
        setDescription("It`s long and wooden, just like a quarterstaff ought to be.");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(4);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(3);
        baseGoldValue = 1;
        recoverPhyStats();
        wornLogicalAnd = true;
        material = RawMaterial.RESOURCE_OAK;
        properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
        weaponDamageType = TYPE_BASHING;
        weaponClassification = Weapon.CLASS_STAFF;
    }

    @Override
    public String ID() {
        return "Quarterstaff";
    }

}
