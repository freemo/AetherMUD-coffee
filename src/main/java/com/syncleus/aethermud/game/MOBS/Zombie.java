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

import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;


public class Zombie extends Undead {
    public Zombie() {

        super();
        username = "a zombie";
        setDescription("decayed and rotting, a dead body has been brought back to life...");
        setDisplayText("a zombie slowly moves about.");
        setMoney(10);
        basePhyStats.setWeight(30);
        baseCharStats().setMyRace(CMClass.getRace("Undead"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(8);
        basePhyStats().setLevel(2);
        basePhyStats().setArmor(90);
        basePhyStats().setSpeed(1.0);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Zombie";
    }

}
