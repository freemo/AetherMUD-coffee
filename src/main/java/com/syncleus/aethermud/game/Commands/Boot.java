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

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;

import java.util.List;


public class Boot extends StdCommand {
    private final String[] access = I(new String[]{"BOOT"});

    public Boot() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        commands.remove(0);
        if (mob.session() == null)
            return false;
        if (commands.size() == 0) {
            mob.tell(L("Boot out who?"));
            return false;
        }
        final String whom = CMParms.combine(commands, 0);
        boolean boot = false;
        for (final Session S : CMLib.sessions().allIterable()) {
            if (((S.mob() != null) && (CMLib.english().containsString(S.mob().name(), whom)))
                || (S.getAddress().equalsIgnoreCase(whom))) {
                if (S == mob.session()) {
                    mob.tell(L("Try QUIT."));
                    return false;
                }
                if (S.mob() != null) {
                    mob.tell(L("You boot @x1", S.mob().name()));
                    if (S.mob().location() != null)
                        S.mob().location().show(S.mob(), null, CMMsg.MSG_OK_VISUAL, L("Something is happening to <S-NAME>."));
                } else
                    mob.tell(L("You boot @x1", S.getAddress()));
                S.stopSession(false, false, false);
                CMLib.s_sleep(100);
                if (((S.getPreviousCMD() == null) || (S.getPreviousCMD().size() == 0))
                    && (!CMLib.flags().isInTheGame(S.mob(), true)))
                    CMLib.sessions().stopSessionAtAllCosts(S);
                boot = true;
                break;
            }
        }
        if (!boot)
            mob.tell(L("You can't find anyone by that name or ip address."));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.BOOT);
    }

}
