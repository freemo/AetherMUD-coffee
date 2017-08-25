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
package com.syncleus.aethermud.game.Abilities.Poisons;

import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Poison_Bloodboil extends Poison {
    private final static String localizedName = CMLib.lang().L("Blood Boil");
    private static final String[] triggerStrings = I(new String[]{"POISONBURN"});

    @Override
    public String ID() {
        return "Poison_Bloodboil";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int POISON_TICKS() {
        return 20;
    } // 0 means no adjustment!

    @Override
    protected int POISON_DELAY() {
        return 2;
    }

    @Override
    protected String POISON_DONE() {
        return "Your blood stops burning.";
    }

    @Override
    protected String POISON_START() {
        return "^R<S-NAME> turn(s) red.^?";
    }

    @Override
    protected String POISON_AFFECT() {
        return "<S-NAME> cringe(s) as <S-HIS-HER> blood burns.";
    }

    @Override
    protected String POISON_CAST() {
        return "^F^<FIGHT^><S-NAME> sting(s) <T-NAMESELF>!^</FIGHT^>^?";
    }

    @Override
    protected String POISON_FAIL() {
        return "<S-NAME> attempt(s) to sting <T-NAMESELF>, but fail(s).";
    }

    @Override
    protected int POISON_DAMAGE() {
        return (invoker != null) ? CMLib.dice().roll(1, 2, 0) : 0;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (affected instanceof MOB)
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - 20);
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 1);
        affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) - 5);
        if (affectableStats.getStat(CharStats.STAT_CONSTITUTION) <= 0)
            affectableStats.setStat(CharStats.STAT_CONSTITUTION, 1);
        if (affectableStats.getStat(CharStats.STAT_STRENGTH) <= 0)
            affectableStats.setStat(CharStats.STAT_STRENGTH, 1);
    }
}
