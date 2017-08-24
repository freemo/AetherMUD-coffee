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
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;


public class StdBoffWeapon extends StdWeapon {
    public StdBoffWeapon() {
        super();

        setName("boff weapon");
        setDisplayText(" sits here.");
        setDescription("This is a not so deadly looking weapon.");
        wornLogicalAnd = false;
        properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(0);
        basePhyStats().setAbility(0);
        baseGoldValue = 15;
        weaponDamageType = Weapon.TYPE_BASHING;
        weaponClassification = Weapon.CLASS_BLUNT;
        material = RawMaterial.RESOURCE_STEEL;
        setUsesRemaining(100);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdBoffWeapon";
    }

    @Override
    public String hitString(int damageAmount) {
        String word = "boff(s)";
        switch (CMLib.dice().roll(1, 7, -1)) {
            case 0:
                word = "puff(s)";
                break;
            case 1:
                word = "boff(s)";
                break;
            case 2:
                word = "poof(s)";
                break;
            case 3:
                word = "bambam(s)";
                break;
            case 4:
                word = "whack(s)";
                break;
            case 5:
                word = "smoosh(es)";
                break;
            case 6:
                word = "kabloom(s)";
                break;
        }
        final boolean showDamn = CMProps.getVar(CMProps.Str.SHOWDAMAGE).equalsIgnoreCase("YES");
        switch (weaponClassification()) {
            case Weapon.CLASS_RANGED:
                return "<S-NAME> fire(s) " + name() + " at <T-NAMESELF> and " + word + ((showDamn) ? " (" + damageAmount + ")" : "") + " <T-HIM-HER>." + CMLib.protocol().msp("arrow.wav", 20);
            case Weapon.CLASS_THROWN:
                return "<S-NAME> throw(s) " + name() + " at <T-NAMESELF> and " + word + ((showDamn) ? " (" + damageAmount + ")" : "") + " <T-HIM-HER>." + CMLib.protocol().msp("arrow.wav", 20);
            default:
                return "<S-NAME> " + word + ((showDamn) ? " (" + damageAmount + ")" : "") + " <T-NAMESELF> with " + name() + "." + CMLib.protocol().msp("punch" + CMLib.dice().roll(1, 7, 0) + ".wav", 20);
        }
    }
}
