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

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.*;
import com.planet_ink.game.Libraries.interfaces.CMFlagLibrary;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


public class Activate extends StdCommand {
    private final String[] access = I(new String[]{"ACTIVATE", "ACT", "A", ">"});

    public Activate() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        final Room R = mob.location();
        if ((commands.size() < 2) || (R == null)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Activate what?"));
            return false;
        }
        commands.remove(0);
        final String what = commands.get(commands.size() - 1);
        final String whole = CMParms.combine(commands, 0);
        Item item = null;
        Environmental E = mob.location().fetchFromMOBRoomFavorsItems(mob, null, whole, Wearable.FILTER_ANY);
        if ((!(E instanceof Electronics)) || (E instanceof Software))
            E = null;
        if (E == null) {
            final CMFlagLibrary flagLib = CMLib.flags();
            for (int i = 0; i < R.numItems(); i++) {
                final Item I = R.getItem(i);
                if (flagLib.isOpenAccessibleContainer(I)) {
                    E = R.fetchFromRoomFavorItems(I, whole);
                    if ((E instanceof Electronics) && (!(E instanceof Software)))
                        break;
                }
            }
        }
        if ((!(E instanceof Electronics)) || (E instanceof Software))
            E = null;
        else {
            item = (Item) E;
            commands.clear();
        }
        if (E == null) {
            E = mob.location().fetchFromMOBRoomFavorsItems(mob, null, what, Wearable.FILTER_ANY);
            if ((!(E instanceof Electronics)) || (E instanceof Software))
                E = null;
            if (E == null) {
                for (int i = 0; i < R.numItems(); i++) {
                    final Item I = R.getItem(i);
                    if (CMLib.flags().isOpenAccessibleContainer(I)) {
                        E = R.fetchFromRoomFavorItems(I, what);
                        if ((E instanceof Electronics) && (!(E instanceof Software)))
                            break;
                    }
                }
            }
            if ((!(E instanceof Electronics)) || (E instanceof Software))
                E = null;
            if ((E == null) && (mob.riding() instanceof Computer)) {
                E = mob.riding();
                item = (Item) E;
            } else {
                item = (Item) E;
                commands.remove(commands.size() - 1);
            }
        }
        if (E == null) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't see anything called '@x1' or '@x2' here that you can activate.", what, whole));
            return false;
        } else if (item == null) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You can't activate @x1.", E.name()));
            return false;
        }

        final String rest = CMParms.combine(commands, 0);
        final CMMsg newMsg = CMClass.getMsg(mob, item, null, CMMsg.MSG_ACTIVATE, null, CMMsg.MSG_ACTIVATE, (rest.length() == 0) ? null : rest, CMMsg.MSG_ACTIVATE, null);
        if (R.okMessage(mob, newMsg))
            R.send(mob, newMsg);
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
