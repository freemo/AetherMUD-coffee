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
package com.syncleus.aethermud.game.Items.MiscMagic;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Libraries.interfaces.GenericBuilder;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenWand extends StdWand {
    private static String[] codes = null;
    protected String readableText = "";
    protected int maxUses = Integer.MAX_VALUE;

    public GenWand() {
        super();

        setName("a wand");
        setDisplayText("a simple wand is here.");
        setDescription("A wand made out of wood.");
        secretIdentity = null;
        setUsesRemaining(0);
        baseGoldValue = 20000;
        basePhyStats().setLevel(12);
        CMLib.flags().setReadable(this, false);
        material = RawMaterial.RESOURCE_OAK;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenWand";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public Ability getSpell() {
        return CMClass.getAbility(readableText);
    }

    @Override
    public void setSpell(Ability theSpell) {
        readableText = "";
        if (theSpell != null)
            readableText = theSpell.ID();
        secretWord = StdWand.getWandWord(readableText);
    }

    @Override
    public String readableText() {
        return readableText;
    }

    @Override
    public void setReadableText(String text) {
        readableText = text;
        secretWord = StdWand.getWandWord(readableText);
    }

    @Override
    public int maxUses() {
        return maxUses;
    }

    @Override
    public void setMaxUses(int newMaxUses) {
        maxUses = newMaxUses;
    }

    @Override
    public String text() {
        return CMLib.coffeeMaker().getPropertiesStr(this, false);
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
        if (!(E instanceof GenWand))
            return false;
        for (int i = 0; i < getStatCodes().length; i++) {
            if (!E.getStat(getStatCodes()[i]).equals(getStat(getStatCodes()[i])))
                return false;
        }
        return true;
    }
}
