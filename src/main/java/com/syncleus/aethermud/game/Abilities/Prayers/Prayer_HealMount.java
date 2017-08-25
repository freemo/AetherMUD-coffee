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
import com.syncleus.aethermud.game.Abilities.interfaces.MendingSkill;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Rideable;

import java.util.List;


public class Prayer_HealMount extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Heal Mount");

    @Override
    public String ID() {
        return "Prayer_HealMount";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HEALING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY | Ability.FLAG_HEALINGMAGIC;
    }

    @Override
    protected long minCastWaitTime() {
        return CMProps.getTickMillis() / 2;
    }

    @Override
    public boolean supportsMending(Physical item) {
        return (item instanceof MOB)
            && (item instanceof Rideable)
            && (((Rideable) item).numRiders() > 0);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (!supportsMending(target))
                    return Ability.QUALITY_INDIFFERENT;
                if (CMLib.flags().isUndead((MOB) target))
                    return Ability.QUALITY_MALICIOUS;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = (mob.riding() instanceof MOB) ? (MOB) mob.riding() : null;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB) && (givenTarget instanceof Rideable))
            target = (MOB) givenTarget;

        if ((target == null) || ((!auto) && (mob.riding() != target))) {
            mob.tell(L("You can only heal your current mount with this magic."));
            return false;
        }

        final boolean undead = CMLib.flags().isUndead(target);

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, (!undead ? 0 : CMMsg.MASK_MALICIOUS) | verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) surrounded by a white light.") : L("^S<S-NAME> @x1 for tremendous healing power over <T-NAMESELF>.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final int healing = CMLib.dice().roll(5, adjustedLevel(mob, asLevel), 10);
                final int oldHP = target.curState().getHitPoints();
                CMLib.combat().postHealing(mob, target, this, healing, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
                if (target.curState().getHitPoints() > oldHP)
                    target.tell(L("You feel tons better!"));
                lastCastHelp = System.currentTimeMillis();
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
