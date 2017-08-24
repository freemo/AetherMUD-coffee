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
package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Duergar extends Dwarf {
    private final static String localizedStaticName = CMLib.lang().L("Duergar");
    private final String[] racialEffectNames = {};
    private final int[] racialEffectLevels = {};
    private final String[] racialEffectParms = {};
    private final String[] culturalAbilityNames = {"Dwarven", "Mining", "Undercommon", "Spell_Invisibility", "Spell_Grow"};
    private final int[] culturalAbilityProficiencies = {100, 50, 25, 25, 25};
    public Duergar() {
        super();
        super.naturalAbilImmunities.add("Disease_Syphilis");
    }

    @Override
    public String ID() {
        return "Duergar";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    protected String[] racialEffectNames() {
        return racialEffectNames;
    }

    @Override
    protected int[] racialEffectLevels() {
        return racialEffectLevels;
    }

    @Override
    protected String[] racialEffectParms() {
        return racialEffectParms;
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        final int senses = affectableStats.sensesMask();
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(senses | PhyStats.CAN_SEE_DARK);
    }

    @Override
    public int getXPAdjustment() {
        return -10;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) - 3);
        affectableStats.setStat(CharStats.STAT_MAX_CHARISMA_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CHARISMA_ADJ) - 3);
        affectableStats.setStat(CharStats.STAT_SAVE_FIRE, affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + 15);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 5);
        affectableStats.setStat(CharStats.STAT_SAVE_COLD, affectableStats.getStat(CharStats.STAT_SAVE_COLD) - 15);
        affectableStats.setStat(CharStats.STAT_SAVE_WATER, affectableStats.getStat(CharStats.STAT_SAVE_WATER) - 10);
        affectableStats.setStat(CharStats.STAT_SAVE_DISEASE, affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) - 10);
    }
}
