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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.interfaces.Decayable;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

/*
   Copyright 2004-2006 Robert Little
   http://www.tttgames.divineright.org
   The Looking Glass RPG
   www.tttgames.divineright.org  host: divineright.org port: 7000
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
public class Prop_IceBox extends Property {
    @Override
    public String ID() {
        return "Prop_IceBox";
    }

    @Override
    public String name() {
        return "Works like an ice box";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_ROOMS;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        switch (msg.targetMinor()) {
            case CMMsg.TYP_PUT:
            case CMMsg.TYP_INSTALL:
                if (affected instanceof Item) {
                    if (msg.amITarget(affected) && (msg.tool() instanceof Decayable))
                        ((Decayable) msg.tool()).setDecayTime(Long.MAX_VALUE);
                }
                break;
            case CMMsg.TYP_GET:
                if ((msg.target() instanceof Decayable)
                    && (msg.target() instanceof Item)) {
                    if ((affected instanceof Item)
                        && (((Item) msg.target()).container() == affected))
                        ((Decayable) msg.target()).setDecayTime(0); // will cause a recalc on next msg
                    if ((affected instanceof Room)
                        && (((Item) msg.target()).owner() == affected))
                        ((Decayable) msg.target()).setDecayTime(0); // will cause a recalc on next msg
                }
                break;
            case CMMsg.TYP_DROP:
                if (affected instanceof Room) {
                    if (msg.target() instanceof Decayable)
                        ((Decayable) msg.target()).setDecayTime(Long.MAX_VALUE);
                }
                break;
        }
        return true;
    }
}