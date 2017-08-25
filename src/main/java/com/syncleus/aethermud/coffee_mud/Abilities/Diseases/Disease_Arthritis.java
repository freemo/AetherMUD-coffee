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
package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;


public class Disease_Arthritis extends Disease {
    private final static String localizedName = CMLib.lang().L("Arthritis");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Arthritis)");

    @Override
    public String ID() {
        return "Disease_Arthritis";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    public int difficultyLevel() {
        return 4;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 999999;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 50;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your arthritis clears up.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> look(s) like <S-HE-SHE> <S-IS-ARE> in pain.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return "";
    }

    @Override
    public int abilityCode() {
        return 0;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        if (affected == null)
            return;
        affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) - 3);
        if (affectableStats.getStat(CharStats.STAT_DEXTERITY) <= 0)
            affectableStats.setStat(CharStats.STAT_DEXTERITY, 1);
    }

}
