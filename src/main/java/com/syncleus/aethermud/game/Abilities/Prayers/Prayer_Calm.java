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
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Calm extends Prayer {
    private final static String localizedName = CMLib.lang().L("Calm");

    @Override
    public String ID() {
        return "Prayer_Calm";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (CMLib.flags().isEvil(mob))
                return Ability.QUALITY_INDIFFERENT;

            if (CMLib.flags().isNeutral(mob) && (CMLib.dice().roll(1, 2, 0) == 1))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        boolean someoneIsFighting = false;
        for (int i = 0; i < mob.location().numInhabitants(); i++) {
            final MOB inhab = mob.location().fetchInhabitant(i);
            if ((inhab != null) && (inhab.isInCombat()))
                someoneIsFighting = true;
        }

        if ((success) && (someoneIsFighting)) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("A feeling of calmness descends.") : L("^S<S-NAME> @x1 for calmness.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (int i = 0; i < mob.location().numInhabitants(); i++) {
                    final MOB inhab = mob.location().fetchInhabitant(i);
                    if ((inhab != null) && (inhab.isInCombat())) {
                        inhab.tell(L("You feel at peace."));
                        inhab.makePeace(true);
                    }
                }
            }
        } else
            beneficialWordsFizzle(mob, null, L("<S-NAME> @x1 for calmness, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
