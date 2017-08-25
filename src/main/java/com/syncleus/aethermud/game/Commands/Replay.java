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
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;

import java.util.List;


public class Replay extends StdCommand {
    private final String[] access = I(new String[]{"REPLAY"});

    public Replay() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (!mob.isMonster()) {
            final Session S = mob.session();
            int num = Session.MAX_PREVMSGS;
            if (commands.size() > 1)
                num = CMath.s_int(CMParms.combine(commands, 1));
            if (num <= 0)
                return false;
            final java.util.List<String> last = S.getLastMsgs();
            if (num > last.size())
                num = last.size();
            for (int v = last.size() - num; v < last.size(); v++)
                S.onlyPrint((last.get(v)) + "\n\r", true);
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
