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
package com.syncleus.aethermud.game.Abilities.Diseases;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Disease_Gangrene extends Disease {
    private final static String localizedName = CMLib.lang().L("Gangrene");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Gangrene)");
    protected int lastHP = Integer.MAX_VALUE;
    protected int tickUpToDay = 0;
    protected int daysSick = 0;
    private boolean norecurse = false;

    @Override
    public String ID() {
        return "Disease_Gangrene";
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
        return 4;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 100 * CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
    }

    @Override
    protected int DISEASE_DELAY() {
        return 5;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your gangrous wounds feel better.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> look(s) like <S-HE-SHE> <S-HAS-HAVE> gangrous wounds.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> wince(s) in pain.");
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
        tickUpToDay++;
        if (tickUpToDay == CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY)) {
            daysSick++;
            tickUpToDay = 0;
        }
        final MOB mob = (MOB) affected;
        if (mob.curState().getHitPoints() >= mob.maxState().getHitPoints()) {
            unInvoke();
            return false;
        }
        if (lastHP < mob.curState().getHitPoints())
            mob.curState().setHitPoints(mob.curState().getHitPoints()
                - ((mob.curState().getHitPoints() - lastHP) / 2));
        MOB diseaser = invoker;
        if (diseaser == null)
            diseaser = mob;
        if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, DISEASE_AFFECT());
            final int damage = 1;
            CMLib.combat().postDamage(diseaser, mob, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_DISEASE, -1, null);
            if (CMLib.dice().rollPercentage() == 1) {
                final Ability A = CMClass.getAbility("Disease_Fever");
                if (A != null)
                    A.invoke(diseaser, mob, true, 0);
            }
            return true;
        }
        lastHP = mob.curState().getHitPoints();
        return true;
    }

    @Override
    public void affectCharState(MOB affected, CharState affectableState) {
        super.affectCharState(affected, affectableState);
        if (affected == null)
            return;
        if (daysSick > 0) {
            affectableState.setHitPoints(affectableState.getHitPoints() - (daysSick * (affectableState.getHitPoints() / 10)));
            if ((affectableState.getHitPoints() <= 0) && (!norecurse)) {
                MOB diseaser = invoker;
                if (diseaser == null)
                    diseaser = affected;
                norecurse = true;
                CMLib.combat().postDeath(diseaser, affected, null);
                norecurse = false;
            }
        }
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) - 4);
        if (affectableStats.getStat(CharStats.STAT_CHARISMA) < 0)
            affectableStats.setStat(CharStats.STAT_CHARISMA, 0);
    }
}
