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
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;

import java.util.Random;


public class Gnoll extends StdMOB {
    public Gnoll() {
        super();
        username = "a Gnoll";
        setDescription("a 7 foot tall creature with a body resembling a large human and the head of a hyena.");
        setDisplayText("A nasty Gnoll stands here.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(20);
        basePhyStats.setWeight(300);
        setWimpHitPoint(0);

        Weapon h = CMClass.getWeapon("MorningStar");
        final Random randomizer = new Random(System.currentTimeMillis());
        final int percentage = randomizer.nextInt() % 100;
        if ((percentage & 1) == 0) {
            h = CMClass.getWeapon("Longsword");
        }
        if (h != null) {
            h.wearAt(Wearable.WORN_WIELD);
            addItem(h);
        }

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 6);
        baseCharStats().setStat(CharStats.STAT_CHARISMA, 2);
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 22);

        basePhyStats().setAbility(0);
        basePhyStats().setLevel(2);
        basePhyStats().setArmor(90);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Gnoll";
    }

}
