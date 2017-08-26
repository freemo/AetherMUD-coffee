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
import com.syncleus.aethermud.game.MOBS.interfaces.Auctioneer;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.collections.Triad;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;

import java.util.List;
import java.util.Vector;


public class Bid extends StdCommand {
    private final String[] access = I(new String[]{"BID"});

    public Bid() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, "Bid how much, on what, with whom?");
        if (shopkeeper == null)
            return false;
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Bid how much on what?"));
            return false;
        }
        if (!(CMLib.aetherShops().getShopKeeper(shopkeeper) instanceof Auctioneer)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("@x1 is not an auctioneer!", shopkeeper.name()));
            return false;
        }

        String bidStr = commands.get(0);
        if (CMLib.english().numPossibleGold(mob, bidStr) <= 0) {
            CMLib.commands().doCommandFail(mob, origCmds, L("It does not look like '@x1' is enough to offer.", bidStr));
            return false;
        }
        final Triad<String, Double, Long> bidThang = CMLib.english().parseMoneyStringSDL(mob, bidStr, null);
        bidStr = CMLib.moneyCounter().nameCurrencyShort(bidThang.first, CMath.mul(bidThang.second.doubleValue(), bidThang.third.longValue()));
        commands.remove(0);

        int maxToDo = Integer.MAX_VALUE;
        if ((commands.size() > 1)
            && (CMath.s_int(commands.get(0)) > 0)) {
            maxToDo = CMath.s_int(commands.get(0));
            commands.set(0, "all");
        }

        String whatName = CMParms.combine(commands, 0);
        final Vector<Environmental> V = new Vector<Environmental>();
        boolean allFlag = commands.get(0).equalsIgnoreCase("all");
        if (whatName.toUpperCase().startsWith("ALL.")) {
            allFlag = true;
            whatName = "ALL " + whatName.substring(4);
        }
        if (whatName.toUpperCase().endsWith(".ALL")) {
            allFlag = true;
            whatName = "ALL " + whatName.substring(0, whatName.length() - 4);
        }
        int addendum = 1;
        boolean doBugFix = true;
        while (doBugFix || ((allFlag) && (addendum <= maxToDo))) {
            doBugFix = false;
            final ShopKeeper SK = CMLib.aetherShops().getShopKeeper(shopkeeper);
            final Environmental itemToDo = SK.getShop().getStock(whatName, mob);
            if (itemToDo == null)
                break;
            if (CMLib.flags().canBeSeenBy(itemToDo, mob))
                V.add(itemToDo);
            if (addendum >= CMLib.aetherShops().getShopKeeper(shopkeeper).getShop().numberInStock(itemToDo))
                break;
            ++addendum;
        }
        if (V.size() == 0)
            mob.tell(mob, shopkeeper, null, L("<T-NAME> do(es)n't appear to have any '@x1' available for auction.  Try LIST.", whatName));
        else
            for (int v = 0; v < V.size(); v++) {
                final Environmental thisThang = V.get(v);
                final CMMsg newMsg = CMClass.getMsg(mob, shopkeeper, thisThang,
                    CMMsg.MSG_BID, L("<S-NAME> bid(s) @x1 on <O-NAME> with <T-NAMESELF>.", bidStr),
                    CMMsg.MSG_BID, L("<S-NAME> bid(s) '@x1' on <O-NAME> with <T-NAMESELF>.", bidStr),
                    CMMsg.MSG_BID, L("<S-NAME> place(s) a bid with <T-NAMESELF>."));
                if (mob.location().okMessage(mob, newMsg))
                    mob.location().send(mob, newMsg);
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
        return false;
    }
}
