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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_GasWard extends Chant {
    private final static String localizedName = CMLib.lang().L("Gas Ward");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Gas Ward)");

    @Override
    public String ID() {
        return "Chant_GasWard";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PRESERVING;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("Your filtering protection dissipates."));

        super.unInvoke();

    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_GAS, affectableStats.getStat(CharStats.STAT_SAVE_GAS) + 100);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already warding gas."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("A filtering field of protection appears around <T-NAME>.") : L("^S<S-NAME> chant(s) for a filtering field of protection around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) for a filtering shield, but fail(s)."));

        return success;
    }
}
