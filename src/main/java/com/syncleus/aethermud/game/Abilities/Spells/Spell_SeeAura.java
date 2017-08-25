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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_SeeAura extends Spell {

    private final static String localizedName = CMLib.lang().L("See Aura");

    @Override
    public String ID() {
        return "Spell_SeeAura";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int enchantQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if (target == mob) {
            mob.tell(L("Um, you could just enter SCORE."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^SYou draw out <T-NAME>s aura, seeing <T-HIM-HER> from the inside out...^?"), verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> draw(s) out your aura.^?"), verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> draws out <T-NAME>s aura.^?"));
        if (success) {
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final StringBuilder str = CMLib.commands().getScore(target);
                if (!mob.isMonster())
                    mob.session().wraplessPrintln(str.toString());
            }
        } else
            beneficialVisualFizzle(mob, target, L("<S-NAME> examine(s) <T-NAME>, incanting, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
