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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.*;
import com.planet_ink.game.core.interfaces.MUDCmdProcessor;

import java.util.List;


public class MetaMsgCommand extends StdCommand {
    private final String[] access = I(new String[]{"METAMSGCOMMAND"});

    public MetaMsgCommand() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (!CMath.bset(metaFlags, MUDCmdProcessor.METAFLAG_ASMESSAGE))
            return CMLib.commands().handleUnknownCommand(mob, commands);
        else {
            final Room R = mob.location();
            commands.remove(0);
            final CMMsg msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_COMMAND, CMParms.combineQuoted(commands, 0), null, null);
            if (R != null) {
                if (!R.okMessage(mob, msg))
                    return false;
                R.send(mob, msg);
                return true;
            }
            return false;
        }
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
        return false;
    }
}
