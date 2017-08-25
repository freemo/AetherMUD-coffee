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
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.ShopKeeper;

/*
   Copyright 2001-2017 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class GenShopkeeper extends StdShopKeeper {
    private final static String[] MYCODES = {"WHATISELL", "PREJUDICE", "BUDGET", "DEVALRATE", "INVRESETRATE", "IGNOREMASK", "PRICEMASKS"};
    private static String[] codes = null;
    protected String prejudiceFactors = "";
    private String ignoreMask = "";

    public GenShopkeeper() {
        super();
        username = "a generic shopkeeper";
        setDescription("He looks like he wants to sell something to you.");
        setDisplayText("A generic shopkeeper stands here.");
        basePhyStats().setAbility(CMProps.getMobHPBase()); // his only off-default
    }

    @Override
    public String ID() {
        return "GenShopkeeper";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public String prejudiceFactors() {
        return prejudiceFactors;
    }

    @Override
    public void setPrejudiceFactors(String factors) {
        prejudiceFactors = factors;
    }

    @Override
    public String ignoreMask() {
        return ignoreMask;
    }

    @Override
    public void setIgnoreMask(String factors) {
        ignoreMask = factors;
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
                return "" + getWhatIsSoldMask();
            case 1:
                return prejudiceFactors();
            case 2:
                return budget();
            case 3:
                return devalueRate();
            case 4:
                return "" + invResetRate();
            case 5:
                return ignoreMask();
            case 6:
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
                    setBudget(val);
                    break;
                case 3:
                    setDevalueRate(val);
                    break;
                case 4:
                    setInvResetRate(CMath.s_parseIntExpression(val));
                    break;
                case 5:
                    setIgnoreMask(val);
                    break;
                case 6:
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
        final String[] MYCODES = CMProps.getStatCodesList(GenShopkeeper.MYCODES, this);
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
        if (!(E instanceof GenShopkeeper))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}
