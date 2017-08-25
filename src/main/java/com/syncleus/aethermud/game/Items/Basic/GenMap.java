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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenMap extends StdMap {
    private static String[] codes = null;
    protected String readableText = "";

    public GenMap() {
        super();
        setName("a generic map");
        basePhyStats.setWeight(1);
        setDisplayText("a generic map sits here.");
        setDescription("");
        baseGoldValue = 5;
        setMaterial(RawMaterial.RESOURCE_PAPER);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenMap";
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
    public String getMapArea() {
        return readableText;
    }

    @Override
    public void setMapArea(String mapName) {
        setReadableText(mapName);
    }

    @Override
    public void setReadableText(String newReadableText) {
        final String oldName = Name();
        final String oldDesc = description();
        readableText = newReadableText;
        doMapArea();
        setName(oldName);
        setDescription(oldDesc);
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
        return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
            CMLib.coffeeMaker().setGenItemStat(this, code, val);
        CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
    }

    @Override
    public String[] getStatCodes() {
        if (codes == null)
            codes = CMProps.getStatCodesList(CMParms.toStringArray(GenericBuilder.GenItemCode.values()), this);
        return codes;
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof GenMap))
            return false;
        for (int i = 0; i < getStatCodes().length; i++) {
            if (!E.getStat(getStatCodes()[i]).equals(getStat(getStatCodes()[i])))
                return false;
        }
        return true;
    }
}
