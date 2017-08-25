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
package com.syncleus.aethermud.game.MOBS;

import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.Faction;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class Elephant extends StdMOB {
    public Elephant() {
        super();
        username = "an elephant";
        setDescription("A large chubby animals with leathery skin, large ears, and a long prehensile nose.");
        setDisplayText("An elephant is heading somewhere.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);

        basePhyStats().setDamage(20);

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);

        basePhyStats().setAbility(0);
        basePhyStats().setLevel(5);
        basePhyStats().setArmor(70);

        baseCharStats().setMyRace(CMClass.getRace("Elephant"));
        baseCharStats().getMyRace().startRacing(this, false);
        baseState.setHitPoints(CMLib.dice().rollHP(basePhyStats.level(), 20));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Elephant";
    }

}
