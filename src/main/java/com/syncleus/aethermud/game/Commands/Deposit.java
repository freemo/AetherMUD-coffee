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
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.Banker;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.MOBS.interfaces.PostOffice;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;

import java.util.List;
import java.util.Vector;


public class Deposit extends StdCommand {
    private final String[] access = I(new String[]{"DEPOSIT"});

    public Deposit() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, "Deposit how much with whom?");
        final ShopKeeper SHOP = CMLib.coffeeShops().getShopKeeper(shopkeeper);
        if (shopkeeper == null)
            return false;
        if ((!(SHOP instanceof Banker)) && (!(SHOP instanceof PostOffice))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You can not deposit anything with @x1.", shopkeeper.name()));
            return false;
        }
        if (commands.size() == 0) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Deposit what or how much?"));
            return false;
        }
        final String thisName = CMParms.combine(commands, 0);
        Item thisThang = CMLib.english().bestPossibleGold(mob, null, thisName);
        if (thisThang == null) {
            thisThang = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, thisName);
            if ((thisThang == null) || (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to be carrying that."));
                return false;
            }
        } else if (((Coins) thisThang).getNumberOfCoins() < CMLib.english().numPossibleGold(mob, thisName))
            return false;
        CMMsg newMsg = null;
        if (SHOP instanceof Banker)
            newMsg = CMClass.getMsg(mob, shopkeeper, thisThang, CMMsg.MSG_DEPOSIT, L("<S-NAME> deposit(s) <O-NAME> into <S-HIS-HER> account with <T-NAMESELF>."));
        else
            newMsg = CMClass.getMsg(mob, shopkeeper, thisThang, CMMsg.MSG_DEPOSIT, L("<S-NAME> mail(s) <O-NAME>."));
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
        return false;
    }

}
