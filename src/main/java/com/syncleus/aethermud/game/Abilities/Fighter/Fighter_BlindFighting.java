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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Fighter_BlindFighting extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Blind Fighting");
    protected boolean seeEnabled = false;

    @Override
    public String ID() {
        return "Fighter_BlindFighting";
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
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
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
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected == null)
            return;
        if (!(affected instanceof MOB))
            return;
        if (seeEnabled)
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_VICTIM);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        seeEnabled = false;
        if (!(ticking instanceof MOB))
            return true;
        final MOB mob = (MOB) ticking;
        if (!mob.isInCombat())
            return true;
        if ((!CMLib.flags().canBeSeenBy(mob.getVictim(), mob))
            && (CMLib.flags().canBeHeardMovingBy(mob.getVictim(), mob))
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(mob, 0, false))) {
            seeEnabled = true;
            if (CMLib.dice().rollPercentage() < 10)
                helpProficiency(mob, 0);
        }
        return true;
    }
}
