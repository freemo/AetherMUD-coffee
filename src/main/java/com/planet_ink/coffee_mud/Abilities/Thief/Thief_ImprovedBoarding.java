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
package com.planet_ink.coffee_mud.Abilities.Thief;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Thief_ImprovedBoarding extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Improved Boarding");

    @Override
    public String ID() {
        return "Thief_ImprovedBoarding";
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
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_FITNESS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
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
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return super.okMessage(myHost, msg);

        final MOB mob = (MOB) affected;
        if ((msg.amISource(mob))
            && (msg.tool() instanceof Ability)
            && (((Ability) msg.tool()).ID().equalsIgnoreCase("Skill_Climb"))
            && (mob.isMine(msg.tool()))
            && (proficiencyCheck(mob, 0, mob.isMonster()))) {
            helpProficiency(mob, 0);
            msg.addTrailerRunnable(new Runnable() {
                @Override
                public void run() {
                    mob.recoverPhyStats();
                }
            });
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);

        if ((affected instanceof MOB)
            && (CMLib.flags().isClimbing(affected))
            && (CMLib.flags().isWateryRoom(((MOB) affected).location()))) {
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_FLYING);
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SNEAKING);
        }
    }

}
