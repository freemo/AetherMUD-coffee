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
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_StoneFlesh extends Spell {

    private final static String localizedName = CMLib.lang().L("Stone Flesh");

    @Override
    public String ID() {
        return "Spell_StoneFlesh";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        final Physical target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        Ability revokeThis = null;
        for (int a = 0; a < target.numEffects(); a++) // personal effects
        {
            final Ability A = target.fetchEffect(a);
            if ((A != null) && (A.canBeUninvoked())
                && ((A.ID().equalsIgnoreCase("Spell_FleshStone"))
                || (A.ID().equalsIgnoreCase("Prayer_FleshRock")))) {
                revokeThis = A;
                break;
            }
        }

        if (revokeThis == null) {
            if (auto)
                mob.tell(L("Nothing happens."));
            else
                mob.tell(mob, target, null, L("<T-NAME> can not be affected by this spell."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> dispel(s) @x1 from <T-NAMESELF>.^?", revokeThis.name()));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                revokeThis.unInvoke();
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to dispel @x1 from <T-NAMESELF>, but flub(s) it.", revokeThis.name()));

        // return whether it worked
        return success;
    }
}
