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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Prayer_MassBlindness extends Prayer {
    private final static String localizedName = CMLib.lang().L("Mass Blindness");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Blindness)");

    @Override
    public String ID() {
        return "Prayer_MassBlindness";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
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
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected == null)
            return;
        if (!(affected instanceof MOB))
            return;

        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SEE);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if ((canBeUninvoked()) && (CMLib.flags().canSee(mob)))
            mob.tell(L("Your vision returns."));
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat()) {
                if (CMLib.flags().isInDark(mob.location()))
                    return Ability.QUALITY_INDIFFERENT;
                if (target instanceof MOB) {
                    if (((MOB) target).charStats().getBodyPart(Race.BODY_EYE) == 0)
                        return Ability.QUALITY_INDIFFERENT;
                    if (!CMLib.flags().canSee((MOB) target))
                        return Ability.QUALITY_INDIFFERENT;
                }
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null)
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        boolean nothingDone = true;
        if (success) {
            for (final Object element : h) {
                final MOB target = (MOB) element;
                if (auto || (target.charStats().getBodyPart(Race.BODY_EYE) > 0)) {
                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> @x1 an unholy blindness upon <T-NAMESELF>.^?", prayForWord(mob)));
                    if ((target != mob) && (mob.location().okMessage(mob, msg))) {
                        mob.location().send(mob, msg);
                        if (msg.value() <= 0) {
                            success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                            mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> go(es) blind!"));
                        }
                        nothingDone = false;
                    }
                }
            }
        }

        if (nothingDone)
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to blind everyone, but flub(s) it."));

        // return whether it worked
        return success;
    }
}
