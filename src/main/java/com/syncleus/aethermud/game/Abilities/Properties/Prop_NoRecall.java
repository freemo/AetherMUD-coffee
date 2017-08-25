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
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Rideable;


public class Prop_NoRecall extends Property {
    @Override
    public String ID() {
        return "Prop_NoRecall";
    }

    @Override
    public String name() {
        return "Recall Neuralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_ITEMS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_ZAPPER;
    }

    @Override
    public String accountForYourself() {
        return "No Recall Field";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (msg.sourceMinor() == CMMsg.TYP_RECALL) {
            if (msg.source().location() != null) {
                if (((myHost instanceof MOB) && ((msg.source() == myHost) || (msg.source().location() == ((MOB) myHost).location())) && (msg.source().isInCombat()))
                    || ((myHost instanceof Rideable) && (msg.source().riding() == myHost))
                    || ((myHost instanceof Item) && (msg.source() == ((Item) myHost).owner()))
                    || ((myHost instanceof Room) && (msg.source().location() == myHost))
                    || (myHost instanceof Exit)
                    || (myHost instanceof Area))
                    msg.source().location().show(msg.source(), null, CMMsg.MSG_OK_ACTION, L("<S-NAME> attempt(s) to recall, but the magic fizzles."));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }
}
