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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_KnowValue extends Spell {

    private final static String localizedName = CMLib.lang().L("Know Value");

    @Override
    public String ID() {
        return "Spell_KnowValue";
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> weigh(s) the value of <T-NAMESELF> carefully.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                String str = null;
                if (target.value() <= 0)
                    str = L("@x1 isn't worth anything.", target.name(mob));
                else if (target.value() == 0)
                    str = L("@x1 is worth hardly anything at all", target.name(mob));
                else
                    str = L("@x1 is worth @x2 ", target.name(mob), CMLib.beanCounter().nameCurrencyShort(mob, (double) target.value()));
                if (mob.isMonster())
                    CMLib.commands().postSay(mob, null, str, false, false);
                else
                    mob.tell(str);
            }

        } else
            beneficialVisualFizzle(mob, target, L("<S-NAME> weigh(s) the value of <T-NAMESELF>, looking more frustrated every second."));

        // return whether it worked
        return success;
    }
}
