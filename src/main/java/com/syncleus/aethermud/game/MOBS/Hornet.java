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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.Faction;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class Hornet extends StdMOB {
    public Hornet() {
        super();

        username = "a hornet";
        setDescription("It\\`s a small mean flying insect with a nasty stinger on its butt.");
        setDisplayText("A hornet flits around here.");
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
        final Ability A = CMClass.getAbility("Poison_Sting");
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
        return "Hornet";
    }

}
