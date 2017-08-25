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
package com.planet_ink.game.Races;

import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Races.interfaces.Race;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;


public class Alligator extends GreatLizard {
    private final static String localizedStaticName = CMLib.lang().L("Alligator");
    private final String[] racialAbilityNames = {"Skill_Swim", "AlligatorSpeak"};
    private final int[] racialAbilityLevels = {1, 1};
    private final int[] racialAbilityProficiencies = {100, 100};
    private final boolean[] racialAbilityQuals = {false, false};
    private final String[] racialAbilityParms = {"", ""};
    public Alligator() {
        super();
        super.naturalAbilImmunities.add("Disease_Gonorrhea");
        super.naturalAbilImmunities.add("Disease_Malaria");
    }

    @Override
    public String ID() {
        return "Alligator";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    protected String[] racialAbilityNames() {
        return racialAbilityNames;
    }

    @Override
    protected int[] racialAbilityLevels() {
        return racialAbilityLevels;
    }

    @Override
    protected int[] racialAbilityProficiencies() {
        return racialAbilityProficiencies;
    }

    @Override
    protected boolean[] racialAbilityQuals() {
        return racialAbilityQuals;
    }

    @Override
    public String[] racialAbilityParms() {
        return racialAbilityParms;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SWIMMING);
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
                return name().toLowerCase() + " hatchling";
            case Race.AGE_CHILD:
                return "young " + name().toLowerCase();
            default:
                return super.makeMobName(gender, age);
        }
    }
}
