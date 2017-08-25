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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_AlterTime extends Chant {
    private final static String localizedName = CMLib.lang().L("Alter Time");

    @Override
    public String ID() {
        return "Chant_AlterTime";
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
    public int overrideMana() {
        return 100;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s), and reality seems to start blurring.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                int x = CMath.s_int(text());
                while (x == 0)
                    x = CMLib.dice().roll(1, 3, -2);
                if (x > 0)
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Time moves forwards!"));
                else
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Time moves backwards!"));
                mob.location().getArea().getTimeObj().tickTock(x);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s), but the magic fades"));

        // return whether it worked
        return success;
    }
}
