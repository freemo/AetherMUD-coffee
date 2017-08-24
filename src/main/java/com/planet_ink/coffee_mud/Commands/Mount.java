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
package com.planet_ink.coffee_mud.Commands;

import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.BoardableShip;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;
import com.planet_ink.coffee_mud.core.interfaces.Rider;

import java.util.List;
import java.util.Vector;


public class Mount extends StdCommand {
    private final String[] access = I(new String[]{"MOUNT", "BOARD", "RIDE", "M"});

    public Mount() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        List<String> origCmds = new XVector<String>(commands);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("@x1 what?", (commands.get(0))));
            return false;
        }
        String cmd = commands.remove(0).toString();
        Environmental recipient = null;
        final Vector<Rideable> possRecipients = new Vector<Rideable>();
        for (int m = 0; m < mob.location().numInhabitants(); m++) {
            final MOB M = mob.location().fetchInhabitant(m);
            if ((M != null) && (M instanceof Rideable))
                possRecipients.add((Rideable) M);
        }
        for (int i = 0; i < mob.location().numItems(); i++) {
            final Item I = mob.location().getItem(i);
            if ((I != null) && (I instanceof Rideable))
                possRecipients.add((Rideable) I);
        }
        Rider RI = null;
        if (commands.size() > 1) {
            final Item I = mob.location().findItem(null, commands.get(0));
            if (I != null) {
                commands.remove(0);
                I.setRiding(null);
                RI = I;
            }
            if (RI == null) {
                final MOB M = mob.location().fetchInhabitant(commands.get(0));
                if (M != null) {
                    if (!CMLib.flags().canBeSeenBy(M, mob)) {
                        CMLib.commands().doCommandFail(mob, origCmds, L("You don't see @x1 here.", (commands.get(0))));
                        return false;
                    }
                    if ((!CMLib.flags().isBoundOrHeld(M)) && (!M.willFollowOrdersOf(mob))) {
                        CMLib.commands().doCommandFail(mob, origCmds, L("Only the bound or servants can be mounted unwillingly."));
                        return false;
                    }
                    RI = M;
                    RI.setRiding(null);
                    commands.remove(0);
                }
            }
        }
        recipient = CMLib.english().fetchEnvironmental(possRecipients, CMParms.combine(commands, 0), true);
        if (recipient == null)
            recipient = CMLib.english().fetchEnvironmental(possRecipients, CMParms.combine(commands, 0), false);
        if (recipient == null)
            recipient = mob.location().fetchFromRoomFavorMOBs(null, CMParms.combine(commands, 0));
        if ((recipient == null) || (!CMLib.flags().canBeSeenBy(recipient, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't see '@x1' here.", CMParms.combine(commands, 0)));
            return false;
        }
        if ((recipient instanceof BoardableShip)
            && (cmd.toUpperCase().startsWith("B"))) {
            Command C = CMClass.getCommand("Enter");
            if (C != null) {
                commands = new XVector<String>(origCmds);
                commands.set(0, "ENTER");
                return C.execute(mob, commands, metaFlags);
            }
        }

        String mountStr = null;
        if (recipient instanceof Rideable) {
            if (RI != null)
                mountStr = L("<S-NAME> mount(s) <O-NAME> onto <T-NAMESELF>.");
            else
                mountStr = "<S-NAME> " + ((Rideable) recipient).mountString(CMMsg.TYP_MOUNT, mob) + " <T-NAMESELF>.";
        } else {
            if (RI != null)
                mountStr = L("<S-NAME> mount(s) <O-NAME> to <T-NAMESELF>.");
            else
                mountStr = L("<S-NAME> mount(s) <T-NAMESELF>.");
        }
        final CMMsg msg = CMClass.getMsg(mob, recipient, RI, CMMsg.MSG_MOUNT, mountStr);
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
