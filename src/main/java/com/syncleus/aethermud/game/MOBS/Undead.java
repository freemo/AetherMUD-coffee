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

import com.syncleus.aethermud.game.Common.interfaces.Faction;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class Undead extends StdMOB {
    public Undead() {
        super();
        username = "an undead being";
        setDescription("decayed and rotting, a dead body has been brought back to life...");
        setDisplayText("an undead thing slowly moves about.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(10);
        basePhyStats.setWeight(30);
        setWimpHitPoint(0);

        baseCharStats().setMyRace(CMClass.getRace("Undead"));
        baseCharStats().getMyRace().startRacing(this, false);
        basePhyStats().setDamage(8);

        basePhyStats().setAbility(0);
        basePhyStats().setLevel(1);
        basePhyStats().setArmor(90);
        basePhyStats().setSpeed(1.0);
        basePhyStats().setDisposition(0); // disable infrared stuff
        basePhyStats().setSensesMask(PhyStats.CAN_SEE_DARK);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        addNonUninvokableEffect(CMClass.getAbility("Skill_AllBreathing"));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Undead";
    }

}
