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

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Beholder extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Beholder");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Unique");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {-1, 10, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"Spell_Sleep", "Spell_FloatingDisc", "Spell_Fear", "Spell_Slow",
        "Spell_Charm", "Prayer_CauseCritical", "Spell_DispelMagic", "Spell_FleshStone",
        "Prayer_DeathFinger", "Spell_Disintegrate"};
    private final int[] racialAbilityLevels = {1, 1, 1, 5, 10, 10, 15, 20, 30, 30};
    private final int[] racialAbilityProficiencies = {50, 50, 50, 50, 50, 50, 100, 50, 50, 50};
    private final boolean[] racialAbilityQuals = {false, false, false, false, false, false, false, false, false, false};
    private final String[] racialAbilityParms = {"", "", "", "", "", "", "", "", "", ""};
    private final String[] culturalAbilityNames = {"Undercommon"};
    private final int[] culturalAbilityProficiencies = {100};
    private final int[] agingChart = {0, 5, 20, 110, 325, 500, 850, 950, 1050};

    @Override
    public String ID() {
        return "Beholder";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 64;
    }

    @Override
    public int shortestFemale() {
        return 60;
    }

    @Override
    public int heightVariance() {
        return 12;
    }

    @Override
    public int lightestWeight() {
        return 100;
    }

    @Override
    public int weightVariance() {
        return 100;
    }

    @Override
    public long forbiddenWornBits() {
        return 0;
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
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public int[] getAgingChart() {
        return agingChart;
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_FLYING);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 25);
        affectableStats.setStat(CharStats.STAT_SAVE_MAGIC, 75);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, 100);
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int x = 0; x < 10; x++) {
                    resources.addElement(makeResource
                        (L("a @x1 eye", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                }
            }
        }
        return resources;
    }
}
