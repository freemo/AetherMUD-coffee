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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Disease_Vampirism extends Disease {
    private final static String localizedName = CMLib.lang().L("Vampirism");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Vampirism)");

    @Override
    public String ID() {
        return "Disease_Vampirism";
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
    protected int DISEASE_TICKS() {
        return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY) * 6;
    }

    @Override
    protected int DISEASE_DELAY() {
        return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your vampirism lifts.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> seem(s) pale and cold.^?");
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
    public int difficultyLevel() {
        return 9;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (!(affected instanceof MOB))
            return;
        if (((MOB) affected).location() == null)
            return;
        if (CMLib.flags().isInDark(((MOB) affected).location()))
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
        else
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SEE);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (msg.amISource(mob)
                && (msg.tool() != null)
                && (msg.tool().ID().equals("Skill_Swim"))) {
                mob.tell(L("You can't swim!"));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 1);
    }
}
