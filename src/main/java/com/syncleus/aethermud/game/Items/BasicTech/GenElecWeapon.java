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
package com.syncleus.aethermud.game.Items.BasicTech;

import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenElecWeapon extends StdElecWeapon {
    private final static String[] MYCODES = {"MINRANGE", "MAXRANGE", "WEAPONTYPE", "WEAPONCLASS",
        "POWERCAP", "ACTIVATED", "POWERREM", "MANUFACTURER", "TECHLEVEL"};
    private static String[] codes = null;
    protected String readableText = "";

    public GenElecWeapon() {
        super();
    }

    @Override
    public String ID() {
        return "GenElecWeapon";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public String text() {
        return CMLib.aetherMaker().getPropertiesStr(this, false);
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
        CMLib.aetherMaker().setPropertiesStr(this, newText, false);
        recoverPhyStats();
    }

    @Override
    public String getStat(String code) {
        if (CMLib.aetherMaker().getGenItemCodeNum(code) >= 0)
            return CMLib.aetherMaker().getGenItemStat(this, code);
        switch (getCodeNum(code)) {
            case 0:
                return "" + minRange();
            case 1:
                return "" + maxRange();
            case 2:
                return "" + weaponDamageType();
            case 3:
                return "" + weaponClassification();
            case 4:
                return "" + powerCapacity();
            case 5:
                return "" + activated();
            case 6:
                return "" + powerRemaining();
            case 7:
                return "" + getManufacturerName();
            case 8:
                return "" + techLevel();
            default:
                return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
        }
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.aetherMaker().getGenItemCodeNum(code) >= 0)
            CMLib.aetherMaker().setGenItemStat(this, code, val);
        else
            switch (getCodeNum(code)) {
                case 0:
                    setRanges(CMath.s_parseIntExpression(val), maxRange());
                    break;
                case 1:
                    setRanges(minRange(), CMath.s_parseIntExpression(val));
                    break;
                case 2:
                    setWeaponDamageType(CMath.s_parseListIntExpression(Weapon.TYPE_DESCS, val));
                    break;
                case 3:
                    setWeaponClassification(CMath.s_parseListIntExpression(Weapon.CLASS_DESCS, val));
                    break;
                case 4:
                    setPowerCapacity(CMath.s_parseLongExpression(val));
                    break;
                case 5:
                    activate(CMath.s_bool(val));
                    break;
                case 6:
                    setPowerRemaining(CMath.s_parseLongExpression(val));
                    break;
                case 7:
                    setManufacturerName(val);
                    break;
                case 8:
                    setTechLevel(CMath.s_parseIntExpression(val));
                    break;
                default:
                    CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
                    break;
            }
    }

    @Override
    protected int getCodeNum(String code) {
        for (int i = 0; i < MYCODES.length; i++) {
            if (code.equalsIgnoreCase(MYCODES[i]))
                return i;
        }
        return -1;
    }

    @Override
    public String[] getStatCodes() {
        if (codes != null)
            return codes;
        final String[] MYCODES = CMProps.getStatCodesList(GenElecWeapon.MYCODES, this);
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
        if (!(E instanceof GenElecWeapon))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}

