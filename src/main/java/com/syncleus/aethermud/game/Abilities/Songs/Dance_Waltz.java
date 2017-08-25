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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Dance_Waltz extends Dance {
    private final static String localizedName = CMLib.lang().L("Waltz");
    private int[] statadd = null;

    @Override
    public String ID() {
        return "Dance_Waltz";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
        super.affectCharStats(affectedMOB, affectedStats);
        if (statadd == null) {
            statadd = new int[CharStats.CODES.TOTAL()];
            int classLevel = CMLib.ableMapper().qualifyingClassLevel(invoker(), this) + (3 * getXLEVELLevel(invoker()));
            classLevel = (classLevel + 1) / 9;
            classLevel++;

            for (int i = 0; i < classLevel; i++)
                statadd[CharStats.CODES.BASECODES()[CMLib.dice().roll(1, CharStats.CODES.BASECODES().length, -1)]] += 3;
        }
        for (final int i : CharStats.CODES.BASECODES())
            affectedStats.setStat(i, affectedStats.getStat(i) + statadd[i]);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        statadd = null;
        return super.invoke(mob, commands, givenTarget, auto, asLevel);
    }

}
