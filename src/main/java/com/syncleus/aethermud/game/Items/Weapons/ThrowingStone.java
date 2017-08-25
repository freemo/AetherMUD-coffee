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
package com.planet_ink.game.Items.Weapons;

import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;


public class ThrowingStone extends StdWeapon {
    public ThrowingStone() {
        super();
        setName("a throwing stone");
        setDisplayText("a sharp stone has been left here.");
        setDescription("It looks like it might sail far!");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(1);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(1);
        baseGoldValue = 10;
        recoverPhyStats();
        minRange = 0;
        maxRange = 3;
        weaponDamageType = Weapon.TYPE_BASHING;
        material = RawMaterial.RESOURCE_STONE;
        weaponClassification = Weapon.CLASS_THROWN;
        setRawLogicalAnd(false);
    }

    @Override
    public String ID() {
        return "ThrowingStone";
    }

}
