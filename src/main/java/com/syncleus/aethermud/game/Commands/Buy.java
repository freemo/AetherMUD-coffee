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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;

import java.util.List;
import java.util.Vector;


public class Buy extends StdCommand {
    private final String[] access = I(new String[]{"BUY"});

    public Buy() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        MOB mobFor = null;
        Vector<String> origCmds = new XVector<String>(commands);
        if ((commands.size() > 2)
            && (commands.get(commands.size() - 2).equalsIgnoreCase("for"))) {
            final MOB M = mob.location().fetchInhabitant(commands.get(commands.size() - 1));
            if (M == null) {
                CMLib.commands().doCommandFail(mob, origCmds, L("There is noone called '@x1' here.", (commands.get(commands.size() - 1))));
                return false;
            }
            commands.remove(commands.size() - 1);
            commands.remove(commands.size() - 1);
            mobFor = M;
        }

        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, "Buy what from whom?");
        if (shopkeeper == null)
            return false;
        if (commands.size() == 0) {
            CMLib.commands().doCommandFail(mob, origCmds, L("Buy what?"));
            return false;
        }
        if (CMLib.aetherShops().getShopKeeper(shopkeeper) == null) {
            mob.tell(L("@x1 is not a shopkeeper!", shopkeeper.name()));
            return false;
        }

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
        String forName = "";
        if ((mobFor != null) && (mobFor != mob)) {
            if (mobFor.name().indexOf(' ') >= 0)
                forName = " for '" + mobFor.Name() + "'";
            else
                forName = " for " + mobFor.Name();
        }

        if (V.size() == 0)
            mob.tell(mob, shopkeeper, null, L("<T-NAME> do(es)n't appear to have any '@x1' for sale.  Try LIST.", whatName));
        else
            for (int v = 0; v < V.size(); v++) {
                final Environmental thisThang = V.get(v);
                final CMMsg newMsg = CMClass.getMsg(mob, shopkeeper, thisThang, CMMsg.MSG_BUY, L("<S-NAME> buy(s) <O-NAME> from <T-NAMESELF>@x1.", forName));
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
