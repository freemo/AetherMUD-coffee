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
package com.syncleus.aethermud.game.Items.Weapons;

import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;


public class SmallMace extends StdWeapon {
    public SmallMace() {
        super();

        setName("a small mace");
        setDisplayText("a small mace has been left here.");
        setDescription("It`s metallic and quite hard..");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(10);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(4);
        baseGoldValue = 8;
        recoverPhyStats();
        weaponDamageType = TYPE_BASHING;
        material = RawMaterial.RESOURCE_STEEL;
        weaponClassification = Weapon.CLASS_BLUNT;
    }

    @Override
    public String ID() {
        return "SmallMace";
    }

}
