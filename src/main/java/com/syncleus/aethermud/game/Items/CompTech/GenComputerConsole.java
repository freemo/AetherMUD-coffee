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

import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Rideable;


public class GenComputerConsole extends StdComputerConsole {
    private final static String[] MYCODES = {"HASLOCK", "HASLID", "CAPACITY", "CONTAINTYPES", "RESETTIME", "RIDEBASIS", "MOBSHELD",
        "POWERCAP", "ACTIVATED", "POWERREM", "MANUFACTURER", "INSTFACT", "DEFCLOSED", "DEFLOCKED",
        "RECHRATE"};
    private static String[] codes = null;

    public GenComputerConsole() {
        super();
    }

    @Override
    public String ID() {
        return "GenComputerConsole";
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
    public void setReadableText(String text) {
    }

    @Override
    public void setMiscText(String newText) {
        miscText = "";
        CMLib.aetherMaker().setPropertiesStr(this, newText, false);
        basePhyStats.setSensesMask(basePhyStats.sensesMask() | PhyStats.SENSE_ITEMREADABLE);
        recoverPhyStats();
    }

    @Override
    public String getStat(String code) {
        if (CMLib.aetherMaker().getGenItemCodeNum(code) >= 0)
            return CMLib.aetherMaker().getGenItemStat(this, code);
        switch (getCodeNum(code)) {
            case 0:
                return "" + hasALock();
            case 1:
                return "" + hasADoor();
            case 2:
                return "" + capacity();
            case 3:
                return "" + containTypes();
            case 4:
                return "" + openDelayTicks();
            case 5:
                return "" + rideBasis();
            case 6:
                return "" + riderCapacity();
            case 7:
                return "" + powerCapacity();
            case 8:
                return "" + activated();
            case 9:
                return "" + powerRemaining();
            case 10:
                return "" + getManufacturerName();
            case 11:
                return "" + getInstalledFactor();
            case 12:
                return "" + defaultsClosed();
            case 13:
                return "" + defaultsLocked();
            case 14:
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
                    setDoorsNLocks(hasADoor(), isOpen(), defaultsClosed(), CMath.s_bool(val), false, CMath.s_bool(val) && defaultsLocked());
                    break;
                case 1:
                    setDoorsNLocks(CMath.s_bool(val), isOpen(), CMath.s_bool(val) && defaultsClosed(), hasALock(), isLocked(), defaultsLocked());
                    break;
                case 2:
                    setCapacity(CMath.s_parseIntExpression(val));
                    break;
                case 3:
                    setContainTypes(CMath.s_parseBitLongExpression(Container.CONTAIN_DESCS, val));
                    break;
                case 4:
                    setOpenDelayTicks(CMath.s_parseIntExpression(val));
                    break;
                case 5:
                    setRideBasis(CMath.s_parseListIntExpression(Rideable.RIDEABLE_DESCS, val));
                    break;
                case 6:
                    setRiderCapacity(CMath.s_parseIntExpression(val));
                    break;
                case 7:
                    setPowerCapacity(CMath.s_parseLongExpression(val));
                    break;
                case 8:
                    activate(CMath.s_bool(val));
                    break;
                case 9:
                    setPowerRemaining(CMath.s_parseLongExpression(val));
                    break;
                case 10:
                    setManufacturerName(val);
                    break;
                case 11:
                    setInstalledFactor((float) CMath.s_parseMathExpression(val));
                    break;
                case 12:
                    setDoorsNLocks(hasADoor(), isOpen(), CMath.s_bool(val), hasALock(), isLocked(), defaultsLocked());
                    break;
                case 13:
                    setDoorsNLocks(hasADoor(), isOpen(), defaultsClosed(), hasALock(), isLocked(), CMath.s_bool(val));
                    break;
                case 14:
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
        final String[] MYCODES = CMProps.getStatCodesList(GenComputerConsole.MYCODES, this);
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
        if (!(E instanceof GenComputerConsole))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}
