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
import com.planet_ink.game.MOBS.interfaces.Banker;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.ShopKeeper;

import java.util.List;
import java.util.Vector;


public class Borrow extends StdCommand {
    private final String[] access = I(new String[]{"BORROW"});

    public Borrow() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, "Borrow how much from whom?");
        if (shopkeeper == null)
            return false;
        final ShopKeeper SHOP = CMLib.coffeeShops().getShopKeeper(shopkeeper);
        if (!(SHOP instanceof Banker)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You can not borrow from @x1.", shopkeeper.name()));
            return false;
        }
        if (commands.size() == 0) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Borrow how much?"));
            return false;
        }
        String str = CMParms.combine(commands, 0);
        if (str.equalsIgnoreCase("all"))
            str = "" + Integer.MAX_VALUE;
        final long numCoins = CMLib.english().numPossibleGold(null, str);
        final String currency = CMLib.english().numPossibleGoldCurrency(shopkeeper, str);
        final double denomination = CMLib.english().numPossibleGoldDenomination(shopkeeper, currency, str);
        Item thisThang = null;
        if ((numCoins == 0) || (denomination == 0.0)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Borrow how much?"));
            return false;
        }
        thisThang = CMLib.beanCounter().makeCurrency(currency, denomination, numCoins);

        if ((thisThang == null) || (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("That doesn't appear to be available.  Try LIST."));
            return false;
        }
        final String str2 = "<S-NAME> borrow(s) <O-NAME> from " + shopkeeper.name() + ".";
        final CMMsg newMsg = CMClass.getMsg(mob, shopkeeper, thisThang, CMMsg.MSG_BORROW, str2);
        if (!mob.location().okMessage(mob, newMsg))
            return false;
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
        return false;
    }
}

