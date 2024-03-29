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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Skill_SlowFall extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Slow Fall");
    public boolean activated = false;

    @Override
    public String ID() {
        return "Skill_SlowFall";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return activated ? "(Slow Fall)" : "";
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (activated)
            affectableStats.setWeight(0);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (affected != null) {
            if ((CMath.bset(affected.phyStats().disposition(), PhyStats.IS_FALLING))
                && ((!(affected instanceof MOB))
                || (((MOB) affected).fetchAbility(ID()) == null)
                || proficiencyCheck((MOB) affected, 0, false))) {
                activated = true;
                affected.recoverPhyStats();
                if (affected instanceof MOB)
                    helpProficiency((MOB) affected, 0);
            } else if (activated) {
                activated = false;
                affected.recoverPhyStats();
            }
        }
        return super.tick(ticking, tickID);
    }
}
