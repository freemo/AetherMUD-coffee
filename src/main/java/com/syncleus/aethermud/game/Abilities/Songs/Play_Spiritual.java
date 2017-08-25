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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Play_Spiritual extends Play {
    private final static String localizedName = CMLib.lang().L("Spiritual");

    @Override
    public String ID() {
        return "Play_Spiritual";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected String songOf() {
        return L("Spiritual Music");
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if ((mob.isInCombat()) && (mob.isMonster()))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        final Room R = CMLib.map().roomLocation(affected);
        if (R != null)
            for (int m = 0; m < R.numInhabitants(); m++) {
                final MOB mob = R.fetchInhabitant(m);
                if (mob != null)
                    for (int i = 0; i < mob.numEffects(); i++) // personal
                    {
                        final Ability A = mob.fetchEffect(i);
                        if ((A != null)
                            && (A instanceof StdAbility)
                            && (A.abstractQuality() != Ability.QUALITY_MALICIOUS)
                            && ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
                            && (((StdAbility) A).getTickDownRemaining() == 1))
                            ((StdAbility) A).setTickDownRemaining(2);
                    }
            }
        return true;
    }
}
