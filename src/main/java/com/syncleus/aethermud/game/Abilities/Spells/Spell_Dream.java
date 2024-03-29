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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;


public class Spell_Dream extends Spell {

    private final static String localizedName = CMLib.lang().L("Dream");

    @Override
    public String ID() {
        return "Spell_Dream";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        if (commands.size() < 1) {
            mob.tell(L("Invoke a dream about what?"));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), L("^S<S-NAME> invoke(s) a dreamy spell.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                try {
                    for (final Enumeration<Room> r = CMLib.map().rooms(); r.hasMoreElements(); ) {
                        final Room R = r.nextElement();
                        if (CMLib.flags().canAccess(mob, R)) {
                            for (int i = 0; i < R.numInhabitants(); i++) {
                                final MOB inhab = R.fetchInhabitant(i);
                                if ((inhab != null) && (CMLib.flags().isSleeping(inhab))) {
                                    msg = CMClass.getMsg(mob, inhab, this, verbalCastCode(mob, inhab, auto), null);
                                    if (R.okMessage(mob, msg))
                                        inhab.tell(L("You dream @x1.", CMParms.combine(commands, 0)));
                                }
                            }
                        }
                    }
                } catch (final NoSuchElementException nse) {
                }
            }

        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to invoke a dream, but fizzle(s) the spell."));

        // return whether it worked
        return success;
    }
}
