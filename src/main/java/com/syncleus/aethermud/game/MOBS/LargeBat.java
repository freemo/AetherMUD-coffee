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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.Random;


public class LargeBat extends StdMOB {
    public LargeBat() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a large bat";
        setDescription("It looks like a bat, just larger.");
        setDisplayText("A large bat flies nearby.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);
        setWimpHitPoint(0);

        basePhyStats.setWeight(1 + Math.abs(randomizer.nextInt() % 10));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 12);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 17);
        baseCharStats().setMyRace(CMClass.getRace("Bat"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(5);
        basePhyStats().setSpeed(1.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(2);
        basePhyStats().setArmor(90);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_FLYING);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "LargeBat";
    }

}
