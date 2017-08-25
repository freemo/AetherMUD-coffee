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

import com.syncleus.aethermud.game.Commands.interfaces.Command;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Rideable;

import java.util.List;
import java.util.Vector;


public class Sit extends StdCommand {
    private final String[] access = I(new String[]{"SIT", "REST", "R"});

    public Sit() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (CMLib.flags().isSitting(mob)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You are already sitting!"));
            return false;
        }
        final Room R = mob.location();
        if (commands.size() <= 1) {
            CMMsg msg;
            if (CMLib.flags().isSleeping(mob))
                msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_SIT, L("<S-NAME> awake(s) and sit(s) up."));
            else
                msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_SIT, L("<S-NAME> sit(s) down and take(s) a rest."));
            if (R.okMessage(mob, msg))
                R.send(mob, msg);
            return false;
        }
        final String possibleRideable = CMParms.combine(commands, 1);
        Environmental E = null;
        if (possibleRideable.length() > 0) {
            E = R.fetchFromRoomFavorItems(null, possibleRideable);
            if (E == null)
                E = R.fetchExit(possibleRideable);
            if ((E == null) || (!CMLib.flags().canBeSeenBy(E, mob))) {
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see '@x1' here.", possibleRideable));
                return false;
            }
            if (E instanceof MOB) {
                final Command C = CMClass.getCommand("Mount");
                if (C != null)
                    return C.execute(mob, commands, metaFlags);
            }
        }
        String mountStr = null;
        if (E instanceof Rideable)
            mountStr = L("<S-NAME> " + ((Rideable) E).mountString(CMMsg.TYP_SIT, mob) + " <T-NAME>.");
        else
            mountStr = L("<S-NAME> sit(s) on <T-NAME>.");
        final CMMsg msg = CMClass.getMsg(mob, E, null, CMMsg.MSG_SIT, mountStr);
        if (R.okMessage(mob, msg))
            R.send(mob, msg);
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
