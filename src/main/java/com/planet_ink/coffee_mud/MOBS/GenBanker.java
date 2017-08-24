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
package com.planet_ink.coffee_mud.MOBS;

import com.planet_ink.coffee_mud.Libraries.interfaces.GenericBuilder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.ShopKeeper;


public class GenBanker extends StdBanker {
    private final static String[] MYCODES = {"WHATISELL", "PREJUDICE", "BANKCHAIN", "COININT", "ITEMINT", "IGNOREMASK", "LOANINT", "PRICEMASKS"};
    private static String[] codes = null;
    protected String PrejudiceFactors = "";
    protected String bankChain = "GenBank";
    private String IgnoreMask = "";

    public GenBanker() {
        super();
        username = "a generic banker";
        setDescription("He looks like he wants your money.");
        setDisplayText("A generic banker stands here.");
        basePhyStats().setAbility(CMProps.getMobHPBase()); // his only off-default
    }

    @Override
    public String ID() {
        return "GenBanker";
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
    public String bankChain() {
        return bankChain;
    }

    @Override
    public void setBankChain(String name) {
        bankChain = name;
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
                return "" + getWhatIsSoldMask();
            case 1:
                return prejudiceFactors();
            case 2:
                return bankChain();
            case 3:
                return "" + getCoinInterest();
            case 4:
                return "" + getItemInterest();
            case 5:
                return ignoreMask();
            case 6:
                return "" + getLoanInterest();
            case 7:
                return CMParms.toListString(itemPricingAdjustments());
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
                case 0: {
                    if ((val.length() == 0) || (CMath.isLong(val)))
                        setWhatIsSoldMask(CMath.s_long(val));
                    else if (CMParms.containsIgnoreCase(ShopKeeper.DEAL_DESCS, val))
                        setWhatIsSoldMask(CMParms.indexOfIgnoreCase(ShopKeeper.DEAL_DESCS, val));
                    break;
                }
                case 1:
                    setPrejudiceFactors(val);
                    break;
                case 2:
                    setBankChain(val);
                    break;
                case 3:
                    setCoinInterest(CMath.s_double(val));
                    break;
                case 4:
                    setItemInterest(CMath.s_double(val));
                    break;
                case 5:
                    setIgnoreMask(val);
                    break;
                case 6:
                    setLoanInterest(CMath.s_double(val));
                    break;
                case 7:
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
        final String[] MYCODES = CMProps.getStatCodesList(GenBanker.MYCODES, this);
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
        if (!(E instanceof GenBanker))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}
