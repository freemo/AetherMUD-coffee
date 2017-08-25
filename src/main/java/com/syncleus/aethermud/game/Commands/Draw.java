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
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


public class Draw extends Get {
    private final String[] access = I(new String[]{"DRAW"});

    public Draw() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    public Vector<Container> getSheaths(MOB mob) {
        final Vector<Container> sheaths = new Vector<Container>();
        if (mob != null)
            for (int i = 0; i < mob.numItems(); i++) {
                final Item I = mob.getItem(i);
                if ((I != null)
                    && (!I.amWearingAt(Wearable.IN_INVENTORY))
                    && (I instanceof Container)
                    && (((Container) I).capacity() > 0)
                    && (((Container) I).containTypes() != Container.CONTAIN_ANYTHING)) {
                    final List<Item> contents = ((Container) I).getContents();
                    for (int c = 0; c < contents.size(); c++) {
                        if (contents.get(c) instanceof Weapon) {
                            sheaths.add((Container) I);
                            break;
                        }
                    }
                }
            }
        return sheaths;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        boolean quiet = false;
        boolean noerrors = false;
        boolean ifNecessary = false;
        Vector<String> origCmds = new XVector<String>(commands);
        if ((commands.size() > 0) && (commands.get(commands.size() - 1).equalsIgnoreCase("IFNECESSARY"))) {
            quiet = true;
            noerrors = true;
            commands.remove(commands.size() - 1);
            if ((commands.size() > 0)
                && (commands.get(commands.size() - 1).equalsIgnoreCase("HELD"))) {
                commands.remove(commands.size() - 1);
                if (mob.fetchHeldItem() != null)
                    return false;
            } else if (mob.fetchWieldedItem() != null)
                return false;
        } else {
            if ((commands.size() > 0) && (commands.get(commands.size() - 1).equalsIgnoreCase("QUIETLY"))) {
                commands.remove(commands.size() - 1);
                quiet = true;
            }
            if ((commands.size() > 0) && (commands.get(commands.size() - 1).equalsIgnoreCase("IFNECESSARY"))) {
                ifNecessary = true;
                commands.remove(commands.size() - 1);
                noerrors = true;
            }
        }

        boolean allFlag = false;
        List<Container> containers = new Vector<Container>();
        String containerName = "";
        String whatToGet = "";
        int c = 0;
        final Vector<Container> sheaths = getSheaths(mob);
        if (commands.size() > 0)
            commands.remove(0);
        if (commands.size() == 0) {
            if (sheaths.size() > 0)
                containerName = ((Item) sheaths.get(0)).name();
            else
                containerName = "a weapon";
            for (int i = 0; i < mob.numItems(); i++) {
                final Item I = mob.getItem(i);
                if ((I instanceof Weapon)
                    && (I.container() != null)
                    && (sheaths.contains(I.container()))) {
                    containers.add(I.container());
                    whatToGet = I.name();
                    break;
                }
            }
            if (whatToGet.length() == 0) {
                for (int i = 0; i < mob.numItems(); i++) {
                    final Item I = mob.getItem(i);
                    if (I instanceof Weapon) {
                        whatToGet = I.name();
                        break;
                    }
                }
            }
        } else {
            containerName = commands.get(commands.size() - 1);
            commands.add(0, "all");
            containers = CMLib.english().possibleContainers(mob, commands, Wearable.FILTER_WORNONLY, true);
            if (containers.size() == 0)
                containers = sheaths;
            whatToGet = CMParms.combine(commands, 0);
            allFlag = commands.get(0).equalsIgnoreCase("all");
            if (whatToGet.toUpperCase().startsWith("ALL.")) {
                allFlag = true;
                whatToGet = "ALL " + whatToGet.substring(4);
            }
            if (whatToGet.toUpperCase().endsWith(".ALL")) {
                allFlag = true;
                whatToGet = "ALL " + whatToGet.substring(0, whatToGet.length() - 4);
            }
        }
        boolean doneSomething = false;
        while ((c < containers.size()) || (containers.size() == 0)) {
            final Vector<Weapon> V = new Vector<Weapon>();
            Container container = null;
            if (containers.size() > 0)
                container = containers.get(c++);
            int addendum = 1;
            String addendumStr = "";
            boolean doBugFix = true;
            while (doBugFix || allFlag) {
                doBugFix = false;
                Environmental getThis = null;
                if ((container != null) && (mob.isMine(container)))
                    getThis = mob.findItem(container, whatToGet + addendumStr);
                if (getThis == null)
                    break;
                if (getThis instanceof Weapon)
                    V.add((Weapon) getThis);
                addendumStr = "." + (++addendum);
            }

            for (int i = 0; i < V.size(); i++) {
                final Item getThis = V.get(i);
                long wearCode = 0;
                if (container != null)
                    wearCode = container.rawWornCode();
                if ((ifNecessary)
                    && (mob.freeWearPositions(Wearable.WORN_WIELD, (short) 0, (short) 0) == 0)
                    && (mob.freeWearPositions(Wearable.WORN_HELD, (short) 0, (short) 0) == 0))
                    break;
                if (get(mob, container, getThis, quiet, "draw", false)) {
                    if (getThis.container() == null) {
                        if (mob.freeWearPositions(Wearable.WORN_WIELD, (short) 0, (short) 0) == 0) {
                            final CMMsg newMsg = CMClass.getMsg(mob, getThis, null, CMMsg.MSG_HOLD, null);
                            if (mob.location().okMessage(mob, newMsg))
                                mob.location().send(mob, newMsg);
                        } else {
                            final CMMsg newMsg = CMClass.getMsg(mob, getThis, null, CMMsg.MSG_WIELD, null);
                            if (mob.location().okMessage(mob, newMsg))
                                mob.location().send(mob, newMsg);
                        }
                    }
                }
                if (container != null)
                    container.setRawWornCode(wearCode);
                doneSomething = true;
            }

            if (containers.size() == 0)
                break;
        }
        if ((!doneSomething) && (!noerrors)) {
            if (containers.size() > 0) {
                final Container container = containers.get(0);
                if (container.isOpen())
                    CMLib.commands().doCommandFail(mob, origCmds, L("You don't see that in @x1.", container.name()));
                else
                    CMLib.commands().doCommandFail(mob, origCmds, L("@x1 is closed.", container.name()));
            } else
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see @x1 here.", containerName));
        }
        return false;
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
