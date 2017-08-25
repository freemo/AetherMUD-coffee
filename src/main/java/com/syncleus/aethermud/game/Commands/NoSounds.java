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

import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMSecurity;

import java.util.List;


public class NoSounds extends StdCommand {
    private final String[] access = I(new String[]{"NOSOUNDS", "NOMSP"});

    public NoSounds() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (!mob.isMonster()) {
            if ((mob.isAttributeSet(MOB.Attrib.SOUND))
                || (mob.session().getClientTelnetMode(Session.TELNET_MSP))) {
                mob.setAttribute(MOB.Attrib.SOUND, false);
                mob.session().changeTelnetMode(Session.TELNET_MSP, false);
                mob.session().setClientTelnetMode(Session.TELNET_MSP, false);
                mob.tell(L("MSP Sound/Music disabled.\n\r"));
            } else
                mob.tell(L("MSP Sound/Music already disabled.\n\r"));
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return super.securityCheck(mob) && (!CMSecurity.isDisabled(CMSecurity.DisFlag.MSP));
    }
}
