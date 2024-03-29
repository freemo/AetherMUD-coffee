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
import com.syncleus.aethermud.game.Items.interfaces.Wearable;

import java.util.Random;


public class Halberd extends StdWeapon {
    public final static int PLAIN = 0;
    public final static int QUALITY_WEAPON = 1;
    public final static int EXCEPTIONAL = 2;
    public Halberd() {
        super();

        final Random randomizer = new Random(System.currentTimeMillis());
        final int HalberdType = Math.abs(randomizer.nextInt() % 3);

        this.phyStats.setAbility(HalberdType);
        setItemDescription(this.phyStats.ability());

        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(10);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(10);
        baseGoldValue = 10;
        recoverPhyStats();
        wornLogicalAnd = true;
        properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
        weaponDamageType = TYPE_SLASHING;
        material = RawMaterial.RESOURCE_STEEL;
        weaponClassification = Weapon.CLASS_POLEARM;
    }

    @Override
    public String ID() {
        return "Halberd";
    }

    public void setItemDescription(int level) {
        switch (level) {
            case Claymore.PLAIN:
                setName("a simple halberd");
                setDisplayText("a simple halberd is on the ground.");
                setDescription("It`s a polearm with a large bladed axe on the end.");
                break;
            case Claymore.QUALITY_WEAPON:
                setName("a very nice halberd");
                setDisplayText("a very nice halberd leans against the wall.");
                setDescription("It`s an ornate polearm with a large bladed axe on the end.");
                break;
            case Claymore.EXCEPTIONAL:
                setName("an exceptional halberd");
                setDisplayText("an exceptional halberd is found nearby.");
                setDescription("It`s an ornate polearm with a large bladed axe on the end.  It is well balanced and decorated with fine etchings.");
                break;
            default:
                setName("a simple halberd");
                setDisplayText("a simple halberd is on the ground.");
                setDescription("It`s a polearm with a large bladed axe on the end.");
                break;
        }
    }

}
