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


public class Bee extends StdMOB {
    public Bee() {
        super();

        username = "a bee";
        setDescription("It\\`s a small buzzing insect with a nasty stinger on its butt.");
        setDisplayText("A bee buzzes around here.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);
        basePhyStats.setWeight(1);
        setWimpHitPoint(2);

        addBehavior(CMClass.getBehavior("Follower"));
        addBehavior(CMClass.getBehavior("CombatAbilities"));
        basePhyStats().setDamage(1);

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);
        basePhyStats().setDisposition(PhyStats.IS_FLYING);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(1);
        basePhyStats().setArmor(80);

        baseCharStats().setMyRace(CMClass.getRace("Insect"));
        baseCharStats().getMyRace().startRacing(this, false);
        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));
        final Ability A = CMClass.getAbility("Poison_BeeSting");
        if (A != null) {
            A.setProficiency(100);
            addAbility(A);
        }

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Bee";
    }

}
