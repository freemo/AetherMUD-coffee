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
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;

import java.util.Random;


public class Minotaur extends StdMOB {
    public Minotaur() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a minotaur";
        setDescription("A tall humanoid with the head of a bull, and the body of a very muscular man.  It\\`s covered in red fur.");
        setDisplayText("A minotaur glares at you.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(0);
        basePhyStats.setWeight(350 + Math.abs(randomizer.nextInt() % 55));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 4 + Math.abs(randomizer.nextInt() % 5));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 18);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 15);
        baseCharStats().setMyRace(CMClass.getRace("Minotaur"));
        baseCharStats().getMyRace().startRacing(this, false);

        final Weapon mainWeapon = CMClass.getWeapon("BattleAxe");
        if (mainWeapon != null) {
            mainWeapon.wearAt(Wearable.WORN_WIELD);
            this.addItem(mainWeapon);
        }

        basePhyStats().setDamage(12);
        basePhyStats().setSpeed(2.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(6);
        basePhyStats().setArmor(70);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        addBehavior(CMClass.getBehavior("Aggressive"));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Minotaur";
    }

}
