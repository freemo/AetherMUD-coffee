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
package com.planet_ink.game.Commands;

import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.*;

import java.util.List;


public class Help extends StdCommand {
    private final String[] access = I(new String[]{"HELP"});

    public Help() {
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
        StringBuilder thisTag = null;
        if (helpStr.length() == 0)
            thisTag = new StringBuilder(Resources.getFileResource("help/help.txt", true));
        else
            thisTag = CMLib.help().getHelpText(helpStr, CMLib.help().getHelpFile(), mob);
        if ((thisTag == null) && (CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.AHELP)))
            thisTag = CMLib.help().getHelpText(helpStr, CMLib.help().getArcHelpFile(), mob);
        if (thisTag == null) {
            final StringBuilder thisList =
                CMLib.help().getHelpList(
                    helpStr,
                    CMLib.help().getHelpFile(),
                    CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.AHELP) ? CMLib.help().getArcHelpFile() : null,
                    mob);
            if ((thisList != null) && (thisList.length() > 0))
                mob.tell(L("No help is available on '@x1'.\n\rHowever, here are some search matches:\n\r^N@x2", helpStr, thisList.toString().replace('_', ' ')));
            else
                mob.tell(L("No help is available on '@x1'.\n\rEnter 'COMMANDS' for a command list, or 'TOPICS' for a complete list, or 'HELPLIST' to search.", helpStr));
            Log.helpOut("Help", mob.Name() + " wanted help on " + helpStr);
        } else if (!mob.isMonster())
            mob.session().wraplessPrintln(thisTag.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
