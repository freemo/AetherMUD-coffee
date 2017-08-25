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
import com.planet_ink.game.Items.interfaces.Armor;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Githyanki extends Humanoid {
    private final static String localizedStaticName = CMLib.lang().L("Githyanki");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Humanoid");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final int[] agingChart = {0, 1, 5, 40, 100, 150, 200, 230, 260};
    private final String[] culturalAbilityNames = {"Thief_Hide", "Spell_AstralStep"};
    private final int[] culturalAbilityProficiencies = {25, 5};

    @Override
    public String ID() {
        return "Githyanki";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
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
    public String[] culturalAbilityNames() {
        return culturalAbilityNames;
    }

    @Override
    public int[] culturalAbilityProficiencies() {
        return culturalAbilityProficiencies;
    }

    @Override
    public int getXPAdjustment() {
        return -20;
    }

    @Override
    public List<Item> outfit(MOB myChar) {
        if (outfitChoices == null) {
            // Have to, since it requires use of special constructor
            final Armor s1 = CMClass.getArmor("GenShirt");
            if (s1 == null)
                return new Vector<Item>();
            outfitChoices = new Vector<Item>();
            outfitChoices.add(s1);
            final Armor s2 = CMClass.getArmor("GenShoes");
            outfitChoices.add(s2);
            final Armor p1 = CMClass.getArmor("GenPants");
            outfitChoices.add(p1);
            final Armor s3 = CMClass.getArmor("GenBelt");
            outfitChoices.add(s3);
        }
        return outfitChoices;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 10);
        affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 1);
        affectableStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ, affectableStats.getStat(CharStats.STAT_MAX_DEXTERITY_ADJ) + 1);
        affectableStats.setStat(CharStats.STAT_WISDOM, affectableStats.getStat(CharStats.STAT_WISDOM) + 1);
        affectableStats.setStat(CharStats.STAT_MAX_WISDOM_ADJ, affectableStats.getStat(CharStats.STAT_MAX_WISDOM_ADJ) + 1);
    }
}
