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
import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Enervate extends Prayer {
    private final static String localizedName = CMLib.lang().L("Enervate");

    @Override
    public String ID() {
        return "Prayer_Enervate";
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
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | verbalCastCode(mob, target, auto), L(auto ? "A light fatigue overcomes <T-NAME>." : "^S<S-NAME> " + prayWord(mob) + " for extreme fatigue to overcome <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    final int harming = CMLib.dice().roll(10, adjustedLevel(mob, asLevel), 50);
                    if ((target.curState().getFatigue() <= CharState.FATIGUED_MILLIS)
                        && (target.maxState().getFatigue() > Long.MIN_VALUE / 2))
                        target.curState().setFatigue(CharState.FATIGUED_MILLIS + 1);
                    target.curState().adjMovement(-harming, target.maxState());
                    target.tell(L("You feel fatigued!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
