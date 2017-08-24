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
package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Disease_Cancer extends Disease {
    private final static String localizedName = CMLib.lang().L("Cancer");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Cancer)");
    protected int conDown = 1;
    private boolean norecurse = false;

    @Override
    public String ID() {
        return "Disease_Cancer";
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
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    public int difficultyLevel() {
        return 5;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 99999;
    }

    @Override
    protected int DISEASE_DELAY() {
        return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your cancer is cured!");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> seem(s) ill.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> <S-IS-ARE> getting sicker...");
    }

    @Override
    public int abilityCode() {
        return 0;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected == null)
            return false;
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_NOISE, DISEASE_AFFECT());
            conDown++;
            return true;
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;
        if (conDown < 0)
            return;
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) - conDown);
        if ((affectableStats.getStat(CharStats.STAT_CONSTITUTION) <= 0) && (!norecurse)) {
            conDown = -1;
            MOB diseaser = invoker;
            if (diseaser == null)
                diseaser = affected;
            norecurse = true;
            CMLib.combat().postDeath(diseaser, affected, null);
            norecurse = false;
        }
    }

    @Override
    public void affectCharState(MOB affected, CharState affectableState) {
        if (affected == null)
            return;
        affectableState.setMovement(affectableState.getMovement() / conDown);
        affectableState.setMana(affectableState.getMana() / conDown);
        affectableState.setHitPoints(affectableState.getHitPoints() / conDown);
    }
}
