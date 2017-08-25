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


public class Rapier extends Sword {
    public Rapier() {
        super();

        setName("an sleek rapier");
        setDisplayText("a sleek rapier sits on the ground.");
        setDescription("It has a long, thin metal blade.");
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(4);
        material = RawMaterial.RESOURCE_STEEL;
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(7);
        baseGoldValue = 15;
        recoverPhyStats();
        weaponDamageType = TYPE_PIERCING;
    }

    @Override
    public String ID() {
        return "Rapier";
    }

}
