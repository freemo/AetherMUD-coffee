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
import com.syncleus.aethermud.game.Common.interfaces.TimeClock;
import com.syncleus.aethermud.game.Items.interfaces.Coins;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;

import java.util.List;
import java.util.Vector;

/*
 * Copyright 2000-2017 Bo Zimmerman, Lee Fox Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

public class Thief_Racketeer extends ThiefSkill {
    private static final String[] triggerStrings = {"RACKETEER"};
    public Vector<MOB> mobs = new Vector<MOB>();

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_CRIMINAL;
    }

    @Override
    public String ID() {
        return "Thief_Racketeer";
    }

    @Override
    public String name() {
        return "Racketeer";
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
    public boolean disregardsArmorCheck(MOB mob) {
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg)) {
            return false;
        }
        final MOB source = msg.source();
        if ((!msg.source().Name().equals(text())) && ((msg.source().getClanRole(text()) == null))
            && (msg.tool() instanceof Ability) && (msg.target() == affected)
            && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_THIEF_SKILL)) {
            if (invoker() == source) {
                source.tell(L("@x1 is currently under your protection.", ((Physical) msg.target()).name(source)));
            } else {
                source.tell(L("@x1 is under @x2's protection.", ((Physical) msg.target()).name(source), invoker().name(source)));
                invoker().tell(L("Word on the street is that @x1 is hassling @x2 who is under your protection.", source.name(invoker()), ((Physical) msg.target()).name(invoker())));
            }
            return false;
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (target instanceof PhysicalAgent) {
                final PhysicalAgent AE = (PhysicalAgent) target;
                if ((CMLib.aetherShops().getShopKeeper(target) == null) && (AE.fetchBehavior("MoneyChanger") == null)
                    && (AE.fetchBehavior("ItemMender") == null) && (AE.fetchBehavior("ItemIdentifier") == null)
                    && (AE.fetchBehavior("ItemRefitter") == null))
                    return Ability.QUALITY_INDIFFERENT;
                if (target.fetchEffect(ID()) != null)
                    return Ability.QUALITY_INDIFFERENT;
                if ((target instanceof MOB) && (!((MOB) target).mayIFight(mob)))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((commands.size() < 1) && (givenTarget == null)) {
            mob.tell(L("Get protection money from whom?"));
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
        if (mob.isInCombat()) {
            mob.tell(L("You are too busy to racketeer right now."));
            return false;
        }
        if ((CMLib.aetherShops().getShopKeeper(target) == null)
            && (target.fetchBehavior("MoneyChanger") == null)
            && (target.fetchBehavior("ItemMender") == null)
            && (target.fetchBehavior("ItemIdentifier") == null)
            && (target.fetchBehavior("ItemRefitter") == null)) {
            mob.tell(L("You can't get protection money from @x1.", target.name(mob)));
            return false;
        }
        final Ability A = target.fetchEffect(ID());
        if (A != null) {
            if (A.invoker() == mob)
                mob.tell(L("@x1 has already been extracted from today.", target.name(mob)));
            else {
                mob.tell(L("@x1 is already under @x2's protection.", target.name(mob), A.invoker().name(mob)));
                A.invoker().tell(L("Word on the street is that @x1 is trying to push into your business with @x2.", mob.name(A.invoker()), target.name()));
            }
            return false;
        }
        final int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (!target.mayIFight(mob)) {
            mob.tell(L("You cannot racketeer @x1.", target.charStats().himher()));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final double amount = CMLib.dice().roll(proficiency(), target.phyStats().level(), 0);
        final boolean success = proficiencyCheck(mob, -(levelDiff), auto);
        final Room R = mob.location();
        if ((success) && (R != null)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, (auto ? CMMsg.MASK_ALWAYS : 0) | CMMsg.MSG_THIEF_ACT, L("<S-NAME> extract(s) @x1 of protection money from <T-NAME>.", CMLib.moneyCounter().nameCurrencyShort(target, amount)));
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                final TimeClock timeObj = R.getArea().getTimeObj();
                final int hoursInMonth = timeObj.getHoursInDay() * timeObj.getDaysInMonth();
                final int tickDown = (int) (((CMProps.getMillisPerMudHour()) * hoursInMonth) / (CMProps.getTickMillis()));
                beneficialAffect(mob, target, asLevel, tickDown);
                final Coins C = CMLib.moneyCounter().makeBestCurrency(target, amount);
                if (C != null) {
                    R.addItem(C, ItemPossessor.Expire.Player_Drop);
                    CMLib.commands().postGet(mob, null, C, true);
                }
            }
        } else
            maliciousFizzle(mob, target, L("<T-NAME> seem(s) unintimidated by <S-NAME>."));
        return success;
    }
}
