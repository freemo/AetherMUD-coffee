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


public class Play_Rhythm extends Play {
    private final static String localizedName = CMLib.lang().L("Rhythm");

    @Override
    public String ID() {
        return "Play_Rhythm";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public void affectCharStats(MOB mob, CharStats stats) {
        super.affectCharStats(mob, stats);
        if (mob == invoker)
            return;
        if (invoker() != null)
            stats.setStat(CharStats.STAT_SAVE_MAGIC, stats.getStat(CharStats.STAT_SAVE_MAGIC)
                - (invoker().charStats().getStat(CharStats.STAT_CHARISMA)
                + (adjustedLevel(invoker(), 0) * 2)));
    }
}

