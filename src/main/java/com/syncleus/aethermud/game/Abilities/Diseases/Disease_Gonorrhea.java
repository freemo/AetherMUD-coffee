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
import com.syncleus.aethermud.game.Abilities.interfaces.DiseaseAffect;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Common.interfaces.Social;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Disease_Gonorrhea extends Disease {
    private final static String localizedName = CMLib.lang().L("Gonorrhea");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Gonorrhea)");

    @Override
    public String ID() {
        return "Disease_Gonorrhea";
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
        return 1;
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
        return L("Your gonorrhea clears up.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> squeeze(s) <S-HIS-HER> privates uncomfortably.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> squeeze(s) <S-HIS-HER> privates uncomfortably.");
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_STD;
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
        if ((CMLib.dice().rollPercentage() == 1)
            && (CMLib.dice().rollPercentage() > mob.charStats().getSave(CharStats.STAT_SAVE_COLD))
            && (!mob.amDead())
            && (CMLib.dice().rollPercentage() < 25 - mob.charStats().getStat(CharStats.STAT_CONSTITUTION))) {
            MOB diseaser = invoker;
            if (diseaser == null)
                diseaser = mob;
            final Ability A = CMClass.getAbility("Disease_Arthritis");
            A.invoke(diseaser, mob, true, 0);
        } else if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_NOISE, DISEASE_AFFECT());
            return true;
        }
        return true;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - 5);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected == null)
            return super.okMessage(myHost, msg);
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (((msg.amITarget(mob)) || (msg.amISource(mob)))
                && (msg.tool() instanceof Social)
                && (msg.tool().Name().equals("MATE <T-NAME>")
                || msg.tool().Name().equals("SEX <T-NAME>"))) {
                msg.source().tell(mob, null, null, L("<S-NAME> really do(es)n't feel like it."));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }
}
