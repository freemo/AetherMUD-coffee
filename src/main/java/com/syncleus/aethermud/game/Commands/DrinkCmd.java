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
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;
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


public class DrinkCmd extends StdCommand {
    private final String[] access = I(new String[]{"DRINK", "DR", "DRI"});

    public DrinkCmd() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if ((commands.size() < 2) && (!(mob.location() instanceof Drink))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Drink what?"));
            return false;
        }
        commands.remove(0);
        if ((commands.size() > 1) && (commands.get(0).equalsIgnoreCase("from")))
            commands.remove(0);

        Environmental thisThang = null;
        if ((commands.size() == 0) && (mob.location() instanceof Drink))
            thisThang = mob.location();
        else {
            String drinkWhat = CMParms.combine(commands, 0);
            thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob, null, drinkWhat, Wearable.FILTER_ANY);
            if ((thisThang == null) && (drinkWhat.equalsIgnoreCase("here") || drinkWhat.equalsIgnoreCase("room")))
                thisThang = mob.location();
            if ((thisThang == null)
                || ((!mob.isMine(thisThang))
                && (!CMLib.flags().canBeSeenBy(thisThang, mob)))) {
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see '@x1' here.", CMParms.combine(commands, 0)));
                return false;
            }
        }
        String str = L("<S-NAME> take(s) a drink from <T-NAMESELF>.");
        Environmental tool = null;
        if ((thisThang instanceof Drink)
            && (((Drink) thisThang).liquidRemaining() > 0)
            && (((Drink) thisThang).liquidType() != RawMaterial.RESOURCE_FRESHWATER))
            str = L("<S-NAME> take(s) a drink of @x1 from <T-NAMESELF>.", RawMaterial.CODES.NAME(((Drink) thisThang).liquidType()).toLowerCase());
        else if (thisThang instanceof Container) {
            final List<Item> V = ((Container) thisThang).getContents();
            for (int v = 0; v < V.size(); v++) {
                final Item I = V.get(v);
                if ((I instanceof Drink) && (I instanceof RawMaterial)) {
                    tool = thisThang;
                    thisThang = I;
                    str = L("<S-NAME> take(s) a drink of <T-NAMESELF> from <O-NAMESELF>.");
                    break;
                }
            }
        }
        final CMMsg newMsg = CMClass.getMsg(mob, thisThang, tool, CMMsg.MSG_DRINK, str + CMLib.protocol().msp("drink.wav", 10));
        if (mob.location().okMessage(mob, newMsg))
            mob.location().send(mob, newMsg);
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
