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

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;

import java.util.Random;


public class FlyingInsect extends StdMOB {
    public FlyingInsect() {
        super();
        final Random randomizer = new Random(System.currentTimeMillis());

        username = "a flying insect";
        setDescription("The small flying bug is too tiny to tell whether it bites or stings.");
        setDisplayText("A flying insect flits around.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);
        setWimpHitPoint(2);

        basePhyStats().setWeight(Math.abs(randomizer.nextInt() % 2));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 1);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 15);
        baseCharStats().setMyRace(CMClass.getRace("Insect"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(10);
        basePhyStats().setSpeed(1.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(1);
        basePhyStats().setArmor(90);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_FLYING);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        final Ability A = CMClass.getAbility("Poison_Sting");
        if (A != null) {
            A.setProficiency(100);
            addAbility(A);
        }

        addBehavior(CMClass.getBehavior("Mobile"));
        addBehavior(CMClass.getBehavior("CombatAbilities"));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();

    }

    @Override
    public String ID() {
        return "FlyingInsect";
    }
}
