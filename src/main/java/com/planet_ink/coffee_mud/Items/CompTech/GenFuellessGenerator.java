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
package com.planet_ink.coffee_mud.Items.CompTech;

import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class GenFuellessGenerator extends StdCompFuellessGenerator {
    private final static String[] MYCODES = {"POWERCAP", "POWERREM", "GENAMTPER", "ACTIVATED", "MANUFACTURER", "INSTFACT", "RECHRATE"};
    private static String[] codes = null;
    protected String readableText = "";

    public GenFuellessGenerator() {
        super();
        setName("a power generator");
        setDisplayText("a power generator sits here.");
        setDescription("");
    }

    @Override
    public String ID() {
        return "GenFuellessGenerator";
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
        switch (getCodeNum(code)) {
            case 0:
                return "" + powerCapacity();
            case 1:
                return "" + powerRemaining();
            case 2:
                return "" + getGeneratedAmountPerTick();
            case 3:
                return "" + activated();
            case 4:
                return "" + getManufacturerName();
            case 5:
                return "" + getInstalledFactor();
            case 6:
                return "" + getRechargeRate();
            default:
                return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
        }
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
            CMLib.coffeeMaker().setGenItemStat(this, code, val);
        else
            switch (getCodeNum(code)) {
                case 0:
                    setPowerCapacity(CMath.s_parseLongExpression(val));
                    break;
                case 1:
                    setPowerCapacity(CMath.s_parseLongExpression(val));
                    break;
                case 2:
                    setGeneratedAmountPerTick(CMath.s_parseIntExpression(val));
                    break;
                case 3:
                    activate(CMath.s_bool(val));
                    break;
                case 4:
                    setManufacturerName(val);
                    break;
                case 5:
                    setInstalledFactor((float) CMath.s_parseMathExpression(val));
                    break;
                case 6:
                    setRechargeRate(CMath.s_parseLongExpression(val));
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
        final String[] MYCODES = CMProps.getStatCodesList(GenFuellessGenerator.MYCODES, this);
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
        if (!(E instanceof GenFuellessGenerator))
            return false;
        final String[] theCodes = getStatCodes();
        for (int i = 0; i < theCodes.length; i++) {
            if (!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
                return false;
        }
        return true;
    }
}
