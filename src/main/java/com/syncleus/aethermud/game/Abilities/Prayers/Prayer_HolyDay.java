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
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.LegalBehavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;


public class Prayer_HolyDay extends Prayer {
    private final static String localizedName = CMLib.lang().L("Holy Day");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Holy Day)");
    protected String godName = "the gods";

    @Override
    public String ID() {
        return "Prayer_HolyDay";
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
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
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
    protected int overrideMana() {
        return Ability.COST_ALL;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if ((affected == null) || (!(affected instanceof Area)))
            return;
        final Area A = (Area) affected;

        super.unInvoke();

        if (canBeUninvoked()) {
            for (final Enumeration<Room> e = A.getMetroMap(); e.hasMoreElements(); ) {
                final Room R = e.nextElement();
                R.showHappens(CMMsg.MSG_OK_VISUAL, L("The holy day has ended."));
            }
        }
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((msg.tool() instanceof Ability)
            && (!((Ability) msg.tool()).isAutoInvoked())
            && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_COMMON_SKILL)
            && (msg.source() != invoker())) {
            msg.source().tell(L("You are not allowed to work on the holy day of @x1.", godName));
            return false;
        } else if (((msg.sourceMinor() == CMMsg.TYP_BUY)
            || (msg.sourceMinor() == CMMsg.TYP_BID)
            || (msg.sourceMinor() == CMMsg.TYP_SELL)
            || (msg.sourceMinor() == CMMsg.TYP_WITHDRAW)
            || (msg.sourceMinor() == CMMsg.TYP_BORROW)
            || (msg.sourceMinor() == CMMsg.TYP_DEPOSIT))
            && (msg.source() != invoker())) {
            msg.source().tell(L("You are not allowed to work or do commerce on the holy day of @x1.", godName));
            return false;
        } else if (((CMath.bset(msg.sourceMajor(), CMMsg.MASK_MALICIOUS))
            || (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            || (CMath.bset(msg.othersMajor(), CMMsg.MASK_MALICIOUS)))
            && (CMLib.clans().findConquerableClan(msg.source()) != null)) {
            LegalBehavior B = null;
            if (msg.source().location() != null)
                B = CMLib.law().getLegalBehavior(msg.source().location());
            if ((B != null) && (B.controlPoints() > 0)) {
                msg.source().tell(L("There can be no conquest on the holy day of @x1.", godName));
                return false;
            }
        }
        return super.okMessage(host, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected == null) || (!(affected instanceof Area)))
            return super.tick(ticking, tickID);

        if (((Area) affected).getTimeObj().getHourOfDay() == 15)
            unInvoke();

        return super.tick(ticking, tickID);
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
        final Area target = mob.location().getArea();
        if (target == null)
            return false;
        if (target.fetchEffect(ID()) != null) {
            target.fetchEffect(ID()).unInvoke();
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 for a holy day.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (final Enumeration<Room> e = target.getMetroMap(); e.hasMoreElements(); ) {
                    final Room R = e.nextElement();
                    godName = mob.getWorshipCharID();
                    if ((godName.length() == 0) || (CMLib.map().getDeity(godName) == null))
                        godName = "the gods";
                    R.showHappens(CMMsg.MSG_OK_VISUAL, L("A holy day of @x1 has begun!", godName));
                }
                beneficialAffect(mob, target, asLevel, CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY));
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for a holy day, but <S-HIS-HER> plea is not answered.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
