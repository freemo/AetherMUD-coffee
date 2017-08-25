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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Faction;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;


public class Prayer_MoralBalance extends Prayer {
    private final static String localizedName = CMLib.lang().L("Moral Balance");

    @Override
    public String ID() {
        return "Prayer_MoralBalance";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        CMMsg msg2 = null;
        if ((mob != target) && (!mob.getGroupMembers(new HashSet<MOB>()).contains(target)))
            msg2 = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, L("<T-NAME> do(es) not seem to like <S-NAME> messing with <T-HIS-HER> head."));

        if ((success) && (CMLib.factions().getFaction(CMLib.factions().AlignID()) != null)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "<T-NAME> feel(s) completely different about the world." : "^S<S-NAME> " + prayWord(mob) + " to bring balance to <T-NAMESELF>!^?"));
            if ((mob.location().okMessage(mob, msg))
                && ((msg2 == null) || (mob.location().okMessage(mob, msg2)))) {
                mob.location().send(mob, msg);
                if ((msg.value() <= 0) && ((msg2 == null) || (msg2.value() <= 0))) {
                    target.tell(L("Your views on the world suddenly change."));
                    final Faction F = CMLib.factions().getFaction(CMLib.factions().AlignID());
                    if (F != null) {
                        final int bredth = F.maximum() - F.minimum();
                        final int midpoint = F.minimum() + (bredth / 2);
                        final int distance = midpoint - target.fetchFaction(F.factionID());
                        final int amt = target.fetchFaction(F.factionID()) + (distance / 8);
                        final int change = amt - target.fetchFaction(F.factionID());
                        CMLib.factions().postFactionChange(target, this, CMLib.factions().AlignID(), change);
                    }
                }
                if (msg2 != null)
                    mob.location().send(mob, msg2);
            }
        } else {
            if ((msg2 != null) && (mob.location().okMessage(mob, msg2)))
                mob.location().send(mob, msg2);
            return beneficialWordsFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.", prayWord(mob)));
        }

        // return whether it worked
        return success;
    }
}
