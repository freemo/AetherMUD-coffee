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
import com.syncleus.aethermud.game.MOBS.interfaces.Deity;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;

import java.util.List;
import java.util.Vector;


public class Serve extends StdCommand {
    private final String[] access = I(new String[]{"SERVE"});

    public Serve() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Serve whom?"));
            return false;
        }
        commands.remove(0);
        final MOB recipient = mob.location().fetchInhabitant(CMParms.combine(commands, 0));
        if ((recipient != null) && (recipient.isMonster()) && (!(recipient instanceof Deity))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You may not serve @x1.", recipient.name()));
            return false;
        }
        if ((recipient == null) || (!CMLib.flags().canBeSeenBy(recipient, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("I don't see @x1 here.", CMParms.combine(commands, 0)));
            return false;
        }
        final CMMsg msg = CMClass.getMsg(mob, recipient, null, CMMsg.MSG_SERVE, L("<S-NAME> swear(s) fealty to <T-NAMESELF>."));
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
        return false;
    }

}
