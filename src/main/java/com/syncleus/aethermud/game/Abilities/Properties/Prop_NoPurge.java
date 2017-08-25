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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prop_NoPurge extends Property {
    @Override
    public String ID() {
        return "Prop_NoPurge";
    }

    @Override
    public String name() {
        return "Prevents automatic purging";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_ITEMS;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected != null) {
            if (affected instanceof Room) {
                final Room R = (Room) affected;
                for (int i = 0; i < R.numItems(); i++) {
                    final Item I = R.getItem(i);
                    if (I != null)
                        I.setExpirationDate(0);
                }
            } else if (affected instanceof Container) {
                if (((Container) affected).owner() instanceof Room) {
                    ((Container) affected).setExpirationDate(0);
                    final List<Item> V = ((Container) affected).getDeepContents();
                    for (int v = 0; v < V.size(); v++)
                        V.get(v).setExpirationDate(0);
                }
            } else if (affected instanceof Item)
                ((Item) affected).setExpirationDate(0);
        }
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (affected != null) {
            if (affected instanceof Room) {
                if ((msg.targetMinor() == CMMsg.TYP_DROP)
                    && (msg.target() instanceof Item))
                    ((Item) msg.target()).setExpirationDate(0);
            } else if (affected instanceof Container) {
                if (((msg.targetMinor() == CMMsg.TYP_PUT)
                    || (msg.targetMinor() == CMMsg.TYP_INSTALL))
                    && (msg.target() == affected)
                    && (msg.target() instanceof Item)
                    && (msg.tool() instanceof Item)) {
                    ((Item) msg.target()).setExpirationDate(0);
                    ((Item) msg.tool()).setExpirationDate(0);
                }
            } else if (affected instanceof Item) {
                if ((msg.targetMinor() == CMMsg.TYP_DROP)
                    && (msg.target() instanceof Item)
                    && (msg.target() == affected))
                    ((Item) msg.target()).setExpirationDate(0);
            }
        }
    }
}
