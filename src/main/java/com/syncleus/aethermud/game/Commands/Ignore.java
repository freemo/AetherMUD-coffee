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

import com.syncleus.aethermud.game.Common.interfaces.PlayerStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMStrings;

import java.util.List;
import java.util.Set;


public class Ignore extends StdCommand {
    private final String[] access = I(new String[]{"IGNORE"});

    public Ignore() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final PlayerStats pstats = mob.playerStats();
        if (pstats == null)
            return false;
        final Set<String> h = pstats.getIgnored();
        if ((commands.size() < 2) || (commands.get(1).equalsIgnoreCase("list"))) {
            if (h.size() == 0)
                mob.tell(L("You have no names on your ignore list.  Use IGNORE ADD to add more."));
            else {
                final StringBuffer str = new StringBuffer(L("You are ignoring: "));
                for (final Object element : h)
                    str.append(((String) element) + " ");
                mob.tell(str.toString());
            }
        } else if (commands.get(1).equalsIgnoreCase("ADD")) {
            String name = CMParms.combine(commands, 2);
            if (name.length() == 0) {
                mob.tell(L("Add whom?"));
                return false;
            }
            name = CMStrings.capitalizeAndLower(name);
            if ((!CMLib.players().playerExists(name)) && (name.indexOf('@') < 0)) {
                mob.tell(L("No player by that name was found."));
                return false;
            }
            if (h.contains(name)) {
                mob.tell(L("That name is already on your list."));
                return false;
            }
            h.add(name);
            mob.tell(L("The Player '@x1' has been added to your ignore list.", name));
        } else if (commands.get(1).equalsIgnoreCase("REMOVE")) {
            final String name = CMParms.combine(commands, 2);
            if (name.length() == 0) {
                mob.tell(L("Remove whom?"));
                return false;
            }
            if (!h.contains(name)) {
                mob.tell(L("That name '@x1' does not appear on your list.  Watch your casing!", name));
                return false;
            }
            h.remove(name);
            mob.tell(L("The Player '@x1' has been removed from your ignore list.", name));
        } else {
            mob.tell(L("Parameter '@x1' is not recognized.  Try LIST, ADD, or REMOVE.", (commands.get(1))));
            return false;
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
