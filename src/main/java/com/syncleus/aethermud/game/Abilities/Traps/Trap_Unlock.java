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
package com.syncleus.aethermud.game.Abilities.Traps;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Trap_Unlock extends Trap_Trap {
    private final static String localizedName = CMLib.lang().L("Unlock Trap");

    @Override
    public String ID() {
        return "Trap_Unlock";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_EXITS | Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (sprung) {
            if (msg.source().isMine(affected))
                unInvoke();
            else
                super.executeMsg(myHost, msg);
            return;
        }
        super.executeMsg(myHost, msg);

        if (msg.amITarget(affected)) {
            if ((msg.targetMinor() == CMMsg.TYP_UNLOCK)
                || (msg.targetMinor() == CMMsg.TYP_JUSTICE)
                || (msg.targetMinor() == CMMsg.TYP_DELICATE_HANDS_ACT)) {
                spring(msg.source());
            }
        }
    }
}
