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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Song_Silence extends Song {
    private final static String localizedName = CMLib.lang().L("Silencing");

    @Override
    public String ID() {
        return "Song_Silence";
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
    protected boolean skipStandardSongTick() {
        return true;
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (invoker == null)
            return;
        if (affected == invoker)
            return;

        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_HEAR);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK);
    }
}
