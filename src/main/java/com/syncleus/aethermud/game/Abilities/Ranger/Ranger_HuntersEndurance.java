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
package com.planet_ink.game.Abilities.Ranger;

import com.planet_ink.game.Abilities.StdAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CharState;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Tickable;


public class Ranger_HuntersEndurance extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Hunters Endurance");
    public volatile CharState oldState = null;

    @Override
    public String ID() {
        return "Ranger_HuntersEndurance";
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public int enchantQuality() {
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
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        if ((tickID == Tickable.TICKID_MOB)
            && (affected instanceof MOB)
            && (((MOB) affected).location() != null)) {
            final MOB mob = (MOB) affected;
            if ((!CMLib.flags().canTrack(affected))
                && (CMLib.flags().isTracking(mob))
                && (!mob.isInCombat())) {
                if (oldState == null)
                    oldState = (CharState) mob.curState().copyOf();
                mob.curState().setHunger(CMProps.getIntVar(CMProps.Int.HUNGER_FULL));
                mob.curState().setThirst(CMProps.getIntVar(CMProps.Int.THIRST_FULL));
                if (proficiency() >= 99) {
                    mob.curState().setFatigue(0);
                    mob.curState().setMovement(mob.maxState().getMovement());
                } else if (proficiencyCheck(mob, 0, false)) {
                    mob.curState().adjFatigue(-proficiency(), mob.maxState());
                    mob.curState().adjMovement(-proficiency(), mob.maxState());
                }
                if (CMLib.dice().rollPercentage() == 1)
                    super.helpProficiency((MOB) affected, 0);
            } else if (oldState != null) {
                mob.curState().setFatigue(oldState.getFatigue());
                mob.curState().setHunger(oldState.getHunger());
                mob.curState().setThirst(oldState.getThirst());
                mob.curState().setMovement(oldState.getMovement());
                oldState = null;
            }
        }
        return true;
    }
}
