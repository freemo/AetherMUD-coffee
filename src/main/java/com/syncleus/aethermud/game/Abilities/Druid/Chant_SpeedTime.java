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
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_SpeedTime extends Chant {
    private final static String localizedName = CMLib.lang().L("Speed Time");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Speed Time)");

    @Override
    public String ID() {
        return "Chant_SpeedTime";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
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
    protected int overrideMana() {
        return 100;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("Something is happening!") : L("^S<S-NAME> begin(s) to chant...^?"));
            if (mob.location().okMessage(mob, msg)) {
                final int mana = mob.curState().getMana();
                mob.location().send(mob, msg);
                for (int i = 0; i < (adjustedLevel(mob, asLevel) / 2); i++)
                    CMLib.threads().tickAllTickers(mob.location());
                if (mob.curState().getMana() > mana)
                    mob.curState().setMana(mana);
                mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("It stops.") : L("^S<S-NAME> stop(s) chanting.^?"));
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> chant(s), but nothing happens."));

        return success;
    }
}
