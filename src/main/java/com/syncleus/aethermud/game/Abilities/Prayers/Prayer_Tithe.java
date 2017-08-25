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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;

import java.util.List;


public class Prayer_Tithe extends Prayer {
    private final static String localizedName = CMLib.lang().L("Tithe");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Tithe)");

    @Override
    public String ID() {
        return "Prayer_Tithe";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your need to tithe fades."));
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected instanceof ShopKeeper)
            affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 2);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_GET)
            && (msg.source() == affected)
            && (msg.target() instanceof Coins)) {
            final long num = ((Coins) msg.target()).getNumberOfCoins();
            ((Coins) msg.target()).setNumberOfCoins(num - (num / 10));
            if ((invoker() != msg.source()) && ((num / 10) > 0)) {
                invoker().tell(msg.source(), null, null, L("<S-NAME> tithes."));
                final String currency = ((Coins) msg.target()).getCurrency();
                CMLib.beanCounter().addMoney(invoker(), currency, CMath.mul(((Coins) msg.target()).getDenomination(), (num / 10)));
            }
        }
        if ((msg.sourceMinor() == CMMsg.TYP_BUY)
            && (msg.amITarget(affected))
            && (msg.tool() != null)) {
            final ShopKeeper SK = CMLib.coffeeShops().getShopKeeper(affected);
            if (SK.getShop().doIHaveThisInStock("$" + msg.tool().Name() + "$", msg.source())) {
                final ShopKeeper.ShopPrice price = CMLib.coffeeShops().sellingPrice((MOB) affected, msg.source(), msg.tool(), SK, true);
                if ((price.absoluteGoldPrice > 0.0) && (price.absoluteGoldPrice <= CMLib.beanCounter().getTotalAbsoluteShopKeepersValue(msg.source(), invoker()))) {
                    if (invoker() != msg.target()) {
                        invoker().tell(msg.source(), null, null, L("<S-NAME> tithes."));
                        CMLib.beanCounter().addMoney(invoker(), CMath.div(price.absoluteGoldPrice, 10.0));
                    }
                }
            }
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (target instanceof MOB) {
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) filled with a need to tithe!") : L("^S<S-NAME> @x1 for <T-YOUPOSS> need to tithe!^?", prayWord(mob)));
            final CMMsg msg3 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg3))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg3);
                if ((msg.value() <= 0) && (msg3.value() <= 0))
                    maliciousAffect(mob, target, asLevel, 0, -1);
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> @x1 for <T-YOUPOSS> tithing need but there is no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
