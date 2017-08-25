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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Clan;
import com.syncleus.aethermud.game.Items.interfaces.ClanItem;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class StdClanApron extends StdClanItem {
    public StdClanApron() {
        super();

        setName("a clan apron");
        basePhyStats.setWeight(1);
        setDisplayText("an apron belonging to a clan is here.");
        setDescription("");
        secretIdentity = "";
        baseGoldValue = 1;
        setClanItemType(ClanItem.ClanItemType.SPECIALAPRON);
        material = RawMaterial.RESOURCE_COTTON;
        setRawProperLocationBitmap(Wearable.WORN_WAIST | Wearable.WORN_ABOUT_BODY);
        setRawLogicalAnd(false);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdClanApron";
    }

    @Override
    public boolean okMessage(Environmental affecting, CMMsg msg) {
        if (owner() instanceof MOB)
            if (msg.amITarget(owner())) {
                switch (msg.targetMinor()) {
                    case CMMsg.TYP_VALUE:
                    case CMMsg.TYP_SELL:
                    case CMMsg.TYP_BUY:
                    case CMMsg.TYP_BID:
                    case CMMsg.TYP_VIEW:
                    case CMMsg.TYP_LIST:
                        if ((clanID().length() > 0)
                            && (msg.source() != owner())
                            && (msg.source().getClanRole(clanID()) == null)) {
                            final Clan C = CMLib.clans().getClan(clanID());
                            if (C != null) {
                                int state = Clan.REL_NEUTRAL;
                                for (final Pair<Clan, Integer> p : CMLib.clans().findRivalrousClans(msg.source())) {
                                    state = C.getClanRelations(p.first.clanID());
                                    if ((state != Clan.REL_NEUTRAL)
                                        && (state != Clan.REL_ALLY)
                                        && (state != Clan.REL_FRIENDLY)) {
                                        msg.source().tell(((MOB) owner()), null, null, L("<S-NAME> seem(s) to be ignoring you."));
                                        return false;
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        return super.okMessage(affecting, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (fetchEffect("Merchant") == null) {
            final Ability A = CMClass.getAbility("Merchant");
            if (A != null)
                addNonUninvokableEffect(A);
        }
        return true;
    }
}
