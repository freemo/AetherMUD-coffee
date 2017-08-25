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
package com.planet_ink.game.Abilities.Diseases;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Tickable;


public class Disease_Anthrax extends Disease {
    private final static String localizedName = CMLib.lang().L("Anthrax");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Anthrax)");
    protected int lastHP = Integer.MAX_VALUE;
    protected int conDown = 0;
    protected int conTickDown = 60;
    private boolean norecurse = false;

    @Override
    public String ID() {
        return "Disease_Anthrax";
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
        return 2;
    }

    @Override
    protected int DISEASE_TICKS() {
        return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY) * 10;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 15;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your anthrax wounds clear up.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> look(s) ill.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> watch(s) black necrotic wounds appear on <S-HIS-HER> flesh.");
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_CONSUMPTION | DiseaseAffect.SPREAD_CONTACT;
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
        MOB diseaser = invoker;
        if (diseaser == null)
            diseaser = mob;
        if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, DISEASE_AFFECT());
            final int damage = CMLib.dice().roll(1, 6, 0);
            if (damage > 1) {
                CMLib.combat().postDamage(diseaser, mob, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_DISEASE, -1, null);
            }
            if ((--conTickDown) <= 0) {
                conTickDown = 60;
                conDown++;
            }
            return true;
        }
        lastHP = mob.curState().getHitPoints();
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;
        if (conDown <= 0)
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
}
