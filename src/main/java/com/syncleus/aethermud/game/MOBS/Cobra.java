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
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class Cobra extends StdMOB {
    public Cobra() {
        super();
        username = "a cobra";
        setDescription("A fearsome creature with long fangs and a menacing head.");
        setDisplayText("A cobra is hissing at you.");
        CMLib.factions().setAlignment(this, Faction.Align.NEUTRAL);
        setMoney(0);

        addBehavior(CMClass.getBehavior("CombatAbilities"));
        addAbility(CMClass.getAbility("Poison"));

        basePhyStats().setDamage(4);

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 1);

        basePhyStats().setAbility(0);
        basePhyStats().setLevel(1);
        basePhyStats().setArmor(90);

        baseCharStats().setMyRace(CMClass.getRace("Cobra"));
        baseCharStats().getMyRace().startRacing(this, false);
        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Cobra";
    }

}
