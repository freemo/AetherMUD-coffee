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

import com.planet_ink.game.Commands.interfaces.Command;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.*;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Drink;
import com.planet_ink.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


public class Empty extends Drop {
    private final String[] access = I(new String[]{"EMPTY", "EMP"});

    public Empty() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        String whatToDrop = null;
        Environmental target = mob;
        final Vector<Item> V = new Vector<Item>();
        if (commands.size() < 2) {
            mob.tell(L("Empty what where?"));
            return false;
        }
        commands.remove(0);
        if (commands.size() > 1) {
            final String s = commands.get(commands.size() - 1);
            if (s.equalsIgnoreCase("here"))
                target = mob.location();
            else if (s.equalsIgnoreCase("me"))
                target = mob;
            else if (s.equalsIgnoreCase("self"))
                target = mob;
            else if ("INVENTORY".startsWith(s.toUpperCase()))
                target = mob;
            else if (s.equalsIgnoreCase("floor"))
                target = mob.location();
            else if (s.equalsIgnoreCase("ground"))
                target = mob.location();
            else {
                target = CMLib.english().possibleContainer(mob, commands, false, Wearable.FILTER_UNWORNONLY);
                if (target == null)
                    target = mob.location().fetchFromRoomFavorItems(null, s);
                else
                    commands.add("delme");
            }
            if (target != null)
                commands.remove(commands.size() - 1);
        }

        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("Empty it where?"));
            return false;
        }

        final int maxToDrop = CMLib.english().calculateMaxToGive(mob, commands, true, mob, false);
        if (maxToDrop < 0)
            return false;

        whatToDrop = CMParms.combine(commands, 0);
        boolean allFlag = (commands.size() > 0) ? commands.get(0).equalsIgnoreCase("all") : false;
        if (whatToDrop.toUpperCase().startsWith("ALL.")) {
            allFlag = true;
            whatToDrop = "ALL " + whatToDrop.substring(4);
        }
        if (whatToDrop.toUpperCase().endsWith(".ALL")) {
            allFlag = true;
            whatToDrop = "ALL " + whatToDrop.substring(0, whatToDrop.length() - 4);
        }
        int addendum = 1;
        String addendumStr = "";
        Drink drink = null;
        boolean doBugFix = true;
        while (doBugFix || ((allFlag) && (addendum <= maxToDrop))) {
            doBugFix = false;
            Item dropThis = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, whatToDrop + addendumStr);
            if ((dropThis == null)
                && (V.size() == 0)
                && (addendumStr.length() == 0)
                && (!allFlag)) {
                dropThis = mob.fetchItem(null, Wearable.FILTER_WORNONLY, whatToDrop);
                if ((dropThis != null) && (dropThis instanceof Container)) {
                    if ((!dropThis.amWearingAt(Wearable.WORN_HELD)) && (!dropThis.amWearingAt(Wearable.WORN_WIELD))) {
                        mob.tell(L("You must remove that first."));
                        return false;
                    }
                    final CMMsg newMsg = CMClass.getMsg(mob, dropThis, null, CMMsg.MSG_REMOVE, null);
                    if (mob.location().okMessage(mob, newMsg))
                        mob.location().send(mob, newMsg);
                    else
                        return false;
                }
            }
            if (dropThis == null)
                break;
            if (dropThis instanceof Drink)
                drink = (Drink) dropThis;
            if ((CMLib.flags().canBeSeenBy(dropThis, mob))
                && (dropThis instanceof Container)
                && (!V.contains(dropThis)))
                V.add(dropThis);
            addendumStr = "." + (++addendum);
        }

        String str = L("<S-NAME> empt(ys) <T-NAME>");
        if (target instanceof Room)
            str += " here.";
        else if (target instanceof MOB)
            str += ".";
        else str += " into " + target.Name() + ".";

        if ((V.size() == 0) && (drink != null)) {
            mob.tell(L("@x1 must be POURed out.", drink.name()));
            return false;
        }

        if (V.size() == 0)
            mob.tell(L("You don't seem to be carrying that."));
        else if ((V.size() == 1) && (V.get(0) == target))
            mob.tell(L("You can't empty something into itself!"));
        else if ((V.size() == 1)
            && (V.get(0) instanceof Drink)
            && (!((Drink) V.get(0)).containsDrink())
            )
            mob.tell(mob, V.get(0), null, L("<T-NAME> is already empty."));
        else
            for (int v = 0; v < V.size(); v++) {
                final Container C = (Container) V.get(v);
                if (C == target)
                    continue;

                boolean skipMessage = false;
                if ((C instanceof Drink) && (((Drink) C).containsDrink())) {
                    if (target instanceof Drink) {
                        final Command C2 = CMClass.getCommand("Pour");
                        C2.execute(mob, new XVector<String>("POUR", "$" + C.Name() + "$", "$" + target.Name() + "$"), metaFlags);
                        skipMessage = true;
                    } else {
                        ((Drink) C).setLiquidRemaining(0);
                        if (((Drink) C).disappearsAfterDrinking())
                            C.destroy();
                    }
                }
                final CMMsg msg = CMClass.getMsg(mob, C, CMMsg.MSG_QUIETMOVEMENT, str);
                final Room R = mob.location();
                if (skipMessage || (R.okMessage(mob, msg))) {
                    if (!skipMessage)
                        R.send(mob, msg);
                    final List<Item> V2 = C.getContents();
                    for (int v2 = 0; v2 < V2.size(); v2++) {
                        final Item I = V2.get(v2);
                        if (I instanceof Coins)
                            ((Coins) I).setContainer(null);

                        if (((I.container() == null) || (Get.get(mob, C, I, true, null, true)))
                            && (I.container() == null)) {
                            if (target instanceof Room)
                                drop(mob, I, true, true, false);
                            else if (target instanceof Container) {
                                final CMMsg putMsg = CMClass.getMsg(mob, target, I, CMMsg.MASK_OPTIMIZE | CMMsg.MSG_PUT, null);
                                if (R.okMessage(mob, putMsg))
                                    R.send(mob, putMsg);
                            }
                            if (I instanceof Coins)
                                ((Coins) I).putCoinsBack();
                            if (I instanceof RawMaterial)
                                ((RawMaterial) I).rebundle();
                        }
                    }
                }
            }
        mob.location().recoverRoomStats();
        mob.location().recoverRoomStats();
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
