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
package com.syncleus.aethermud.game.Items.Weapons;

import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenNet extends StdNet {
    private static String[] codes = null;
    protected String readableText = "";

    public GenNet() {
        super();

        setName("a generic net");
        setDisplayText("a generic net sits here.");
        setDescription("");
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenNet";
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
        if (GenWeapon.getGenWeaponCodeNum(code) >= 0)
            return GenWeapon.getGenWeaponStat(this, code);
        return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.aetherMaker().getGenItemCodeNum(code) >= 0)
            CMLib.aetherMaker().setGenItemStat(this, code, val);
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
        if (!(E instanceof GenNet))
            return false;
        final String[] codes = getStatCodes();
        for (int i = 0; i < codes.length; i++) {
            if (!E.getStat(codes[i]).equals(getStat(codes[i])))
                return false;
        }
        return true;
    }
}
