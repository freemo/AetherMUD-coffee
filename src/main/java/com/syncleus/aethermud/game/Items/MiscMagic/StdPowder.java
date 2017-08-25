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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.Basic.StdItem;
import com.syncleus.aethermud.game.Items.interfaces.MagicDust;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.SpellHolder;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;

/**
 * Title: False Realities Flavored AetherMUD
 * Description: The False Realities Version of AetherMUD
 * Copyright: Copyright (c) 2004 Jeremy Vyska
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Company: http://www.falserealities.com
 * @author FR - Jeremy Vyska; CM - Bo Zimmerman
 * @version 1.0.0.0
 */

public class StdPowder extends StdItem implements MagicDust {
    public StdPowder() {
        super();

        setName("a pile of powder");
        basePhyStats.setWeight(1);
        setDisplayText("A small pile of powder sits here.");
        setDescription("A small pile of powder.");
        secretIdentity = "This is a pile of inert materials.";
        baseGoldValue = 0;
        material = RawMaterial.RESOURCE_ASH;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdPowder";
    }

    @Override
    public void spreadIfAble(MOB mob, Physical target) {
        final List<Ability> spells = getSpells();
        if (spells.size() > 0) {
            for (int i = 0; i < spells.size(); i++) {
                final Ability thisOne = (Ability) spells.get(i).copyOf();
                if (thisOne.canTarget(target)) {
                    if ((malicious(this)) || (!(target instanceof MOB)))
                        thisOne.invoke(mob, target, true, phyStats().level());
                    else
                        thisOne.invoke((MOB) target, (MOB) target, true, phyStats().level());
                }
            }
        }
        destroy();
    }

    // That which makes Powders work.  They're an item that when successfully dusted on a target, are 'cast' on the target
    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.sourceMinor() == CMMsg.TYP_THROW) {
            if ((msg.tool() == this) && (msg.target() instanceof Physical))
                spreadIfAble(msg.source(), (Physical) msg.target());
            else
                super.executeMsg(myHost, msg);
        } else
            super.executeMsg(myHost, msg);
    }

    @Override
    public String getSpellList() {
        return miscText;
    }

    @Override
    public void setSpellList(String list) {
        miscText = list;
    }

    public boolean malicious(SpellHolder me) {
        final List<Ability> spells = getSpells();
        for (final Ability checking : spells) {
            if (checking.abstractQuality() == Ability.QUALITY_MALICIOUS)
                return true;
        }
        return false;
    }

    @Override
    public List<Ability> getSpells() {
        final String names = getSpellList();

        final List<Ability> theSpells = new Vector<Ability>();
        final List<String> parsedSpells = CMParms.parseSemicolons(names, true);
        for (String thisOne : parsedSpells) {
            thisOne = thisOne.trim();
            String parms = "";
            final int x = thisOne.indexOf('(');
            if ((x > 0) && (thisOne.endsWith(")"))) {
                parms = thisOne.substring(x + 1, thisOne.length() - 1);
                thisOne = thisOne.substring(0, x).trim();
            }
            Ability A = CMClass.getAbility(thisOne);
            if ((A != null) && ((A.classificationCode() & Ability.ALL_DOMAINS) != Ability.DOMAIN_ARCHON)) {
                A = (Ability) A.copyOf();
                A.setMiscText(parms);
                theSpells.add(A);
            }
        }
        recoverPhyStats();
        return theSpells;
    }

    @Override
    public String secretIdentity() {
        return description() + "\n\r" + super.secretIdentity();
    }
}
