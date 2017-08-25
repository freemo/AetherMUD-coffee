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
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenMob extends StdMOB {
    private static String[] codes = null;

    public GenMob() {
        super();
        username = "a generic mob";
        setDescription("");
        setDisplayText("A generic mob stands here.");

        basePhyStats().setAbility(CMProps.getMobHPBase()); // his only off-default

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "GenMob";
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
        return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
    }

    @Override
    public void setStat(String code, String val) {
        if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
            CMLib.coffeeMaker().setGenMobStat(this, code, val);
        CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
    }

    @Override
    public String[] getStatCodes() {
        if (codes == null)
            codes = CMProps.getStatCodesList(CMParms.toStringArray(GenericBuilder.GenMOBCode.values()), this);
        return codes;
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof GenMob))
            return false;
        final String[] theCodes = getStatCodes();
        for (int i = 0; i < theCodes.length; i++) {
            if (!E.getStat(theCodes[i]).equals(getStat(theCodes[i])))
                return false;
        }
        return true;
    }
}
