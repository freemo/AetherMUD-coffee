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
package com.planet_ink.game.Abilities.Skills;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;


public class Skill_DeepBreath extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Deep Breath");
    protected int breatheTicks = -1;

    @Override
    public String ID() {
        return "Skill_DeepBreath";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    protected int getMaxBreathTicks(final MOB mob) {
        return 3 + (super.adjustedLevel((MOB) affected, 0) / 5);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((msg.target() == affected)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && ((msg.sourceMajor() & CMMsg.MASK_ALWAYS) != 0)
            && (msg.tool() == affected)
            && ((msg.targetMinor() == CMMsg.TYP_GAS) || (msg.targetMinor() == CMMsg.TYP_WATER))
            && (!CMLib.flags().canBreatheHere((MOB) affected, ((MOB) affected).location()))
            && (msg.value() > 0)
            && (breatheTicks != 0)) {
            if (breatheTicks < 0)
                breatheTicks = getMaxBreathTicks((MOB) affected);
            if (super.proficiencyCheck((MOB) affected, 2 * ((MOB) affected).charStats().getStat(CharStats.STAT_CONSTITUTION), false)) {
                breatheTicks--;
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        final Physical affected = this.affected;
        if ((breatheTicks >= 0) && (affected instanceof MOB)) {
            if (CMLib.flags().canBreatheHere((MOB) affected, ((MOB) affected).location()))
                breatheTicks = -1;
        }
        return true;
    }
}
