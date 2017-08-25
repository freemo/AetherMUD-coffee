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
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_ArcaneMark extends Spell {

    private final static String localizedName = CMLib.lang().L("Arcane Mark");

    @Override
    public String ID() {
        return "Spell_ArcaneMark";
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
        return CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("You must specify what object you want the spell cast on, and the message you wish the object have marked upon it. "));
            return false;
        }
        final Physical target = mob.location().fetchFromMOBRoomFavorsItems(mob, null, (commands.get(0)), Wearable.FILTER_UNWORNONLY);
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", (commands.get(0))));
            return false;
        }
        if ((!(target instanceof Item)) || (!target.isGeneric())) {
            mob.tell(L("You can't can't cast this on @x1.", target.name(mob)));
            return false;
        }
        final String message = CMParms.combine(commands, 1);

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("^S<S-NAME> invoke(s) a spell upon <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (target.description().indexOf("Some markings on it say") >= 0)
                    target.setDescription(L("@x1 Some other markings say `@x2`.", target.description(), message));
                else
                    target.setDescription(L("@x1 Some markings on it say `@x2`.", target.description(), message));
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke a spell upon <T-NAMESELF>, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
