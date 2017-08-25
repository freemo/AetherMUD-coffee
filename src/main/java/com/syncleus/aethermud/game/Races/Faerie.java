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
package com.syncleus.aethermud.game.Races;

import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Faerie extends SmallElfKin {
    private final static String localizedStaticName = CMLib.lang().L("Faerie");
    //                                     an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 2};
    private final static String localizedStaticRacialCat = CMLib.lang().L("Fairy-kin");
    private final String[] racialAbilityNames = {"WingFlying"};
    private final int[] racialAbilityLevels = {1};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {""};
    private final String[] culturalAbilityNames = {"Fey", "Foraging"};
    private final int[] culturalAbilityProficiencies = {100, 50};

    @Override
    public String ID() {
        return "Faerie";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
    }

    @Override
    public String[] racialAbilityNames() {
        return racialAbilityNames;
    }

    @Override
    public int[] racialAbilityLevels() {
        return racialAbilityLevels;
    }

    @Override
    public int[] racialAbilityProficiencies() {
        return racialAbilityProficiencies;
    }

    @Override
    public boolean[] racialAbilityQuals() {
        return racialAbilityQuals;
    }

    @Override
    public String[] racialAbilityParms() {
        return racialAbilityParms;
    }

    @Override
    public String[] culturalAbilityNames() {
        return culturalAbilityNames;
    }

    @Override
    public int[] culturalAbilityProficiencies() {
        return culturalAbilityProficiencies;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) + 2);
        affectableStats.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ) + 2);
        affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 2);
        affectableStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ, affectableStats.getStat(CharStats.STAT_MAX_DEXTERITY_ADJ) + 2);
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 10);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((affectableStats.disposition() & (PhyStats.IS_SITTING | PhyStats.IS_SLEEPING)) == 0)
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_FLYING);
    }
}
