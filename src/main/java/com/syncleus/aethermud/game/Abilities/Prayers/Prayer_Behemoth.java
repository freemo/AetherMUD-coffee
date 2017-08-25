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
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Behemoth extends Prayer {
    private final static String localizedName = CMLib.lang().L("Behemoth");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Behemoth)");

    @Override
    public String ID() {
        return "Prayer_Behemoth";
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
        return Ability.QUALITY_BENEFICIAL_SELF;
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
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your behemoth size has subsided."));
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
        super.affectCharStats(affectedMOB, affectedStats);
        affectedStats.setStat(CharStats.STAT_STRENGTH, affectedStats.getStat(CharStats.STAT_STRENGTH) + 5);
        affectedStats.setStat(CharStats.STAT_DEXTERITY, affectedStats.getStat(CharStats.STAT_DEXTERITY) - 5);
        long newWeight = affectedStats.getStat(CharStats.STAT_WEIGHTADJ) + (affectedMOB.basePhyStats().weight() * 4);
        if (newWeight > Short.MAX_VALUE)
            newWeight = Short.MAX_VALUE;
        affectedStats.setStat(CharStats.STAT_WEIGHTADJ, (int) newWeight);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectedStats) {
        super.affectPhyStats(affected, affectedStats);
        affectedStats.setHeight(affectedStats.height() * 3);
        affectedStats.setName(L("A BEHEMOTH @x1", affected.name().toUpperCase()));
        if (!(affected instanceof MOB))
            affectedStats.setWeight(affectedStats.weight() * 4);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already BEHEMOTH in size."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> grow(s) to BEHEMOTH size!"));
                beneficialAffect(mob, target, asLevel, 0);
                CMLib.utensils().confirmWearability(target);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
