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
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;

import java.util.Random;


public class StoneGiant extends StdMOB {
    public StoneGiant() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a Stone Giant";
        setDescription("A tall humanoid standing about 18 feet tall with gray, hairless flesh.");
        setDisplayText("A Stone Giant glares at you.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(0);
        basePhyStats.setWeight(8000 + Math.abs(randomizer.nextInt() % 1001));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 8 + Math.abs(randomizer.nextInt() % 3));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 20);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 13);

        basePhyStats().setDamage(20);
        basePhyStats().setSpeed(1.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(14);
        basePhyStats().setArmor(0);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        addBehavior(CMClass.getBehavior("Aggressive"));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "StoneGiant";
    }

}
