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
package com.planet_ink.game.MOBS;

import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.Faction;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;

import java.util.Random;


public class Hawk extends StdMOB {
    public Hawk() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a hawk";
        setDescription("a hunting bird with a narrow face, shark beak, and nasty talons.");
        setDisplayText("An hawk soars overhead.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);
        setWimpHitPoint(0);

        basePhyStats.setWeight(1 + Math.abs(randomizer.nextInt() % 6));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 9);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 15);

        basePhyStats().setDamage(1);
        basePhyStats().setSpeed(3.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(2);
        basePhyStats().setArmor(90);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_FLYING);
        baseCharStats().setMyRace(CMClass.getRace("Hawk"));
        baseCharStats().getMyRace().startRacing(this, false);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Hawk";
    }

}
