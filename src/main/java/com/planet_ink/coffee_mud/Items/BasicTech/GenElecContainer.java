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
package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class GenElecContainer extends StdElecContainer {
    private final static String[] MYCODES = {"TECHLEVEL", "HASLOCK", "HASLID", "CAPACITY", "CONTAINTYPES", "RESETTIME", "POWERCAP", "ACTIVATED", "POWERREM", "MANUFACTURER", "DEFCLOSED", "DEFLOCKED"};
    private static String[] codes = null;
    protected String readableText = "";

    public GenElecContainer() {
        super();
        setName("a generic electric container");
        basePhyStats.setWeight(2);
        setDisplayText("a generic electric container sits here.");
        setDescription("");
        baseGoldValue = 5;
        basePhyStats().setLevel(1);
        recoverPhyStats();
        setMaterial(RawMaterial.RESOURCE_STEEL);
    }

    @Override
    public String ID() {
        return "GenElecContainer";
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
                return "" + techLevel();
            case 1:
                return "" + hasALock();
            case 2:
                return "" + hasADoor();
            case 3:
                return "" + capacity();
            case 4:
                return "" + containTypes();
            case 5:
                return "" + openDelayTicks();
            case 6:
                return "" + powerCapacity();
            case 7:
                return "" + activated();
            case 8:
                return "" + powerRemaining();
            case 9:
                return "" + getManufacturerName();
            case 10:
                return "" + defaultsClosed();
            case 11:
                return "" + defaultsLocked();
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
                    setTechLevel(CMath.s_parseIntExpression(val));
                    break;
                case 1:
                    setDoorsNLocks(hasADoor(), isOpen(), defaultsClosed(), CMath.s_bool(val), false, CMath.s_bool(val) && defaultsLocked());
                    break;
                case 2:
                    setDoorsNLocks(CMath.s_bool(val), isOpen(), CMath.s_bool(val) && defaultsClosed(), hasALock(), isLocked(), defaultsLocked());
                    break;
                case 3:
                    setCapacity(CMath.s_parseIntExpression(val));
                    break;
                case 4:
                    setContainTypes(CMath.s_parseBitLongExpression(Container.CONTAIN_DESCS, val));
                    break;
                case 5:
                    setOpenDelayTicks(CMath.s_parseIntExpression(val));
                    break;
                case 6:
                    setPowerCapacity(CMath.s_parseLongExpression(val));
                    break;
                case 7:
                    activate(CMath.s_bool(val));
                    break;
                case 8:
                    setPowerRemaining(CMath.s_parseLongExpression(val));
                    break;
                case 9:
                    setManufacturerName(val);
                    break;
                case 10:
                    setDoorsNLocks(hasADoor(), isOpen(), CMath.s_bool(val), hasALock(), isLocked(), defaultsLocked());
                    break;
                case 11:
                    setDoorsNLocks(hasADoor(), isOpen(), defaultsClosed(), hasALock(), isLocked(), CMath.s_bool(val));
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
        final String[] MYCODES = CMProps.getStatCodesList(GenElecContainer.MYCODES, this);
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
        if (!(E instanceof GenElecContainer))
            return false;
        final String[] theCodes = getStatCodes();
        for (int i = 0; i < theCodes.length; i++) {
            if (!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
                return false;
        }
        return true;
    }
}
