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
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Disease_Infection extends Disease {
    private final static String localizedName = CMLib.lang().L("Infection");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Infected Wounds)");
    protected int lastHP = Integer.MAX_VALUE;

    @Override
    public String ID() {
        return "Disease_Infection";
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
        return 0;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 34;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 5;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your infected wounds feel better.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> look(s) like <S-HE-SHE> <S-HAS-HAVE> infected wounds.^?");
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

        final MOB mob = (MOB) affected;
        if (mob.curState().getHitPoints() >= mob.maxState().getHitPoints()) {
            unInvoke();
            return false;
        }
        if (lastHP < mob.curState().getHitPoints()) {
            mob.curState().setHitPoints(mob.curState().getHitPoints()
                - ((mob.curState().getHitPoints() - lastHP) / 2));
        }
        MOB diseaser = invoker;
        if (diseaser == null)
            diseaser = mob;
        if ((getTickDownRemaining() == 1)
            && (!mob.amDead())
            && (CMLib.dice().rollPercentage() > mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE))
            && (CMLib.dice().rollPercentage() < 25 - mob.charStats().getStat(CharStats.STAT_CONSTITUTION))) {
            mob.delEffect(this);
            final Ability A = CMClass.getAbility("Disease_Gangrene");
            A.invoke(diseaser, mob, true, 0);
        } else if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
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
}
