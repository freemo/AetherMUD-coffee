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
import com.syncleus.aethermud.game.Commands.interfaces.Command;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.collections.Filterer;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.io.IOException;
import java.util.List;


public class Prayer_SenseFaithful extends Prayer {
    private final static String localizedName = CMLib.lang().L("Sense Faithful");

    @Override
    public String ID() {
        return "Prayer_SenseFaithful";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
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
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((mob.getWorshipCharID() == null) || (mob.getWorshipCharID().length() == 0)) {
            mob.tell(L("You don't worship a deity, so this magic means nothing."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), L("^S<S-NAME> @x1 for knowledge of the faithful^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final String deityName = mob.getWorshipCharID();
                Command C = CMClass.getCommand("Who");
                if (C != null) {
                    try {
                        if (mob.session() != null)
                            mob.session().print(
                                C.executeInternal(mob, 0, Boolean.FALSE, new Filterer<MOB>() {
                                    @Override
                                    public boolean passesFilter(MOB obj) {
                                        return (obj != null) && (obj.getWorshipCharID().equals(deityName));
                                    }

                                }).toString());
                    } catch (IOException e) {
                    }
                }
            }
        } else
            beneficialWordsFizzle(mob, null, auto ? "" : L("<S-NAME> @x1 for knowledge of the faithful, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}