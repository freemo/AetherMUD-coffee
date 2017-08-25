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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Clan;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.Banker;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public class Thief_Embezzle extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Embezzle");
    private static final String[] triggerStrings = I(new String[]{"EMBEZZLE"});
    private final LinkedList<Pair<MOB, Integer>> lastOnes = new LinkedList<Pair<MOB, Integer>>();
    public List<MOB> mobs = new Vector<MOB>();

    @Override
    public String ID() {
        return "Thief_Embezzle";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_CRIMINAL;
    }

    @Override
    public boolean disregardsArmorCheck(MOB mob) {
        return true;
    }

    protected int timesPicked(MOB target) {
        int times = 0;
        for (final Iterator<Pair<MOB, Integer>> p = lastOnes.iterator(); p.hasNext(); ) {
            final Pair<MOB, Integer> P = p.next();
            final MOB M = P.first;
            final Integer I = P.second;
            if (M == target) {
                times = I.intValue();
                p.remove();
                break;
            }
        }
        if (lastOnes.size() >= 50)
            lastOnes.removeFirst();
        lastOnes.add(new Pair<MOB, Integer>(target, Integer.valueOf(times + 1)));
        return times + 1;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(affected))
            && (mobs.contains(msg.source()))) {
            if ((msg.targetMinor() == CMMsg.TYP_BUY)
                || (msg.targetMinor() == CMMsg.TYP_BID)
                || (msg.targetMinor() == CMMsg.TYP_SELL)
                || (msg.targetMinor() == CMMsg.TYP_LIST)
                || (msg.targetMinor() == CMMsg.TYP_VALUE)
                || (msg.targetMinor() == CMMsg.TYP_VIEW)) {
                msg.source().tell(L("@x1 looks unwilling to do business with you.", affected.name()));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((commands.size() < 1) && (givenTarget == null)) {
            mob.tell(L("Embezzle money from whose accounts?"));
            return false;
        }
        MOB target = null;
        if ((givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        else
            target = mob.location().fetchInhabitant(CMParms.combine(commands, 0));
        if ((target == null) || (target.amDead()) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", CMParms.combine(commands, 1)));
            return false;
        }
        if (!(target instanceof Banker)) {
            mob.tell(L("You can't embezzle from @x1's accounts.", target.name(mob)));
            return false;
        }
        if (mob.isInCombat()) {
            mob.tell(L("You are too busy to embezzle."));
            return false;
        }
        final Banker bank = (Banker) target;
        final Ability A = target.fetchEffect(ID());
        if (A != null) {
            mob.tell(L("@x1 is watching @x2 books too closely.", target.name(mob), target.charStats().hisher()));
            return false;
        }
        final int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));

        if (!target.mayIFight(mob)) {
            mob.tell(L("You cannot embezzle from @x1.", target.charStats().himher()));
            return false;
        }

        Item myCoins = null;
        String myAcct = mob.Name();
        if (bank.isSold(ShopKeeper.DEAL_CLANBANKER)) {
            Pair<Clan, Integer> clanPair = CMLib.clans().findPrivilegedClan(mob, Clan.Function.WITHDRAW);
            if (clanPair == null)
                clanPair = CMLib.clans().findPrivilegedClan(mob, Clan.Function.DEPOSIT_LIST);
            if (clanPair == null)
                clanPair = CMLib.clans().findPrivilegedClan(mob, Clan.Function.DEPOSIT);
            if (clanPair != null)
                myAcct = clanPair.first.clanID();
        }
        myCoins = bank.findDepositInventory(myAcct, "1");
        if ((myCoins == null) || (!(myCoins instanceof Coins))) {
            mob.tell(L("You don't have your own account with @x1.", target.name(mob)));
            return false;
        }
        final List<String> accounts = bank.getAccountNames();
        String victim = "";
        int tries = 0;
        Coins hisCoins = null;
        double hisAmount = 0;
        while ((hisCoins == null) && ((++tries) < 10)) {
            final String possVic = accounts.get(CMLib.dice().roll(1, accounts.size(), -1));
            final Item C = bank.findDepositInventory(possVic, "1");
            if ((C != null)
                && (C instanceof Coins)
                && ((((Coins) C).getTotalValue() / 50.0) > 0.0)
                && (!mob.Name().equals(possVic))) {
                hisCoins = (Coins) C;
                victim = possVic;
                hisAmount = hisCoins.getTotalValue() / 50.0;
            }
        }
        final int classLevel = CMLib.ableMapper().qualifyingClassLevel(mob, this) + (2 * getXLEVELLevel(mob));
        if ((classLevel > 0)
            && (Math.round(hisAmount) > (1000 * (classLevel) + (2 * getXLEVELLevel(mob)))))
            hisAmount = 1000l * (classLevel + (2l * getXLEVELLevel(mob)));

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, (-(levelDiff + (timesPicked(mob) * 50))), auto);
        if ((success) && (hisAmount > 0) && (hisCoins != null)) {
            final String str = L("<S-NAME> embezzle(s) @x1 from the @x2 account maintained by <T-NAME>.", CMLib.beanCounter().nameCurrencyShort(target, hisAmount), victim);
            final CMMsg msg = CMClass.getMsg(mob, target, this, (auto ? CMMsg.MASK_ALWAYS : 0) | CMMsg.MSG_THIEF_ACT, str, null, str);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, (int) (((CMProps.getMillisPerMudHour() * mob.location().getArea().getTimeObj().getHoursInDay() * mob.location().getArea().getTimeObj().getDaysInMonth()) / CMProps.getTickMillis())));
                bank.delDepositInventory(victim, hisCoins);
                hisCoins = CMLib.beanCounter().makeBestCurrency(target, hisCoins.getTotalValue() - (hisAmount / 3.0));
                if (hisCoins.getNumberOfCoins() > 0)
                    bank.addDepositInventory(victim, hisCoins, null);
                bank.delDepositInventory(myAcct, myCoins);
                myCoins = CMLib.beanCounter().makeBestCurrency(mob, ((Coins) myCoins).getTotalValue() + hisAmount);
                if (((Coins) myCoins).getNumberOfCoins() > 0)
                    bank.addDepositInventory(myAcct, myCoins, null);
            }
        } else
            maliciousFizzle(mob, target, L("<T-NAME> catch(es) <S-NAME> trying to embezzle money!"));
        return success;
    }

}
