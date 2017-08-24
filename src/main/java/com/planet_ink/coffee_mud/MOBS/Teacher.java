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

import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;


public class Teacher extends StdMOB {
    public Teacher() {
        super();
        username = "Cornelius, Knower of All Things";
        setDescription("He looks wise beyond his years.");
        setDisplayText("Cornelius is standing here contemplating your ignorance.");
        CMLib.factions().setAlignment(this, Faction.Align.GOOD);
        setMoney(100);
        basePhyStats.setWeight(150);
        setWimpHitPoint(200);

        Behavior B = CMClass.getBehavior("MOBTeacher");
        if (B != null)
            addBehavior(B);
        B = CMClass.getBehavior("MudChat");
        if (B != null)
            addBehavior(B);
        B = CMClass.getBehavior("CombatAbilities");
        if (B != null)
            addBehavior(B);

        for (final int i : CharStats.CODES.BASECODES())
            baseCharStats().setStat(i, 25);
        baseCharStats().setMyRace(CMClass.getRace("Human"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setAbility(10);
        basePhyStats().setLevel(25);
        basePhyStats().setArmor(-500);
        setAttribute(MOB.Attrib.NOTEACH, false);

        baseState.setHitPoints(4999);
        baseState.setMana(4999);
        baseState.setMovement(4999);

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "Teacher";
    }

}
