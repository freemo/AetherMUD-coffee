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


public class Prayer_CauseFatigue extends Prayer {
    private final static String localizedName = CMLib.lang().L("Cause Fatigue");

    @Override
    public String ID() {
        return "Prayer_CauseFatigue";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_VEXING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | verbalCastCode(mob, target, auto), L(auto ? "A light fatigue overcomes <T-NAME>." : "^S<S-NAME> " + prayWord(mob) + " for light fatigue to overcome <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    final int harming = CMLib.dice().roll(3, adjustedLevel(mob, asLevel), 10);
                    if (target.maxState().getFatigue() > Long.MIN_VALUE / 2)
                        target.curState().adjFatigue((target.curState().getFatigue() / 2), target.maxState());
                    target.curState().adjMovement(-harming, target.maxState());
                    target.tell(L("You feel slightly more fatigued!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
