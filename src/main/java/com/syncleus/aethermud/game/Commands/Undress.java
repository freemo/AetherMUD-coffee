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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;

import java.util.List;
import java.util.Vector;


public class Undress extends StdCommand {
    private final String[] access = I(new String[]{"UNDRESS"});

    public Undress() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands.size() < 3) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Undress whom? What would you like to remove?"));
            return false;
        }
        if (mob.isInCombat()) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Not while you are in combat!"));
            return false;
        }
        commands.remove(0);
        final String what = commands.get(commands.size() - 1);
        commands.remove(what);
        final String whom = CMParms.combine(commands, 0);
        final MOB target = mob.location().fetchInhabitant(whom);
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("I don't see @x1 here.", whom));
            return false;
        }
        if (target.willFollowOrdersOf(mob) || (CMLib.flags().isBoundOrHeld(target))) {
            final Item item = target.findItem(null, what);
            if ((item == null)
                || (!CMLib.flags().canBeSeenBy(item, mob))
                || (item.amWearingAt(Wearable.IN_INVENTORY))) {
                CMLib.commands().doCommandFail(mob, origCmds, L("@x1 doesn't seem to be equipped with '@x2'.", target.name(mob), what));
                return false;
            }
            if (target.isInCombat()) {
                CMLib.commands().doCommandFail(mob, origCmds, L("Not while @x1 is in combat!", target.name(mob)));
                return false;
            }
            CMMsg msg = CMClass.getMsg(mob, target, null, CMMsg.MSG_QUIETMOVEMENT, null);
            if (mob.location().okMessage(mob, msg)) {
                msg = CMClass.getMsg(target, item, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_REMOVE, CMMsg.MSG_REMOVE, CMMsg.MSG_REMOVE, null);
                if (mob.location().okMessage(mob, msg)) {
                    mob.location().send(mob, msg);
                    msg = CMClass.getMsg(target, item, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_DROP, CMMsg.MSG_DROP, CMMsg.MSG_DROP, null);
                    if (mob.location().okMessage(mob, msg)) {
                        mob.location().send(mob, msg);
                        if (CMLib.commands().postGet(mob, null, item, true))
                            mob.location().show(mob, target, item, CMMsg.MASK_ALWAYS | CMMsg.MSG_QUIETMOVEMENT, L("<S-NAME> take(s) <O-NAME> off <T-NAMESELF>."));
                    } else
                        CMLib.commands().doCommandFail(mob, origCmds, L("You cannot seem to get @x1 off @x2.", item.name(), target.name(mob)));
                } else
                    CMLib.commands().doCommandFail(mob, origCmds, L("You cannot seem to get @x1 off of @x2.", item.name(), target.name(mob)));
            }
        } else
            CMLib.commands().doCommandFail(mob, origCmds, L("@x1 won't let you.", target.name(mob)));
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
