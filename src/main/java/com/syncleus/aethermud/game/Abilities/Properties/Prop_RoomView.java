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
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Prop_RoomView extends Property {
    protected Room newRoom = null;

    @Override
    public String ID() {
        return "Prop_RoomView";
    }

    @Override
    public String name() {
        return "Different Room View";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_ITEMS | Ability.CAN_EXITS;
    }

    @Override
    public String accountForYourself() {
        return "Different View of " + text();
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((newRoom == null)
            || (newRoom.amDestroyed())
            || (!CMLib.map().getExtendedRoomID(newRoom).equalsIgnoreCase(text().trim())))
            newRoom = CMLib.map().getRoom(text());
        if (newRoom == null)
            return super.okMessage(myHost, msg);

        if ((affected != null)
            && ((affected instanceof Room) || (affected instanceof Exit) || (affected instanceof Item))
            && (msg.amITarget(affected))
            && (newRoom.fetchEffect(ID()) == null)
            && ((msg.targetMinor() == CMMsg.TYP_LOOK) || (msg.targetMinor() == CMMsg.TYP_EXAMINE))) {
            final CMMsg msg2 = CMClass.getMsg(msg.source(), newRoom, msg.tool(),
                msg.sourceCode(), msg.sourceMessage(),
                msg.targetCode(), msg.targetMessage(),
                msg.othersCode(), msg.othersMessage());
            if (newRoom.okMessage(msg.source(), msg2)) {
                newRoom.executeMsg(msg.source(), msg2);
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

}
