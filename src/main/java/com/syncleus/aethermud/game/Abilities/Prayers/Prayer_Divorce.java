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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.AetherTableRow;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Libraries.interfaces.ChannelsLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.Banker;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.LandTitle;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;


public class Prayer_Divorce extends Prayer {
    private final static String localizedName = CMLib.lang().L("Divorce");

    @Override
    public String ID() {
        return "Prayer_Divorce";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if (!target.isMarriedToLiege()) {
            mob.tell(L("@x1 is not married!", target.name(mob)));
            return false;
        }
        if (target.fetchItem(null, Wearable.FILTER_WORNONLY, "wedding band") != null) {
            mob.tell(L("@x1 must remove the wedding band first.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> divorce(s) <T-NAMESELF> from @x1.^?", target.getLiegeID()));
            if (mob.location().okMessage(mob, msg)) {
                if ((!target.isMonster()) && (target.soulMate() == null))
                    CMLib.aetherTables().bump(target, AetherTableRow.STAT_DIVORCES);
                mob.location().send(mob, msg);
                String maleName = target.Name();
                String femaleName = target.getLiegeID();
                if (target.charStats().getStat(CharStats.STAT_GENDER) == 'F') {
                    femaleName = target.Name();
                    maleName = target.getLiegeID();
                }
                final List<String> channels = CMLib.channels().getFlaggedChannelNames(ChannelsLibrary.ChannelFlag.DIVORCES);
                for (int i = 0; i < channels.size(); i++)
                    CMLib.commands().postChannel(channels.get(i), mob.clans(), L("@x1 and @x2 are now divorced.", maleName, femaleName), true);
                final MOB M = CMLib.players().getPlayer(target.getLiegeID());
                if (M != null)
                    M.setLiegeID("");
                target.setLiegeID("");
                try {
                    for (final Enumeration<Room> e = CMLib.map().rooms(); e.hasMoreElements(); ) {
                        final Room R = e.nextElement();
                        final LandTitle T = CMLib.law().getLandTitle(R);
                        if ((T != null) && (T.getOwnerName().equals(maleName))) {
                            T.setOwnerName(femaleName);
                            CMLib.database().DBUpdateRoom(R);
                        }
                        for (int i = 0; i < R.numInhabitants(); i++) {
                            final MOB M2 = R.fetchInhabitant(i);
                            if ((M2 != null) && (M2 instanceof Banker)) {
                                final Banker B = (Banker) M2;
                                final List<Item> allMaleItems = B.getDepositedItems(maleName);
                                Item coins = B.findDepositInventory(femaleName, "" + Integer.MAX_VALUE);
                                for (int v = 0; v < allMaleItems.size(); v++) {
                                    final Item I = allMaleItems.get(v);
                                    if ((I != null) && (I.container() == null)) {
                                        final List<Item> items = B.delDepositInventory(maleName, I);
                                        if (I instanceof Coins) {
                                            if (coins != null)
                                                B.delDepositInventory(femaleName, coins);
                                            else {
                                                coins = CMClass.getItem("StdCoins");
                                                ((Coins) coins).setNumberOfCoins(0);
                                            }
                                            ((Coins) coins).setNumberOfCoins(((Coins) coins).getNumberOfCoins() + Math.round(((Coins) I).getTotalValue() / ((Coins) coins).getDenomination()));
                                            B.addDepositInventory(femaleName, coins, null);
                                        } else
                                            for (Item oI : items)
                                                B.addDepositInventory(femaleName, oI, oI.container());
                                    }
                                }
                                for (int v = 0; v < allMaleItems.size(); v++) {
                                    final Item I = allMaleItems.get(v);
                                    if (I != null)
                                        I.destroy();
                                }
                            }
                        }
                    }
                } catch (final NoSuchElementException e) {
                }
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> clear(s) <S-HIS-HER> throat."));

        return success;
    }
}
