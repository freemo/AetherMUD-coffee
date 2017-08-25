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
package com.planet_ink.coffee_mud.MOBS;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;

import java.util.Random;


public class Pegasus extends StdRideable {
    Random randomizer = null;

    public Pegasus() {
        super();
        randomizer = new Random(System.currentTimeMillis());

        username = "a Pegasus";
        setDescription("a beautiful, white stallion with wings.");
        setDisplayText("A Pegasus flaps its wings.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);
        setWimpHitPoint(0);
        rideBasis = Rideable.RIDEABLE_AIR;

        basePhyStats.setWeight(1500 + Math.abs(randomizer.nextInt() % 200));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 8 + Math.abs(randomizer.nextInt() % 3));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 11);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 17);
        baseCharStats().setMyRace(CMClass.getRace("Horse"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(8);
        basePhyStats().setSpeed(3.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(4);
        basePhyStats().setArmor(60);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_FLYING);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Pegasus";
    }

}
