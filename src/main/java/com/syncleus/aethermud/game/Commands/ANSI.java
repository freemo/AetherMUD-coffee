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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.Commands.interfaces.Command;
import com.syncleus.aethermud.game.Common.interfaces.PlayerAccount;
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;

import java.util.List;


public class ANSI extends StdCommand {
    private final String[] access = I(new String[]{"ANSI", "COLOR", "COLOUR"});

    public ANSI() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (!mob.isMonster()) {
            if ((commands != null)
                && (commands.size() > 1)
                && (commands.get(1).toUpperCase().equals("OFF"))) {
                final Command C = CMClass.getCommand("NOANSI");
                if (C != null) {
                    return C.execute(mob, commands, metaFlags);
                }
            }

            PlayerAccount acct = null;
            if (mob.playerStats() != null)
                acct = mob.playerStats().getAccount();
            if (acct != null)
                acct.setFlag(PlayerAccount.AccountFlag.ANSI, true);
            if (!mob.isAttributeSet(MOB.Attrib.ANSI)) {
                mob.setAttribute(MOB.Attrib.ANSI, true);
                mob.tell(L("^!ANSI^N ^Hcolour^N enabled.\n\r"));
            } else {
                mob.tell(L("^!ANSI^N is ^Halready^N enabled.\n\r"));
            }
            mob.session().setClientTelnetMode(Session.TELNET_ANSI, true);
            mob.session().setServerTelnetMode(Session.TELNET_ANSI, true);
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
