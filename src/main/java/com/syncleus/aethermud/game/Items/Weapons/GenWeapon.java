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

import com.planet_ink.game.Items.interfaces.AmmunitionWeapon;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.Libraries.interfaces.GenericBuilder;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class GenWeapon extends StdWeapon {
    protected final static String[] GENWEAPONCODES = {"MINRANGE", "MAXRANGE", "WEAPONTYPE", "WEAPONCLASS", "AMMOTYPE", "AMMOCAPACITY"};
    private static String[] codes = null;
    protected String readableText = "";

    public GenWeapon() {
        super();

        setName("a generic weapon");
        basePhyStats.setWeight(2);
        setDisplayText("a generic weapon sits here.");
        setDescription("");
        baseGoldValue = 5;
        properWornBitmap = Wearable.WORN_WIELD | Wearable.WORN_HELD;
        wornLogicalAnd = false;
        weaponDamageType = Weapon.TYPE_BASHING;
        material = RawMaterial.RESOURCE_STEEL;
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(5);
        basePhyStats().setLevel(5);
        recoverPhyStats();
    }

    protected static int getGenWeaponCodeNum(String code) {
        for (int i = 0; i < GENWEAPONCODES.length; i++) {
            if (code.equalsIgnoreCase(GENWEAPONCODES[i]))
                return i;
        }
        return -1;
    }

    protected static String getGenWeaponStat(AmmunitionWeapon W, String code) {
        switch (getGenWeaponCodeNum(code)) {
            case 0:
                return "" + W.minRange();
            case 1:
                return "" + W.maxRange();
            case 2:
                return "" + W.weaponDamageType();
            case 3:
                return "" + W.weaponClassification();
            case 4:
                return W.ammunitionType();
            case 5:
                return "" + W.ammunitionCapacity();
            default:
                return "";
        }
    }

    public static void setGenWeaponStat(AmmunitionWeapon W, String code, String val) {
        switch (getGenWeaponCodeNum(code)) {
            case 0:
                W.setRanges(CMath.s_parseIntExpression(val), W.maxRange());
                break;
            case 1:
                W.setRanges(W.minRange(), CMath.s_parseIntExpression(val));
                break;
            case 2:
                W.setWeaponDamageType(CMath.s_parseListIntExpression(Weapon.TYPE_DESCS, val));
                break;
            case 3:
                W.setWeaponClassification(CMath.s_parseListIntExpression(Weapon.CLASS_DESCS, val));
                break;
            case 4:
                W.setAmmunitionType(val);
                break;
            case 5:
                W.setAmmoCapacity(CMath.s_parseIntExpression(val));
                break;
        }
    }

    @Override
    public String ID() {
        return "GenWeapon";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public String text() {
        return CMLib.coffeeMaker().getPropertiesStr(this, false);
    }

    @Override
    public String readableText() {
        return readableText;
    }

    @Override
    public void setReadableText(String text) {
        readableText = text;
    }

    @Override
    public void setMiscText(String newText) {
        miscText = "";
        CMLib.coffeeMaker().setPropertiesStr(this, newText, false);
        recoverPhyStats();
    }

    @Override
    public String getStat(String code) {
        if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
            return CMLib.coffeeMaker().getGenItemStat(this, code);
        if (GenWeapon.getGenWeaponCodeNum(code) >= 0)
            return GenWeapon.getGenWeaponStat(this, code);
        return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
            CMLib.coffeeMaker().setGenItemStat(this, code, val);
        else if (GenWeapon.getGenWeaponCodeNum(code) >= 0)
            GenWeapon.setGenWeaponStat(this, code, val);
        else
            CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
    }

    @Override
    protected int getCodeNum(String code) {
        return GenWeapon.getGenWeaponCodeNum(code);
    }

    @Override
    public String[] getStatCodes() {
        if (codes != null)
            return codes;
        final String[] MYCODES = CMProps.getStatCodesList(GenWeapon.GENWEAPONCODES, this);
        final String[] superCodes = CMParms.toStringArray(GenericBuilder.GenItemCode.values());
        codes = new String[superCodes.length + MYCODES.length];
        int i = 0;
        for (; i < superCodes.length; i++)
            codes[i] = superCodes[i];
        for (int x = 0; x < MYCODES.length; i++, x++)
            codes[i] = MYCODES[x];
        return codes;
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof GenWeapon))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}

