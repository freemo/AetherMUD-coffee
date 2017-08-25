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
package com.syncleus.aethermud.game.Items.ClanItems;

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.ClanItem;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

/*
   Copyright 2004-2017 Bo Zimmerman

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
public class StdClanSpecialItem extends StdClanItem {
    private Behavior B = null;
    private String flag = "";
    public StdClanSpecialItem() {
        super();

        setName("a clan item");
        basePhyStats.setWeight(1);
        setDisplayText("an item belonging to a clan is here.");
        setDescription("");
        secretIdentity = "";
        baseGoldValue = 1;
        setClanItemType(ClanItem.ClanItemType.SPECIALOTHER);
        material = RawMaterial.RESOURCE_PINE;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdClanSpecialItem";
    }

    @Override
    public void setReadableText(String text) {
        if ((text.equalsIgnoreCase("GOOD")) || (text.equalsIgnoreCase("EVIL")))
            flag = text;
        else
            super.setReadableText(text);
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        if (((getClanItemType() == ClanItem.ClanItemType.SPECIALSCALES)
            || (getClanItemType() == ClanItem.ClanItemType.SPECIALTAXER))
            && (owner() instanceof MOB)
            && (clanID().length() > 0)
            && (((MOB) owner()).isMonster())
            && ((((MOB) owner()).getClanRole(clanID()) != null)
            && (CMLib.flags().isAliveAwakeMobile((MOB) owner(), true))
            && (!CMLib.flags().isAnimalIntelligence((MOB) owner())))
            && (B != null))
            B.executeMsg(owner(), msg);
    }

    @Override
    public boolean okMessage(Environmental affecting, CMMsg msg) {
        if (!super.okMessage(affecting, msg))
            return false;
        if ((getClanItemType() == ClanItem.ClanItemType.SPECIALTAXER) && (B != null) && (owner() instanceof MOB))
            return B.okMessage(owner(), msg);
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((tickID == Tickable.TICKID_CLANITEM)
            && (owner() instanceof MOB)
            && (clanID().length() > 0)
            && (((MOB) owner()).isMonster())
            && (((MOB) owner()).getClanRole(clanID()) != null)
            && (CMLib.flags().isAliveAwakeMobileUnbound((MOB) owner(), true))
            && (!CMLib.flags().isAnimalIntelligence((MOB) owner()))) {
            switch (getClanItemType()) {
                case SPECIALSCAVENGER: {
                    final MOB mob = (MOB) owner();
                    final Room R = ((MOB) owner()).location();
                    if (R != null) {
                        final Item I = R.getRandomItem();
                        if ((I != null) && (I.container() == null))
                            CMLib.commands().postGet(mob, null, I, false);
                    }
                    break;
                }
                case SPECIALSCALES: {
                    if (((B == null))
                        || (!flag.equalsIgnoreCase("EVIL")
                        && (!B.ID().equals("GoodExecutioner")))
                        || (flag.equalsIgnoreCase("EVIL")
                        && (!B.ID().equals("EvilExecutioner")))) {
                        if (flag.equalsIgnoreCase("EVIL"))
                            B = CMClass.getBehavior("EvilExecutioner");
                        else
                            B = CMClass.getBehavior("GoodExecutioner");
                    }
                    break;
                }
                case SPECIALTAXER: {
                    if ((B == null) || (!B.ID().equals("TaxCollector")))
                        B = CMClass.getBehavior("TaxCollector");
                    if (B != null)
                        B.tick(owner(), Tickable.TICKID_MOB);
                    break;
                }
                default:
                    break;
            }
        }
        return true;
    }
}
