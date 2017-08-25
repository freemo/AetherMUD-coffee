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
import java.util.Set;


public class Spell_MassHaste extends Spell {

    private final static String localizedName = CMLib.lang().L("Mass Haste");

    @Override
    public String ID() {
        return "Spell_MassHaste";
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
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, false);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth speeding up."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (mob.location().show(mob, null, this, somanticCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> wave(s) <S-HIS-HER> arms and speak(s) quickly.^?"))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), null);
                    if ((mob.location().okMessage(mob, msg))
                        && (target.fetchEffect("Spell_Haste") == null)
                        && (target.fetchEffect("Spell_MassHaste") == null)) {
                        mob.location().send(mob, msg);
                        mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> speed(s) up!"));
                        final Spell_Haste haste = new Spell_Haste();
                        haste.setProficiency(proficiency());
                        haste.beneficialAffect(mob, target, asLevel, 0);
                    }
                }
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> wave(s) <S-HIS-HER> arms and speak(s) quickly, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
