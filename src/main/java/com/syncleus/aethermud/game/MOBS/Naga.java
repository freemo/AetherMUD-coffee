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


public class Naga extends StdMOB {
    public Naga() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a naga";
        setDisplayText("A naga is here");
        setDescription("A sharp-featured humanoid head crowns the powerful, sinuous body of this snake-like monstrosity."
            + "  Two slender arms extend from the body of the Naga just below the head.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(20);
        basePhyStats.setWeight(100 + Math.abs(randomizer.nextInt() % 101));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 13 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 12 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 15 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setMyRace(CMClass.getRace("Naga"));

        basePhyStats().setDamage(7);
        basePhyStats().setSpeed(2.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(4);
        basePhyStats().setArmor(80);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        addBehavior(CMClass.getBehavior("Mobile"));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Naga";
    }

}
