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

import com.planet_ink.game.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMStrings;
import com.planet_ink.game.core.collections.Pair;
import com.planet_ink.game.core.collections.XVector;

import java.util.Enumeration;
import java.util.List;


public class Expertises extends StdCommand {
    private final String[] access = I(new String[]{"EXPERTISES", "EXPS"});

    public Expertises() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final StringBuffer msg = new StringBuffer("");
        msg.append(L("\n\r^HYour expertises:^? \n\r"));
        int col = 0;
        final int COL_LEN = CMLib.lister().fixColWidth(25.0, mob);
        final XVector<String> expers = new XVector<String>();
        for (final Enumeration<String> e = mob.expertises(); e.hasMoreElements(); ) {
            final String exper = e.nextElement();
            final ExpertiseLibrary.ExpertiseDefinition def = CMLib.expertises().getDefinition(exper);
            if (def == null) {
                final Pair<String, Integer> p = mob.fetchExpertise(exper);
                if (p == null)
                    expers.add("?" + CMStrings.capitalizeAllFirstLettersAndLower(exper));
                else if (p.first.endsWith("%"))
                    expers.add("?" + CMStrings.capitalizeAllFirstLettersAndLower(p.first.substring(0, p.first.length() - 1)) + " (" + p.second.intValue() + "%)");
                else
                    expers.add("?" + CMStrings.capitalizeAllFirstLettersAndLower(p.first) + " " + p.second.intValue());
            } else
                expers.add(def.name());
        }
        expers.sort();
        for (final String expName : expers) {
            if (expName.startsWith("?")) {
                msg.append(CMStrings.padRight(expName.substring(1), COL_LEN));
            } else if (expName.length() >= COL_LEN) {
                if (col >= 2) {
                    msg.append("\n\r");
                    col = 0;
                }
                msg.append(CMStrings.padRightPreserve("^<HELP^>" + expName + "^</HELP^>", COL_LEN));
                final int spaces = (COL_LEN * 2) - expName.length();
                for (int i = 0; i < spaces; i++) msg.append(" ");
                col++;
            } else
                msg.append(CMStrings.padRight("^<HELP^>" + expName + "^</HELP^>", COL_LEN));
            if ((++col) >= 3) {
                msg.append("\n\r");
                col = 0;
            }
        }
        if (!msg.toString().endsWith("\n\r"))
            msg.append("\n\r");
        if (!mob.isMonster())
            mob.session().wraplessPrintln(msg.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
