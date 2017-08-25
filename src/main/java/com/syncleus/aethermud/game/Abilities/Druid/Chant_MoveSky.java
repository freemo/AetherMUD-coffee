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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.TimeClock;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_MoveSky extends Chant {
    private final static String localizedName = CMLib.lang().L("Move The Sky");

    @Override
    public String ID() {
        return "Chant_MoveSky";
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_ALL - 99;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s), and the sky starts moving.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (mob.location().getArea().getTimeObj().getTODCode() == TimeClock.TimeOfDay.NIGHT) {
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The moon begin(s) to descend!"));
                    final int x = mob.location().getArea().getTimeObj().getHoursInDay() - mob.location().getArea().getTimeObj().getHourOfDay();
                    mob.location().getArea().getTimeObj().tickTock(x);
                } else {
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The sun hurries towards the horizon!"));
                    final int x = mob.location().getArea().getTimeObj().getDawnToDusk()[TimeClock.TimeOfDay.NIGHT.ordinal()] - mob.location().getArea().getTimeObj().getHourOfDay();
                    mob.location().getArea().getTimeObj().tickTock(x);
                }
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s), but the magic fades"));

        // return whether it worked
        return success;
    }
}
