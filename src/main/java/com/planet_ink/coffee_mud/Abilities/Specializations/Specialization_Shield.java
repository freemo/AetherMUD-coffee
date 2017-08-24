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
package com.planet_ink.coffee_mud.Abilities.Specializations;

import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Shield;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;


public class Specialization_Shield extends Specialization_Weapon {
    private final static String localizedName = CMLib.lang().L("Shield Specialization");

    public Specialization_Shield() {
        super();
        weaponClass = Weapon.CLASS_BLUNT;
    }

    @Override
    public String ID() {
        return "Specialization_Shield";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int getDamageBonus(MOB mob, int dmgType) {
        return getXLEVELLevel(mob);
    }

    @Override
    protected boolean isWeaponMatch(Weapon W) {
        return W instanceof Shield;
    }

    @Override
    protected boolean canDamage(MOB mob, Weapon W) {
        return W instanceof Shield;
    }

    @Override
    protected boolean isWearableItem(Item I) {
        return I instanceof Shield;
    }
}
