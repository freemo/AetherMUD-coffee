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


public class HillGiant extends StdMOB {
    public HillGiant() {
        super();
        username = "a Hill Giant";
        setDescription("A tall humanoid standing about 16 feet tall and very smelly.");
        setDisplayText("A Hill Giant glares at you.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(0);
        basePhyStats.setWeight(3500 + CMLib.dice().roll(1, 1000, 0));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 6 + CMLib.dice().roll(1, 2, 0));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 20);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 13);
        baseCharStats().setMyRace(CMClass.getRace("Giant"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(19);
        basePhyStats().setSpeed(1.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(12);
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
        return "HillGiant";
    }

}
