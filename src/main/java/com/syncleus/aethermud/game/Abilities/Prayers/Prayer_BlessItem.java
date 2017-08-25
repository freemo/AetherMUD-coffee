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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Abilities.interfaces.MendingSkill;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Coins;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_BlessItem extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Bless Item");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Blessed)");

    @Override
    public String ID() {
        return "Prayer_BlessItem";
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
        return Ability.CAN_MOBS | Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS | Ability.CAN_ITEMS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_BLESSING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected == null)
            return;
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_GOOD);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_BONUS);
        if (affected instanceof MOB)
            affectableStats.setArmor((affectableStats.armor() - 5) - ((affected.phyStats().level() / 10) + (2 * getXLEVELLevel(invoker()))));
        else if (affected instanceof Item)
            affectableStats.setAbility(affectableStats.ability() + 1);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB)) {
            if (canBeUninvoked())
                if ((affected instanceof Item) && (((Item) affected).owner() != null) && (((Item) affected).owner() instanceof MOB))
                    ((MOB) ((Item) affected).owner()).tell(L("The blessing on @x1 fades.", ((Item) affected).name()));
            super.unInvoke();
            return;
        }
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("Your aura of blessing fades."));
        super.unInvoke();
    }

    @Override
    public boolean supportsMending(Physical item) {
        return (item instanceof Item)
            && (CMLib.flags().domainAffects(item, Ability.DOMAIN_CURSING).size() > 0);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                Item I = Prayer_Bless.getSomething((MOB) target, true);
                if (I == null)
                    I = Prayer_Bless.getSomething((MOB) target, false);
                if (I == null)
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB mobTarget = getTarget(mob, commands, givenTarget, true, false);
        Item target = null;
        if (mobTarget != null)
            target = Prayer_Bless.getSomething(mobTarget, true);
        if ((target == null) && (mobTarget != null))
            target = Prayer_Bless.getSomething(mobTarget, false);
        if (target == null)
            target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        if (target instanceof Coins) {
            mob.tell(L("You can not bless that."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "<T-NAME> appear(s) blessed!" : "^S<S-NAME> bless(es) <T-NAMESELF>" + inTheNameOf(mob) + ".^?") + CMLib.protocol().msp("bless.wav", 10));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                Prayer_Bless.endLowerCurses(target, CMLib.ableMapper().lowestQualifyingLevel(ID()));
                beneficialAffect(mob, target, asLevel, 0);
                target.recoverPhyStats();
                mob.recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for blessings, but nothing happens.", prayWord(mob)));
        // return whether it worked
        return success;
    }
}
