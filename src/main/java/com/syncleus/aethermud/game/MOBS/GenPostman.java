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
package com.syncleus.aethermud.game.MOBS;

import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenPostman extends StdPostman {
    private final static String[] MYCODES = {"WHATISELL",
        "PREJUDICE",
        "POSTCHAIN", "POSTMIN", "POSTLBS",
        "POSTHOLD", "POSTNEW", "POSTHELD",
        "IGNOREMASK", "PRICEMASKS"};
    private static String[] codes = null;
    private String PrejudiceFactors = "";
    private String postalChain = "main";
    private String IgnoreMask = "";

    public GenPostman() {
        super();
        username = "a generic postman";
        setDescription("He looks bored and slow.");
        setDisplayText("A generic postman stands here.");
    }

    @Override
    public String ID() {
        return "GenPostman";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public String text() {
        if (CMProps.getBoolVar(CMProps.Bool.MOBCOMPRESS))
            miscText = CMLib.encoder().compressString(CMLib.aetherMaker().getPropertiesStr(this, false));
        else
            miscText = CMLib.aetherMaker().getPropertiesStr(this, false);
        return super.text();
    }

    @Override
    public String prejudiceFactors() {
        return PrejudiceFactors;
    }

    @Override
    public void setPrejudiceFactors(String factors) {
        PrejudiceFactors = factors;
    }

    @Override
    public String ignoreMask() {
        return IgnoreMask;
    }

    @Override
    public void setIgnoreMask(String factors) {
        IgnoreMask = factors;
    }

    @Override
    public String postalChain() {
        return postalChain;
    }

    @Override
    public void setPostalChain(String name) {
        postalChain = name;
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        CMLib.aetherMaker().resetGenMOB(this, newText);
    }

    @Override
    public String getStat(String code) {
        if (CMLib.aetherMaker().getGenMobCodeNum(code) >= 0)
            return CMLib.aetherMaker().getGenMobStat(this, code);
        switch (getCodeNum(code)) {
            case 0:
                return "" + getWhatIsSoldMask();
            case 1:
                return prejudiceFactors();
            case 2:
                return postalChain();
            case 3:
                return "" + minimumPostage();
            case 4:
                return "" + postagePerPound();
            case 5:
                return "" + holdFeePerPound();
            case 6:
                return "" + feeForNewBox();
            case 7:
                return "" + maxMudMonthsHeld();
            case 8:
                return ignoreMask();
            case 9:
                return CMParms.toListString(itemPricingAdjustments());
            default:
                return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
        }
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.aetherMaker().getGenMobCodeNum(code) >= 0)
            CMLib.aetherMaker().setGenMobStat(this, code, val);
        else
            switch (getCodeNum(code)) {
                case 0:
                    setWhatIsSoldMask(CMath.s_long(val));
                    break;
                case 1:
                    setPrejudiceFactors(val);
                    break;
                case 2:
                    setPostalChain(val);
                    break;
                case 3:
                    setMinimumPostage(CMath.s_parseMathExpression(val));
                    break;
                case 4:
                    setPostagePerPound(CMath.s_parseMathExpression(val));
                    break;
                case 5:
                    setHoldFeePerPound(CMath.s_parseMathExpression(val));
                    break;
                case 6:
                    setFeeForNewBox(CMath.s_parseMathExpression(val));
                    break;
                case 7:
                    setMaxMudMonthsHeld(CMath.s_parseIntExpression(val));
                    break;
                case 8:
                    setIgnoreMask(val);
                    break;
                case 9:
                    setItemPricingAdjustments((val.trim().length() == 0) ? new String[0] : CMParms.toStringArray(CMParms.parseCommas(val, true)));
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
        final String[] MYCODES = CMProps.getStatCodesList(GenPostman.MYCODES, this);
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
        if (!(E instanceof GenPostman))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}
