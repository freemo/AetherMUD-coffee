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

import java.util.Random;


public class BrownBear extends StdMOB {
    public BrownBear() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a Brown Bear";
        setDescription("A bear, large and husky with brown fur.");
        setDisplayText("A brown bear hunts here.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);
        basePhyStats.setWeight(20 + Math.abs(randomizer.nextInt() % 45));
        setWimpHitPoint(2);

        basePhyStats.setWeight(450 + Math.abs(randomizer.nextInt() % 55));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 18);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 16);
        baseCharStats().setMyRace(CMClass.getRace("Bear"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(8);
        basePhyStats().setSpeed(2.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(5);
        basePhyStats().setArmor(70);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "BrownBear";
    }

}
