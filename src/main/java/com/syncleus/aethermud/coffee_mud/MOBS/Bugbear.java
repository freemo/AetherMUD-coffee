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


public class Bugbear extends StdMOB {
    public Bugbear() {
        super();
        username = "a Bugbear";
        setDescription("a 7 foot tall, hairy, yellow-brown, muscular creature with sharp teeth and recessed eyes.");
        setDisplayText("A large Bugbear stands here.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(20);
        basePhyStats.setWeight(300);
        setWimpHitPoint(0);

        final Weapon h = CMClass.getWeapon("Halberd");
        if (h != null) {
            h.wearAt(Wearable.WORN_WIELD);
            addItem(h);
        }

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 6);
        baseCharStats().setStat(CharStats.STAT_CHARISMA, 2);
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 22);

        basePhyStats().setAbility(0);
        basePhyStats().setLevel(3);
        basePhyStats().setArmor(70);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Bugbear";
    }

}
