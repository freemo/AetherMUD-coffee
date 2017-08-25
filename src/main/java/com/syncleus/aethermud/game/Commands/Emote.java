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
import com.syncleus.aethermud.game.Libraries.interfaces.TelnetFilter.Pronoun;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;


public class Emote extends StdCommand {
    private final String[] access = I(new String[]{"EMOTE", ",", ";", ":"});

    public Emote() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (commands.size() < 2) {
            mob.tell(L(" EMOTE what?"));
            return false;
        }
        String combinedCommands = CMParms.combine(commands, 1);
        combinedCommands = CMProps.applyINIFilter(combinedCommands, CMProps.Str.EMOTEFILTER);
        if (combinedCommands.trim().startsWith("'") || combinedCommands.trim().startsWith("`"))
            combinedCommands = combinedCommands.trim();
        else
            combinedCommands = " " + combinedCommands.trim();
        Environmental target = null;
        int x = combinedCommands.indexOf('/');
        while (x > 0) {
            int y = CMStrings.indexOfEndOfWord(combinedCommands, x + 1);
            if (y < 0)
                y = combinedCommands.length();
            String rest = combinedCommands.substring(x + 1, y);
            Pronoun P = Pronoun.NAME;
            for (final Pronoun p : Pronoun.values()) {
                if ((p.emoteSuffix != null) && (rest.endsWith(p.emoteSuffix))) {
                    P = p;
                    rest = rest.substring(0, rest.length() - p.emoteSuffix.length());
                    break;
                }
            }
            if (rest.length() > 0) {
                final Environmental E = mob.location().fetchFromRoomFavorMOBs(null, rest);
                if ((E != null) && (CMLib.flags().canBeSeenBy(E, mob))) {
                    target = E;
                    combinedCommands = combinedCommands.substring(0, x) + "<T" + P.suffix + ">" + combinedCommands.substring(y);
                }
            }
            x = combinedCommands.indexOf('/', x + 1);
        }
        final String emote = "^E<S-NAME>" + combinedCommands + " ^?";
        final CMMsg msg = CMClass.getMsg(mob, target, null, CMMsg.MSG_EMOTE, emote);
        if (mob.location().okMessage(mob, msg))
            mob.location().send(mob, msg);
        return false;
    }

    @Override
    public double combatActionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandCombatActionCost(ID());
    }

    @Override
    public double actionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandActionCost(ID());
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
