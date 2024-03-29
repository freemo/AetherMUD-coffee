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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


public class Prayer_RemoveDeathMark extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Remove Death Mark");

    @Override
    public String ID() {
        return "Prayer_RemoveDeathMark";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public boolean supportsMending(Physical item) {
        if (!(item instanceof MOB))
            return false;
        return (item.fetchEffect("Thief_Mark") != null) || (item.fetchEffect("Thief_ContractHit") != null);
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
        final Hashtable<Ability, MOB> remove = new Hashtable<Ability, MOB>();
        Ability E = target.fetchEffect("Thief_Mark");
        if (E != null)
            remove.put(E, target);
        E = target.fetchEffect("Thief_ContractHit");
        if (E != null)
            remove.put(E, target);
        for (final Enumeration<MOB> e = CMLib.players().players(); e.hasMoreElements(); ) {
            final MOB M = e.nextElement();
            if ((M != null) && (M != target)) {
                E = M.fetchEffect("Thief_Mark");
                if ((E != null) && (E.text().startsWith(target.Name() + "/")))
                    remove.put(E, M);
            }
        }

        if ((success) && (remove.size() > 0)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("^SA glow surrounds <T-NAME>.^?") : L("^S<S-NAME> call(s) on @x1 for <T-NAME> to be released from a death mark.^?", hisHerDiety(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (final Enumeration<Ability> e = remove.keys(); e.hasMoreElements(); ) {
                    final Ability A = e.nextElement();
                    final MOB M = remove.get(A);
                    A.unInvoke();
                    M.delEffect(A);
                }

            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> call(s) on @x1 to release <T-NAME> from a death mark, but nothing happens.", hisHerDiety(mob)));

        // return whether it worked
        return success;
    }
}
