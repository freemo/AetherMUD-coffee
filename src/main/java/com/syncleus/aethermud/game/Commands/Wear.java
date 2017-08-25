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


public class Wear extends StdCommand {
    @SuppressWarnings("rawtypes")
    private final static Class[][] internalParameters = new Class[][]{
        {Item.class},
        {Item.class, Boolean.class},
        {Item.class, Integer.class},
        {Item.class, Integer.class, Boolean.class},
        {Item.class, String.class},
        {Item.class, String.class, Boolean.class},
    };
    private final String[] access = I(new String[]{"WEAR"});

    public Wear() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    public boolean wear(MOB mob, Item item, int locationIndex, boolean quiet) {
        String str = L("<S-NAME> put(s) on <T-NAME>.");
        int msgType = CMMsg.MSG_WEAR;
        if (item.rawProperLocationBitmap() == Wearable.WORN_HELD) {
            str = L("<S-NAME> hold(s) <T-NAME>.");
            msgType = CMMsg.MSG_HOLD;
        } else if ((item.rawProperLocationBitmap() == Wearable.WORN_WIELD)
            || (item.rawProperLocationBitmap() == (Wearable.WORN_HELD | Wearable.WORN_WIELD))) {
            str = L("<S-NAME> wield(s) <T-NAME>.");
            msgType = CMMsg.MSG_WIELD;
        } else if (locationIndex != 0)
            str = L("<S-NAME> put(s) <T-NAME> on <S-HIS-HER> @x1.", Wearable.CODES.NAME(locationIndex).toLowerCase());
        final CMMsg newMsg = CMClass.getMsg(mob, item, null, msgType, quiet ? null : str);
        newMsg.setValue(locationIndex);
        if (mob.location().okMessage(mob, newMsg)) {
            mob.location().send(mob, newMsg);
            return true;
        }
        return false;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Wear what?"));
            return false;
        }
        commands.remove(0);

        // discover if a wear location was specified
        int wearLocationIndex = 0;
        for (int i = commands.size() - 2; i > 0; i--) {
            if (commands.get(i).equalsIgnoreCase("on")) {
                if ((i < commands.size() - 2) && commands.get(i + 1).equalsIgnoreCase("my"))
                    commands.remove(i + 1);
                final String possibleWearLocation = CMParms.combine(commands, i + 1).toLowerCase().trim();
                int possIndex = CMParms.indexOfIgnoreCase(Wearable.CODES.NAMES(), possibleWearLocation);
                if (possIndex < 0)
                    possIndex = Wearable.CODES.FINDDEX_endsWith(" " + possibleWearLocation);
                if (possIndex > 0) {
                    wearLocationIndex = possIndex;
                    while (commands.size() > i)
                        commands.remove(commands.size() - 1);
                    break;
                } else {
                    CMLib.commands().doCommandFail(mob, origCmds, L("You can't wear anything on your '@x1'", possibleWearLocation));
                    return false;
                }
                // will always break out here, one way or the other.
            }
        }
        final List<Item> items = CMLib.english().fetchItemList(mob, mob, null, commands, Wearable.FILTER_UNWORNONLY, false);
        if (items.size() == 0)
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to be carrying that."));
        else {
            // sort hold-onlys down.
            Item I = null;
            for (int i = items.size() - 2; i >= 0; i--) {
                I = items.get(i);
                if (I.rawProperLocationBitmap() == Wearable.WORN_HELD) {
                    items.remove(i);
                    items.add(I);
                }
            }
            for (int i = 0; i < items.size(); i++) {
                I = items.get(i);
                if ((items.size() == 1) || (I.canWear(mob, 0)))
                    wear(mob, I, wearLocationIndex, false);
            }
        }
        return false;
    }

    @Override
    public Object executeInternal(MOB mob, int metaFlags, Object... args) throws java.io.IOException {
        if (!super.checkArguments(internalParameters, args))
            return Boolean.FALSE;
        final Wearable.CODES codes = Wearable.CODES.instance();
        final Item targetWearI = (Item) args[0];
        boolean quietly = false;
        int wearLocationIndex = 0;
        for (int i = 1; i < args.length; i++) {
            if (args[i] instanceof String) {
                final int newDex = codes.findDex_ignoreCase((String) args[i]);
                if (newDex > 0) {
                    wearLocationIndex = newDex;
                }
            } else if (args[i] instanceof Integer) {
                wearLocationIndex = ((Integer) args[i]).intValue();
            } else if (args[i] instanceof Boolean) {
                quietly = ((Boolean) args[i]).booleanValue();
            }
        }
        return Boolean.valueOf(wear(mob, targetWearI, wearLocationIndex, quietly));
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
