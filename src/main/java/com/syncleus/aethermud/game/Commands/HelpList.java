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

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.Log;

import java.util.List;


public class HelpList extends StdCommand {
    private final String[] access = I(new String[]{"HELPLIST", "HLIST"});

    public HelpList() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final String helpStr = CMParms.combine(commands, 1);
        if (CMLib.help().getHelpFile().size() == 0) {
            mob.tell(L("No help is available."));
            return false;
        }
        if (helpStr.length() == 0) {
            mob.tell(L("You must enter a search pattern.  Use 'TOPICS' or 'COMMANDS' for an unfiltered list."));
            return false;
        }
        final StringBuilder thisTag =
            CMLib.help().getHelpList(
                helpStr,
                CMLib.help().getHelpFile(),
                CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.AHELP) ? CMLib.help().getArcHelpFile() : null,
                mob);
        if ((thisTag == null) || (thisTag.length() == 0)) {
            mob.tell(L("No help entries match '@x1'.\nEnter 'COMMANDS' for a command list, or 'TOPICS' for a complete list.", helpStr));
            Log.helpOut("Help", mob.Name() + " wanted help list match on " + helpStr);
        } else if (!mob.isMonster())
            mob.session().wraplessPrintln(L("^xHelp File Matches:^.^?\n\r^N@x1", thisTag.toString().replace('_', ' ')));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}

