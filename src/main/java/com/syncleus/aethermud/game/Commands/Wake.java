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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.MUDCmdProcessor;

import java.util.List;
import java.util.Vector;


public class Wake extends StdCommand {
    private final String[] access = I(new String[]{"WAKE"});

    public Wake() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands != null)
            commands.remove(0);
        if ((commands == null) || (commands.size() == 0)) {
            if (!CMLib.flags().isSleeping(mob))
                CMLib.commands().doCommandFail(mob, origCmds, L("You aren't sleeping!?"));
            else {
                final CMMsg msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_SIT, L("<S-NAME> awake(s) and get(s) up."));
                if (mob.location().okMessage(mob, msg)) {
                    mob.location().send(mob, msg);
                    msg.modify(mob, CMMsg.MSG_STAND, null);
                    if (mob.location().okMessage(mob, msg))
                        mob.location().send(mob, msg);
                }
            }
        } else {
            final String whom = CMParms.combine(commands, 0);
            final MOB M = mob.location().fetchInhabitant(whom);
            if ((M == null) || (!CMLib.flags().canBeSeenBy(M, mob))) {
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see '@x1' here.", whom));
                return false;
            }
            if (!CMLib.flags().isSleeping(M)) {
                CMLib.commands().doCommandFail(mob, origCmds, L("@x1 is awake!", M.name(mob)));
                return false;
            }
            final CMMsg msg = CMClass.getMsg(mob, M, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> attempt(s) to wake <T-NAME> up."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                execute(M, null, metaFlags | MUDCmdProcessor.METAFLAG_ORDER);
            }
        }
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
