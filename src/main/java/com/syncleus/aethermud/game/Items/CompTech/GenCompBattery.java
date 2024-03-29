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
package com.syncleus.aethermud.game.Items.CompTech;

import com.syncleus.aethermud.game.Items.interfaces.PowerSource;
import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenCompBattery extends StdCompBattery implements PowerSource {
    private final static String[] MYCODES = {"POWERCAP", "ACTIVATED", "POWERREM", "MANUFACTURER", "INSTFACT", "RECHRATE"};
    private static String[] codes = null;

    public GenCompBattery() {
        super();
        setName("a generic battery");
        setDisplayText("a generic battery sits here.");
        setDescription("");
        super.setPowerCapacity(1000);
        super.setPowerRemaining(1000);
    }

    @Override
    public String ID() {
        return "GenCompBattery";
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
                return "" + powerCapacity();
            case 1:
                return "" + activated();
            case 2:
                return "" + powerRemaining();
            case 3:
                return "" + getManufacturerName();
            case 4:
                return "" + getInstalledFactor();
            case 5:
                return "" + getRechargeRate();
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
                    setPowerCapacity(CMath.s_parseLongExpression(val));
                    break;
                case 1:
                    activate(CMath.s_bool(val));
                    break;
                case 2:
                    setPowerRemaining(CMath.s_parseLongExpression(val));
                    break;
                case 3:
                    setManufacturerName(val);
                    break;
                case 4:
                    setInstalledFactor((float) CMath.s_parseMathExpression(val));
                    break;
                case 5:
                    setRechargeRate((float) CMath.s_parseMathExpression(val));
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
        final String[] MYCODES = CMProps.getStatCodesList(GenCompBattery.MYCODES, this);
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
        if (!(E instanceof GenCompBattery))
            return false;
        final String[] theCodes = getStatCodes();
        for (int i = 0; i < theCodes.length; i++) {
            if (!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
                return false;
        }
        return true;
    }
}
