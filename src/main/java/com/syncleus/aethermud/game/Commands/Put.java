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
import com.syncleus.aethermud.game.Items.interfaces.*;
import com.syncleus.aethermud.game.Libraries.interfaces.CMFlagLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Rideable;

import java.util.List;
import java.util.Vector;


public class Put extends StdCommand {
    private final String[] access = I(new String[]{"PUT", "PU", "P"});

    public Put() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    public void putout(MOB mob, List<String> commands, boolean quiet) {
        List<String> origCmds = new XVector<String>(commands);
        final Room R = mob.location();
        if ((commands.size() < 3) || (R == null)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Put out what?"));
            return;
        }
        commands.remove(1);
        commands.remove(0);

        final List<Item> items = CMLib.english().fetchItemList(mob, mob, null, commands, Wearable.FILTER_UNWORNONLY, true);
        if (items.size() == 0) {
            Item I = R.findItem(null, CMParms.combine(commands, 0));
            if (I != null)
                items.add(I);
        }
        if (items.size() == 0)
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to be carrying that."));
        else
            for (int i = 0; i < items.size(); i++) {
                final Item I = items.get(i);
                if ((items.size() == 1) || (I instanceof Light)) {
                    final CMMsg msg = CMClass.getMsg(mob, I, null, CMMsg.MSG_EXTINGUISH, quiet ? null : L("<S-NAME> put(s) out <T-NAME>."));
                    if (R.okMessage(mob, msg))
                        R.send(mob, msg);
                }
            }
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Put what where?"));
            return false;
        }

        if (commands.get(commands.size() - 1).equalsIgnoreCase("on")) {
            commands.remove(commands.size() - 1);
            final Command C = CMClass.getCommand("Wear");
            if (C != null)
                C.execute(mob, commands, metaFlags);
            return false;
        }

        if (commands.size() >= 4) {
            final String s = CMParms.combine(commands, 0).toLowerCase();
            final Wearable.CODES codes = Wearable.CODES.instance();
            for (int i = 1; i < codes.total(); i++) {
                if (s.endsWith(" on " + codes.name(i).toLowerCase()) || s.endsWith(" on my " + codes.name(i).toLowerCase())) {
                    final Command C = CMClass.getCommand("Wear");
                    if (C != null)
                        C.execute(mob, commands, metaFlags);
                    return false;
                }
            }
        }

        if (commands.get(1).equalsIgnoreCase("on")) {
            commands.remove(1);
            final Command C = CMClass.getCommand("Wear");
            if (C != null)
                C.execute(mob, commands, metaFlags);
            return false;
        }

        if (commands.get(1).equalsIgnoreCase("out")) {
            putout(mob, commands, false);
            return false;
        }

        commands.remove(0);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Where should I put the @x1", commands.get(0)));
            return false;
        }

        final Room R = mob.location();
        if (R == null)
            return false;

        String containerName = commands.get(commands.size() - 1);
        Environmental container = CMLib.english().possibleContainer(mob, commands, false, Wearable.FILTER_ANY);
        if (container == null) {
            container = R.fetchFromMOBRoomFavorsItems(mob, null, containerName, Wearable.FILTER_UNWORNONLY);
            commands.remove(commands.size() - 1);
        }
        if (container == null) {
            final CMFlagLibrary flagLib = CMLib.flags();
            for (int i = 0; i < R.numItems(); i++) {
                final Item I = R.getItem(i);
                if (flagLib.isOpenAccessibleContainer(I)) {
                    Physical P = R.fetchFromRoomFavorItems(I, containerName);
                    if (P instanceof Container) {
                        container = P;
                        break;
                    }
                }
            }
        }
        if ((container == null) || (!CMLib.flags().canBeSeenBy(container, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("I don't see a @x1 here.", containerName));
            return false;
        }

        final int maxToPut = CMLib.english().calculateMaxToGive(mob, commands, true, mob, false);
        if (maxToPut < 0)
            return false;

        String thingToPut = CMParms.combine(commands, 0);
        int addendum = 1;
        String addendumStr = "";
        final Vector<Item> V = new Vector<Item>();
        boolean allFlag = (commands.size() > 0) ? commands.get(0).equalsIgnoreCase("all") : false;
        if (thingToPut.toUpperCase().startsWith("ALL.")) {
            allFlag = true;
            thingToPut = "ALL " + thingToPut.substring(4);
        }
        if (thingToPut.toUpperCase().endsWith(".ALL")) {
            allFlag = true;
            thingToPut = "ALL " + thingToPut.substring(0, thingToPut.length() - 4);
        }
        final boolean onlyGoldFlag = mob.hasOnlyGoldInInventory();
        Item putThis = CMLib.english().bestPossibleGold(mob, null, thingToPut);
        if (putThis != null) {
            if (((Coins) putThis).getNumberOfCoins() < CMLib.english().numPossibleGold(mob, thingToPut))
                return false;
            if (CMLib.flags().canBeSeenBy(putThis, mob))
                V.add(putThis);
        }
        boolean doBugFix = true;
        if (V.size() == 0)
            while (doBugFix || ((allFlag) && (addendum <= maxToPut))) {
                doBugFix = false;
                putThis = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, thingToPut + addendumStr);
                if ((allFlag) && (!onlyGoldFlag) && (putThis instanceof Coins) && (thingToPut.equalsIgnoreCase("ALL")))
                    putThis = null;
                else {
                    if (putThis == null)
                        break;
                    if ((CMLib.flags().canBeSeenBy(putThis, mob))
                        && (!V.contains(putThis)))
                        V.add(putThis);
                }
                addendumStr = "." + (++addendum);
            }

        if (V.contains(container))
            V.remove(container);

        if (V.size() == 0)
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to be carrying that."));
        else
            for (int i = 0; i < V.size(); i++) {
                putThis = V.get(i);
                final String putWord = (container instanceof Rideable) ? ((Rideable) container).putString(mob) : "in";
                final CMMsg putMsg = CMClass.getMsg(mob, container, putThis, CMMsg.MASK_OPTIMIZE | CMMsg.MSG_PUT, L("<S-NAME> put(s) <O-NAME> @x1 <T-NAME>.", putWord));
                if (R.okMessage(mob, putMsg))
                    R.send(mob, putMsg);
                if (putThis instanceof Coins)
                    ((Coins) putThis).putCoinsBack();
                if (putThis instanceof RawMaterial)
                    ((RawMaterial) putThis).rebundle();
            }
        R.recoverRoomStats();
        R.recoverRoomStats();
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
