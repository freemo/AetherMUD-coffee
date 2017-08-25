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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_DiviningEye extends Spell {

    private final static String localizedName = CMLib.lang().L("Divining Eye");

    @Override
    public String ID() {
        return "Spell_DiviningEye";
    }

    @Override
    public String name() {
        return localizedName;
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() == 0) {
            mob.tell(L("You must specify a divining spell and any parameters for it."));
            return false;
        }

        final Ability pryingEyeA = mob.fetchEffect("Spell_PryingEye");
        if (pryingEyeA == null) {
            mob.tell(L("This spell requires an active prying eye."));
            return false;
        }

        final String commandStr = CMParms.combine(commands);
        commands.add(0, "CAST");
        final Ability A = CMLib.english().getToEvoke(mob, commands);
        if (A == null) {
            mob.tell(L("'@x1' does not refer to any diviner spell you know.", commandStr));
            return false;
        }
        if (((A.classificationCode() & Ability.ALL_ACODES) != Ability.ACODE_SPELL)
            || ((A.classificationCode() & Ability.ALL_DOMAINS) != Ability.DOMAIN_DIVINATION)) {
            mob.tell(L("'@x1' is not a diviner spell you know.", A.name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, somanticCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> invoke(s) a remote divination!^?"));
            final Room room = mob.location();
            if (room.okMessage(mob, msg)) {
                final MOB eyeM = (MOB) pryingEyeA.affecting();
                room.send(mob, msg);
                try {
                    final Room eyeRoom = eyeM.location();
                    if (eyeRoom != null) {
                        eyeM.addAbility(A);
                        A.invoke(eyeM, commands, null, false, 0);
                    }
                } finally {
                }
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to invoke something, but fail(s)."));

        // return whether it worked
        return success;
    }
}
