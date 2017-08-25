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
package com.planet_ink.game.MOBS;

import com.planet_ink.game.Libraries.interfaces.GenericBuilder;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Environmental;


public class GenDeity extends StdDeity {
    private final static String[] MYCODES = {"CLERREQ", "CLERRIT", "WORREQ", "WORRIT", "SVCRIT"};
    private static String[] codes = null;

    public GenDeity() {
        super();
        username = "a generic deity";
        setDescription("He is a run-of-the-mill deity.");
        setDisplayText("A generic deity stands here.");
        basePhyStats().setAbility(CMProps.getMobHPBase()); // his only off-default
        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "GenDeity";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public String text() {
        if (CMProps.getBoolVar(CMProps.Bool.MOBCOMPRESS))
            miscText = CMLib.encoder().compressString(CMLib.coffeeMaker().getPropertiesStr(this, false));
        else
            miscText = CMLib.coffeeMaker().getPropertiesStr(this, false);
        return super.text();
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        CMLib.coffeeMaker().resetGenMOB(this, newText);
    }

    @Override
    public String getStat(String code) {
        if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
            return CMLib.coffeeMaker().getGenMobStat(this, code);
        switch (getCodeNum(code)) {
            case 0:
                return getClericRequirements();
            case 1:
                return getClericRitual();
            case 2:
                return getWorshipRequirements();
            case 3:
                return getWorshipRitual();
            case 4:
                return getServiceRitual();
            default:
                return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
        }
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
            CMLib.coffeeMaker().setGenMobStat(this, code, val);
        else
            switch (getCodeNum(code)) {
                case 0:
                    setClericRequirements(val);
                    break;
                case 1:
                    setClericRitual(val);
                    break;
                case 2:
                    setWorshipRequirements(val);
                    break;
                case 3:
                    setWorshipRitual(val);
                    break;
                case 4:
                    setServiceRitual(val);
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
        final String[] MYCODES = CMProps.getStatCodesList(GenDeity.MYCODES, this);
        final String[] superCodes = CMParms.toStringArray(GenericBuilder.GenMOBCode.values());
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
        if (!(E instanceof GenDeity))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}
