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

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.MUDCmdProcessor;

import java.util.List;
import java.util.Vector;


public class Order extends StdCommand {
    private final String[] access = I(new String[]{"ORDER"});

    public Order() {
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
            CMLib.commands().doCommandFail(mob, origCmds, L("Order who do to what?"));
            return false;
        }
        commands.remove(0);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Order them to do what?"));
            return false;
        }
        if ((!CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.ORDER))
            && (!mob.isMonster())
            && (mob.isAttributeSet(MOB.Attrib.AUTOASSIST))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You may not order someone around with your AUTOASSIST flag off."));
            return false;
        }

        String whomToOrder = commands.get(0);
        final Vector<MOB> V = new Vector<MOB>();
        boolean allFlag = whomToOrder.equalsIgnoreCase("all");
        if (whomToOrder.toUpperCase().startsWith("ALL.")) {
            allFlag = true;
            whomToOrder = "ALL " + whomToOrder.substring(4);
        }
        if (whomToOrder.toUpperCase().endsWith(".ALL")) {
            allFlag = true;
            whomToOrder = "ALL " + whomToOrder.substring(0, whomToOrder.length() - 4);
        }
        int addendum = 1;
        String addendumStr = "";
        boolean doBugFix = true;
        while (doBugFix || allFlag) {
            doBugFix = false;
            final MOB target = mob.location().fetchInhabitant(whomToOrder + addendumStr);
            if (target == null)
                break;
            if ((CMLib.flags().canBeSeenBy(target, mob))
                && (target != mob)
                && (!V.contains(target)))
                V.add(target);
            addendumStr = "." + (++addendum);
        }

        if (V.size() == 0) {
            if (whomToOrder.equalsIgnoreCase("ALL"))
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see anyone called '@x1' here.", whomToOrder));
            else
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see anyone here."));
            return false;
        }

        MOB target = null;
        if (V.size() == 1) {
            target = V.get(0);
            if ((!CMLib.flags().canBeSeenBy(target, mob))
                || (!CMLib.flags().canBeHeardSpeakingBy(mob, target))
                || (target.location() != mob.location())) {
                CMLib.commands().doCommandFail(mob, origCmds, L("'@x1' doesn't seem to be listening.", whomToOrder));
                return false;
            }
            if (!target.willFollowOrdersOf(mob)) {
                CMLib.commands().doCommandFail(mob, origCmds, L("You can't order '@x1' around.", target.name(mob)));
                return false;
            }
        }

        commands.remove(0);

        CMObject O = CMLib.english().findCommand(mob, commands);
        final String order = CMParms.combine(commands, 0);
        if (!CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.ORDER)) {
            if ((O instanceof Command) && (!((Command) O).canBeOrdered())) {
                CMLib.commands().doCommandFail(mob, origCmds, L("You can't order anyone to '@x1'.", order));
                return false;
            }
        }

        final Vector<MOB> doV = new Vector<MOB>();
        for (int v = 0; v < V.size(); v++) {
            target = V.get(v);
            O = CMLib.english().findCommand(target, new XVector<String>(commands));
            if (!CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.ORDER)) {
                if ((O instanceof Command)
                    && ((!((Command) O).canBeOrdered()) || (!((Command) O).securityCheck(mob)))) {
                    CMLib.commands().doCommandFail(mob, origCmds, L("You can't order @x1 to '@x2'.", target.name(mob), order));
                    continue;
                }
                if (O instanceof Ability)
                    O = CMLib.english().getToEvoke(target, new XVector<String>(commands));
                if (O instanceof Ability) {
                    if (CMath.bset(((Ability) O).flags(), Ability.FLAG_NOORDERING)) {
                        CMLib.commands().doCommandFail(mob, origCmds, L("You can't order @x1 to '@x2'.", target.name(mob), order));
                        continue;
                    }
                }
            }
            if ((!CMLib.flags().canBeSeenBy(target, mob))
                || (!CMLib.flags().canBeHeardSpeakingBy(mob, target))
                || (target.location() != mob.location()))
                CMLib.commands().doCommandFail(mob, origCmds, L("'@x1' doesn't seem to be listening.", whomToOrder));
            else if (!target.willFollowOrdersOf(mob))
                CMLib.commands().doCommandFail(mob, origCmds, L("You can't order '@x1' around.", target.name(mob)));
            else {
                final CMMsg msg = CMClass.getMsg(mob, target, null, CMMsg.MSG_SPEAK, CMMsg.MSG_ORDER, CMMsg.MSG_SPEAK, L("^T<S-NAME> order(s) <T-NAMESELF> to '@x1'^?.", order));
                if ((mob.location().okMessage(mob, msg))) {
                    mob.location().send(mob, msg);
                    if ((msg.targetMinor() == CMMsg.TYP_ORDER) && (msg.target() == target))
                        doV.add(target);
                }
            }
        }
        for (int v = 0; v < doV.size(); v++) {
            target = doV.get(v);
            target.enqueCommand(new XVector<String>(commands), metaFlags | MUDCmdProcessor.METAFLAG_ORDER, 0);
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
