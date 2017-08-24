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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Skill_Struggle extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Struggle");
    private static final String[] triggerStrings = I(new String[]{"STRUGGLE"});

    @Override
    public String ID() {
        return "Skill_Struggle";
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
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_EVASIVE;
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public void affectCharStats(MOB mob, CharStats stats) {
        super.affectCharStats(mob, stats);
        if (!CMLib.flags().isBound(mob))
            unInvoke();
        else {
            final int xlvl = super.getXLEVELLevel(mob);
            stats.setStat(CharStats.STAT_STRENGTH, stats.getStat(CharStats.STAT_STRENGTH) + stats.getStat(CharStats.STAT_DEXTERITY) + mob.phyStats().level() + xlvl);
        }
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if ((CMLib.flags().isBound(target)) && (target == mob))
                    return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!CMLib.flags().isBound(mob)) {
            mob.tell(L("You don't seem to be bound by anything you can struggle against!"));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> attempt(s) to struggle with <S-HIS-HER> bonds."));
            if (mob.location().okMessage(mob, msg)) {
                mob.addEffect(this);
                mob.recoverCharStats();
                mob.location().send(mob, msg);
                mob.delEffect(this);
                mob.recoverCharStats();
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> fumble(s) <S-HIS-HER> attempt to struggle."));

        return success;
    }

}
