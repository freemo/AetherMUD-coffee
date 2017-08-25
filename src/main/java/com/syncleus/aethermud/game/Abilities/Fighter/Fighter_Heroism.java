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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Fighter_Heroism extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Heroism");
    private boolean activated = false;

    @Override
    public String ID() {
        return "Fighter_Heroism";
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

    public void setActivated(boolean activate) {
        if (activate == activated)
            return;
        activated = activate;
        if (affected instanceof MOB)
            ((MOB) affected).recoverCharStats();
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);

        final MOB mob = (MOB) affected;

        if ((CMLib.flags().isStanding(mob))
            && (mob.isInCombat())
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, 0, false))
            && (tickID == Tickable.TICKID_MOB)) {
            setActivated(true);
            if (CMLib.dice().rollPercentage() == 1)
                helpProficiency(mob, 0);
        } else
            setActivated(false);
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_JUSTICE,
            affectableStats.getStat(CharStats.STAT_SAVE_JUSTICE)
                + (affectableStats.getStat(CharStats.STAT_CHARISMA) / 4)
                + (affectableStats.getStat(CharStats.STAT_STRENGTH) / 4)
                + (adjustedLevel(affected, 0) / 2));
    }
}
