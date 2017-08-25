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
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;


public class Spell_SummonMarker extends Spell {

    private final static String localizedName = CMLib.lang().L("Summon Marker");

    @Override
    public String ID() {
        return "Spell_SummonMarker";
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
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public void unInvoke() {

        if ((canBeUninvoked()) && (invoker() != null) && (affected instanceof Room))
            invoker().tell(L("Your marker in '@x1' dissipates.", ((Room) affected).displayText()));
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        try {
            for (final Enumeration<Room> r = CMLib.map().rooms(); r.hasMoreElements(); ) {
                final Room R = r.nextElement();
                if (CMLib.flags().canAccess(mob, R)) {
                    for (final Enumeration<Ability> a = R.effects(); a.hasMoreElements(); ) {
                        final Ability A = a.nextElement();
                        if ((A != null)
                            && (A.ID().equals(ID()))
                            && (A.invoker() == mob)) {
                            A.unInvoke();
                            break;
                        }
                    }
                }
            }
        } catch (final NoSuchElementException nse) {
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, mob.location(), this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> summon(s) <S-HIS-HER> marker energy to this place!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(mob, mob.location(), CMMsg.MSG_OK_VISUAL, L("The spot <S-NAME> pointed to glows for brief moment."));
                beneficialAffect(mob, mob.location(), 0, (adjustedLevel(mob, asLevel) * 240) + 450);
            }

        } else
            beneficialWordsFizzle(mob, null, L("<S-NAME> attempt(s) to summon <S-HIS-HER> marker energy, but fail(s)."));

        // return whether it worked
        return success;
    }
}
