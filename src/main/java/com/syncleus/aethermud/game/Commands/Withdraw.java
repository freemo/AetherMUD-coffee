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
import com.syncleus.aethermud.game.Common.interfaces.Clan;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Items.interfaces.Item;
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


public class Withdraw extends StdCommand {
    private final String[] access = I(new String[]{"WITHDRAW"});

    public Withdraw() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, "Withdraw what or how much from whom?");
        if (shopkeeper == null)
            return false;
        final ShopKeeper SHOP = CMLib.aetherShops().getShopKeeper(shopkeeper);
        if ((!(SHOP instanceof Banker)) && (!(SHOP instanceof PostOffice))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You can not withdraw anything from @x1.", shopkeeper.name()));
            return false;
        }
        if (commands.size() == 0) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Withdraw what or how much?"));
            return false;
        }
        String str = CMParms.combine(commands, 0);
        if (str.equalsIgnoreCase("all"))
            str = "" + Integer.MAX_VALUE;
        final long numCoins = CMLib.english().numPossibleGold(null, str);
        final String currency = CMLib.english().numPossibleGoldCurrency(shopkeeper, str);
        final double denomination = CMLib.english().numPossibleGoldDenomination(shopkeeper, currency, str);
        Item thisThang = null;
        if (SHOP instanceof Banker) {
            final String accountName = ((Banker) SHOP).getBankClientName(mob, Clan.Function.WITHDRAW, false);
            if (numCoins > 0) {
                if (denomination == 0.0) {
                    CMLib.commands().doCommandFail(mob, origCmds, L("Withdraw how much?"));
                    return false;
                }
                thisThang = ((Banker) SHOP).findDepositInventory(accountName, "" + Integer.MAX_VALUE);
                if (thisThang instanceof Coins)
                    thisThang = CMLib.moneyCounter().makeCurrency(currency, denomination, numCoins);
            } else
                thisThang = ((Banker) SHOP).findDepositInventory(accountName, str);

            if (((thisThang == null) || ((thisThang instanceof Coins) && (((Coins) thisThang).getNumberOfCoins() <= 0)))
                && (!((Banker) SHOP).isSold(ShopKeeper.DEAL_CLANBANKER))
                && (mob.isMarriedToLiege())) {
                final MOB mob2 = CMLib.players().getPlayer(mob.getLiegeID());
                if (mob2 != null) {
                    final String accountName2 = ((Banker) SHOP).getBankClientName(mob2, Clan.Function.WITHDRAW, false);
                    if (numCoins > 0) {
                        thisThang = ((Banker) SHOP).findDepositInventory(accountName2, "" + Integer.MAX_VALUE);
                        if (thisThang instanceof Coins)
                            thisThang = CMLib.moneyCounter().makeCurrency(currency, denomination, numCoins);
                        else {
                            CMLib.commands().doCommandFail(mob, origCmds, L("Withdraw how much?"));
                            return false;
                        }
                    } else
                        thisThang = ((Banker) SHOP).findDepositInventory(accountName2, str);
                }
            }
        } else if (SHOP instanceof PostOffice) {
            final String accountName = ((PostOffice) SHOP).getSenderName(mob, Clan.Function.WITHDRAW, false);
            thisThang = ((PostOffice) SHOP).findBoxContents(accountName, str);
            if ((thisThang == null)
                && (!((PostOffice) SHOP).isSold(ShopKeeper.DEAL_CLANPOSTMAN))
                && (mob.isMarriedToLiege())) {
                final MOB mob2 = CMLib.players().getPlayer(mob.getLiegeID());
                if (mob2 != null) {
                    final String accountName2 = ((PostOffice) SHOP).getSenderName(mob, Clan.Function.WITHDRAW, false);
                    thisThang = ((PostOffice) SHOP).findBoxContents(accountName2, str);
                }
            }
        }

        if ((thisThang == null) || (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("That doesn't appear to be available.  Try LIST."));
            return false;
        }
        String str2 = "<S-NAME> withdraw(s) <O-NAME> from <S-HIS-HER> account with " + shopkeeper.name() + ".";
        if (SHOP instanceof PostOffice)
            str2 = "<S-NAME> withdraw(s) <O-NAME> from <S-HIS-HER> postal box with " + shopkeeper.name() + ".";
        final CMMsg newMsg = CMClass.getMsg(mob, shopkeeper, thisThang, CMMsg.MSG_WITHDRAW, str2);
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
