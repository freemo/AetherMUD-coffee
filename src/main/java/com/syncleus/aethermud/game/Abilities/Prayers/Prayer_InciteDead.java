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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class Prayer_InciteDead extends Prayer {
    private final static String localizedName = CMLib.lang().L("Incite Dead");

    @Override
    public String ID() {
        return "Prayer_InciteDead";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_DEATHLORE;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (!CMLib.flags().isUndead((MOB) target))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> possibleTargets = properTargets(mob, givenTarget, auto);
        if (possibleTargets == null)
            return false;

        final Set<MOB> h = new HashSet<MOB>();
        for (Iterator<MOB> i = possibleTargets.iterator(); i.hasNext(); ) {
            final MOB M = i.next();
            if (CMLib.flags().isUndead(M))
                h.add(M);
        }

        if (h.size() == 0) {
            mob.tell(L("None of your targets are undead."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Set<MOB> friends = mob.getGroupMembers(new HashSet<MOB>());
        boolean success = proficiencyCheck(mob, 0, auto);
        boolean nothingDone = true;
        final Room R = mob.location();
        if (success && (R != null)) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> @x1 to incite the dead.^?", prayForWord(mob)));
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                for (final Object element : h) {
                    final MOB target = (MOB) element;
                    final CMMsg msg2 = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, null);
                    if ((target != mob) && (R.okMessage(mob, msg2))) {
                        for (int i = 0; i < 20; i++) {
                            final MOB M = R.fetchRandomInhabitant();
                            if ((M != target) && (M != mob) && (!friends.contains(M)) && (mob.mayIFight(M))) {
                                if (R.show(target, M, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> seem(s) incited to attack <T-NAMESELF>."))) {
                                    target.setVictim(M);
                                    nothingDone = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (nothingDone)
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to incite the dead, but flub(s) it."));

        // return whether it worked
        return success;
    }
}
