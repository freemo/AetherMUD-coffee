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
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.Random;


public class DrowElf extends StdMOB {
    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public int darkDown = 4;

    public DrowElf() {
        super();

        final Random randomizer = new Random(System.currentTimeMillis());

        basePhyStats().setLevel(4 + Math.abs(randomizer.nextInt() % 7));

        final int gender = Math.abs(randomizer.nextInt() % 2);
        String sex = null;
        if (gender == MALE)
            sex = "male";
        else
            sex = "female";

        // ===== set the basics
        username = "a Drow Elf";
        setDescription("a " + sex + " Drow Fighter");
        setDisplayText("The drow is armored in black chain mail and carrying a nice arsenal of weapons");

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));
        setMoney((int) Math.round(CMath.div((50 * basePhyStats().level()), (randomizer.nextInt() % 10 + 1))));
        basePhyStats.setWeight(70 + Math.abs(randomizer.nextInt() % 20));

        setWimpHitPoint(5);

        basePhyStats().setSpeed(2.0);
        basePhyStats().setSensesMask(PhyStats.CAN_SEE_DARK | PhyStats.CAN_SEE_INFRARED);

        if (gender == MALE)
            baseCharStats().setStat(CharStats.STAT_GENDER, 'M');
        else
            baseCharStats().setStat(CharStats.STAT_GENDER, 'F');

        baseCharStats().setStat(CharStats.STAT_STRENGTH, 12 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 14 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_WISDOM, 13 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 15 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_CONSTITUTION, 12 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_CHARISMA, 13 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setMyRace(CMClass.getRace("Elf"));
        baseCharStats().getMyRace().startRacing(this, false);

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "DrowElf";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((!amDead()) && (tickID == Tickable.TICKID_MOB)) {
            if (isInCombat()) {
                if ((--darkDown) <= 0) {
                    darkDown = 4;
                    castDarkness();
                }
            }

        }
        return super.tick(ticking, tickID);
    }

    protected boolean castDarkness() {
        if (this.location() == null)
            return true;
        if (CMLib.flags().isInDark(this.location()))
            return true;

        Ability dark = CMClass.getAbility("Spell_Darkness");
        dark.setProficiency(100);
        dark.setSavable(false);
        if (this.fetchAbility(dark.ID()) == null)
            this.addAbility(dark);
        else
            dark = this.fetchAbility(dark.ID());

        if (dark != null)
            dark.invoke(this, null, true, 0);
        return true;
    }

}
