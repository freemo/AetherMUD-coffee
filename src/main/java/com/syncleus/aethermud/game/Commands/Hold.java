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
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.collections.XVector;

import java.util.List;
import java.util.Vector;


public class Hold extends StdCommand {
    private final String[] access = I(new String[]{"HOLD", "HOL", "HO", "H"});

    public Hold() {
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
            CMLib.commands().doCommandFail(mob, origCmds, L("Hold what?"));
            return false;
        }
        commands.remove(0);
        final List<Item> items = CMLib.english().fetchItemList(mob, mob, null, commands, Wearable.FILTER_UNWORNONLY, false);
        if (items.size() == 0)
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to be carrying that."));
        else
            for (int i = 0; i < items.size(); i++) {
                if ((items.size() == 1) || (items.get(i).canWear(mob, Wearable.WORN_HELD))) {
                    final Item item = items.get(i);
                    int msgType = CMMsg.MSG_HOLD;
                    String str = L("<S-NAME> hold(s) <T-NAME>.");
                    if ((mob.freeWearPositions(Wearable.WORN_WIELD, (short) 0, (short) 0) > 0)
                        && ((item.rawProperLocationBitmap() == Wearable.WORN_WIELD)
                        || (item.rawProperLocationBitmap() == (Wearable.WORN_HELD | Wearable.WORN_WIELD)))) {
                        str = L("<S-NAME> wield(s) <T-NAME>.");
                        msgType = CMMsg.MSG_WIELD;
                    }
                    final CMMsg newMsg = CMClass.getMsg(mob, item, null, msgType, str);
                    if (mob.location().okMessage(mob, newMsg))
                        mob.location().send(mob, newMsg);
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
