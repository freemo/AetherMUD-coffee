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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Skill_MountedCombat extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Mounted Combat");

    @Override
    public String ID() {
        return "Skill_MountedCombat";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if ((mob.isInCombat()) && (mob.rangeToTarget() == 0) && (mob.riding() != null) && (mob.riding().amRiding(mob))) {
                final int xlvl = super.getXLEVELLevel(invoker());
                affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + ((1 + (xlvl / 3)) * mob.basePhyStats().attackAdjustment()));
                affectableStats.setDamage(affectableStats.damage() + ((1 + (xlvl / 3)) * mob.basePhyStats().damage()));
            }
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.source() == affected)
            && (msg.target() instanceof MOB)
            && (CMLib.dice().roll(1, 10, 0) == 1)
            && (msg.source().riding() != null)
            && (msg.source().riding().amRiding(msg.source())))
            helpProficiency(msg.source(), 0);
        return super.okMessage(myHost, msg);
    }
}
