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


public class Cestus extends StdWeapon {
    public Cestus() {
        super();

        setName("a mean looking cestus");
        setDisplayText("a cestus is on the gound.");
        setDescription("It\\`s a glove covered in long spikes and blades.");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(2);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(4);
        material = RawMaterial.RESOURCE_LEATHER;
        baseGoldValue = 5;
        recoverPhyStats();
        weaponDamageType = Weapon.TYPE_PIERCING;
        weaponClassification = Weapon.CLASS_EDGED;

    }

    @Override
    public String ID() {
        return "Cestus";
    }

}
