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


public class Spell_Mend extends Spell {

    private final static String localizedName = CMLib.lang().L("Mend");

    @Override
    public String ID() {
        return "Spell_Mend";
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
        final Item target = getTarget(mob, null, givenTarget, commands, Wearable.FILTER_ANY);
        if (target == null)
            return false;
        if (!target.subjectToWearAndTear()) {
            mob.tell(L("@x1 cannot be mended.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, (((mob.phyStats().level() + (2 * getXLEVELLevel(mob))) - target.phyStats().level()) * 5), auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto),
                (auto ? "<T-NAME> begins to shimmer!"
                    : "^S<S-NAME> incant(s) at <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (target.usesRemaining() >= 100)
                    mob.tell(L("Nothing happens to @x1.", target.name(mob)));
                else {
                    mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-NAME> begin(s) to glow and mend!"));
                    target.setUsesRemaining(100);
                }
                target.recoverPhyStats();
                mob.location().recoverRoomStats();
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> incant(s) at <T-NAMESELF>, but nothing happens."));

        // return whether it worked
        return success;
    }
}
