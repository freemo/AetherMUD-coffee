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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;


public class Poison_XXX extends Poison {
    private final static String localizedName = CMLib.lang().L("XXX");
    private static final String[] triggerStrings = I(new String[]{"POISONXXX"});

    @Override
    public String ID() {
        return "Poison_XXX";
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
        return 25;
    } // 0 means no adjustment!

    @Override
    protected int POISON_DELAY() {
        return 2;
    }

    @Override
    protected String POISON_DONE() {
        return "The poison runs its course.";
    }

    @Override
    protected String POISON_START() {
        return "^G<S-NAME> turn(s) green.^?";
    }

    @Override
    protected String POISON_AFFECT() {
        return "<S-NAME> cringe(s) in horrible pain as the poison courses through <S-HIS-HER> blood.";
    }

    @Override
    protected String POISON_CAST() {
        return "^F^<FIGHT^><S-NAME> bite(s) <T-NAMESELF>!^</FIGHT^>^?";
    }

    @Override
    protected String POISON_FAIL() {
        return "<S-NAME> attempt(s) to bite <T-NAMESELF>, but fail(s).";
    }

    @Override
    protected int POISON_DAMAGE() {
        return (invoker != null) ? CMLib.dice().roll(1, 99, 1) : 0;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 5);
        affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) - 1);
        if (affectableStats.getStat(CharStats.STAT_CONSTITUTION) <= 0)
            affectableStats.setStat(CharStats.STAT_CONSTITUTION, 1);
        if (affectableStats.getStat(CharStats.STAT_STRENGTH) <= 0)
            affectableStats.setStat(CharStats.STAT_STRENGTH, 1);
    }
}
