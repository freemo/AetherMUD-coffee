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
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.Resources;

import java.util.*;


public class ATopics extends StdCommand {
    private final String[] access = I(new String[]{"ARCTOPICS", "ATOPICS"});

    public ATopics() {
    }

    public static void doTopics(MOB mob, Properties rHelpFile, String helpName, String resName) {
        StringBuffer topicBuffer = (StringBuffer) Resources.getResource(resName);
        if (topicBuffer == null) {
            topicBuffer = new StringBuffer();

            final Vector<String> reverseList = new Vector<String>();
            for (final Enumeration<Object> e = rHelpFile.keys(); e.hasMoreElements(); ) {
                final String ptop = (String) e.nextElement();
                final String thisTag = rHelpFile.getProperty(ptop);
                if ((thisTag == null) || (thisTag.length() == 0) || (thisTag.length() >= 35)
                    || (rHelpFile.getProperty(thisTag) == null))
                    reverseList.add(ptop);
            }

            Collections.sort(reverseList);
            topicBuffer = new StringBuffer("Help topics: \n\r\n\r");
            topicBuffer.append(CMLib.lister().fourColumns(mob, reverseList, "HELP"));
            topicBuffer = new StringBuffer(topicBuffer.toString().replace('_', ' '));
            Resources.submitResource(resName, topicBuffer);
        }
        if ((mob != null) && (!mob.isMonster()))
            mob.session().colorOnlyPrintln(CMLib.lang().L("@x1\n\r\n\rEnter @x2 (TOPIC NAME) for more information.", topicBuffer.toString(), helpName), false);
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final Properties arcHelpFile = CMLib.help().getArcHelpFile();
        if (arcHelpFile.size() == 0) {
            if (mob != null)
                mob.tell(L("No archon help is available."));
            return false;
        }

        doTopics(mob, arcHelpFile, "AHELP", "ARCHON TOPICS");
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.AHELP);
    }

}
