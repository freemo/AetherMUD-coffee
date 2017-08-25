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
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Disease_Lockjaw extends Disease {
    private final static String localizedName = CMLib.lang().L("Lockjaw");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Lockjaw)");

    @Override
    public String ID() {
        return "Disease_Lockjaw";
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
        return 9999999;
    }

    @Override
    protected int DISEASE_DELAY() {
        return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your lockjaw is cured.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> get(s) lockjaw!^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return "";
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_CONSUMPTION;
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
            if (CMLib.dice().rollPercentage() < mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE)) {
                unInvoke();
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return super.okMessage(myHost, msg);

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if ((msg.amISource(mob))
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && ((msg.sourceMinor() == CMMsg.TYP_EAT) || (msg.sourceMinor() == CMMsg.TYP_DRINK))) {
            mob.tell(L("You can't open your mouth!"));
            return false;
        }

        return super.okMessage(myHost, msg);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (affected == null)
            return;
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK);
    }
}
