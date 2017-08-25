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
import com.syncleus.aethermud.game.Abilities.interfaces.MendingSkill;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;


public class Prayer_RemoveCurse extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Remove Curse");

    @Override
    public String ID() {
        return "Prayer_RemoveCurse";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_BLESSING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public boolean supportsMending(Physical item) {
        if (!(item instanceof MOB))
            return false;
        return CMLib.flags().domainAffects(item, Ability.DOMAIN_CURSING).size() > 0;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (!supportsMending(target))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("^SA glow surrounds <T-NAME>.^?") : L("^S<S-NAME> call(s) on @x1 for <T-NAME> to be released from a curse.^?", hisHerDiety(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                Item I = Prayer_Bless.getSomething(target, true);
                Item lastI = null;
                final HashSet<Item> alreadyDone = new HashSet<Item>();
                while ((I != null) && (!alreadyDone.contains(I))) {
                    if (lastI == I) {
                        alreadyDone.add(I);
                        final CMMsg msg2 = CMClass.getMsg(target, I, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_DROP, L("<S-NAME> release(s) <T-NAME>."));
                        target.location().send(target, msg2);
                    } else {
                        CMLib.flags().setRemovable(I, true);
                        CMLib.flags().setDroppable(I, true);
                    }
                    Prayer_Bless.endLowerCurses(I, adjustedLevel(mob, asLevel));
                    I.recoverPhyStats();
                    lastI = I;
                    I = Prayer_Bless.getSomething(target, true);
                }
                Prayer_Bless.endLowerCurses(target, adjustedLevel(mob, asLevel));
                target.recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> call(s) on @x1 to release <T-NAME> from a curse, but nothing happens.", hisHerDiety(mob)));

        // return whether it worked
        return success;
    }
}
