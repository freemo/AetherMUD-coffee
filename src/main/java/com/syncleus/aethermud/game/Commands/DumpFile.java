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

import com.planet_ink.game.Common.interfaces.Session;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMFile;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.Resources;

import java.util.List;


public class DumpFile extends StdCommand {
    private final String[] access = I(new String[]{"DUMPFILE"});

    public DumpFile() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (commands.size() < 3) {
            mob.tell(L("dumpfile {raw} username|all {filename1 ...}"));
            return false;
        }
        commands.remove(0);

        int numFiles = 0;
        int numSessions = 0;
        boolean rawMode = false;

        if (commands.get(0).equalsIgnoreCase("raw")) {
            rawMode = true;
            commands.remove(0);
        }

        final String targetName = commands.get(0);
        final boolean allFlag = (targetName.equalsIgnoreCase("all"));

        commands.remove(0);

        // so they can do dumpfile (username) RAW filename too
        if (!rawMode && (commands.get(0).equalsIgnoreCase("raw"))) {
            rawMode = true;
            commands.remove(0);
        }

        final StringBuffer fileText = new StringBuffer("");
        while (commands.size() > 0) {
            boolean wipeAfter = true;
            final String fn = commands.get(0);

            if (Resources.getResource(fn) != null)
                wipeAfter = false;

            final StringBuffer ft = new CMFile(fn, mob, CMFile.FLAG_LOGERRORS).text();
            if (ft != null && ft.length() > 0) {
                fileText.append("\n\r");
                fileText.append(ft);
                ++numFiles;
            }

            if (wipeAfter)
                Resources.removeResource(fn);
            commands.remove(0);

        }
        if (fileText.length() > 0) {
            for (final Session S : CMLib.sessions().localOnlineIterable()) {
                if (!CMSecurity.isAllowed(mob, S.mob().location(), CMSecurity.SecFlag.DUMPFILE))
                    continue;
                if (allFlag || S.mob().name().equalsIgnoreCase(targetName)) {
                    if (rawMode)
                        S.rawPrintln(fileText.toString());
                    else
                        S.colorOnlyPrintln(fileText.toString());
                    ++numSessions;
                }
            }
        }
        mob.tell(L("dumped @x1 files to @x2 user(s)", "" + numFiles, "" + numSessions));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.DUMPFILE);
    }

}
