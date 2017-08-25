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
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Prayer_MassParalyze extends Prayer {
    private final static String localizedName = CMLib.lang().L("Mass Paralyze");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Paralyzed)");

    @Override
    public String ID() {
        return "Prayer_MassParalyze";
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
        return Ability.FLAG_UNHOLY | Ability.FLAG_PARALYZING;
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

        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("The paralysis eases out of your muscles."));
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
                final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> @x1 an unholy paralysis upon <T-NAMESELF>.^?", prayForWord(mob)));
                final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | CMMsg.TYP_PARALYZE | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                if ((target != mob) && (mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                    int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
                    if (levelDiff < 0)
                        levelDiff = 0;
                    if (levelDiff > 6)
                        levelDiff = 6;
                    mob.location().send(mob, msg);
                    mob.location().send(mob, msg2);
                    if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                        success = maliciousAffect(mob, target, asLevel, 8 - levelDiff, -1) != null;
                        mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> can't move!"));
                    }
                    nothingDone = false;
                }
            }
        }

        if (nothingDone)
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to paralyze everyone, but flub(s) it."));

        // return whether it worked
        return success;
    }
}
