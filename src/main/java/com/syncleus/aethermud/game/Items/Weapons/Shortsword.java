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


public class Shortsword extends Sword {
    public Shortsword() {
        super();

        setName("a short sword");
        setDisplayText("a short sword has been dropped on the ground.");
        setDescription("A sword with a not-too-long blade.");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(3);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(5);
        baseGoldValue = 10;
        recoverPhyStats();
        material = RawMaterial.RESOURCE_STEEL;
        weaponDamageType = TYPE_PIERCING;
    }

    @Override
    public String ID() {
        return "Shortsword";
    }

}
