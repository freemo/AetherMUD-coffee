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

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Armor;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Mindflayer extends Humanoid {
    private final static String localizedStaticName = CMLib.lang().L("Mindflayer");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Illithid");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 0};
    protected static List<RawMaterial> resources = new Vector<RawMaterial>();
    final String brainStr;
    final String brainStrs;
    private final String[] culturalAbilityNames = {"Spell_MindFog", "Spell_Charm", "Undercommon"};
    private final int[] culturalAbilityProficiencies = {100, 50, 25};
    private final String[] racialAbilityNames = {"Skill_MindSuck", "Spell_DetectSentience", "Spell_CombatPrecognition"};
    private final int[] racialAbilityLevels = {1, 10, 30};
    private final int[] racialAbilityProficiencies = {100, 50, 30};
    private final boolean[] racialAbilityQuals = {false, false, false};
    private final String[] racialAbilityParms = {"", "", ""};
    private final int[] agingChart = {0, 2, 20, 110, 175, 263, 350, 390, 430};

    public Mindflayer() {
        super();
        brainStr = L("brain");
        brainStrs = L("brains");
    }

    @Override
    public String ID() {
        return "Mindflayer";
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
    public String[] culturalAbilityNames() {
        return culturalAbilityNames;
    }

    @Override
    public int[] culturalAbilityProficiencies() {
        return culturalAbilityProficiencies;
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
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public int[] getAgingChart() {
        return agingChart;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
    }

    @Override
    public List<Item> outfit(MOB myChar) {
        if (outfitChoices == null) {
            // Have to, since it requires use of special constructor
            final Armor s1 = CMClass.getArmor("GenArmor");
            if (s1 == null)
                return new Vector<Item>();
            outfitChoices = new Vector<Item>();
            s1.setName(L("grey robes"));
            s1.setMaterial(RawMaterial.RESOURCE_COTTON);
            s1.setDisplayText(L("a pile of grey robes have been left here."));
            s1.setDescription(L("Just ordinary grey robes."));
            s1.setRawProperLocationBitmap(Wearable.WORN_ABOUT_BODY | Wearable.WORN_ARMS | Wearable.WORN_TORSO);
            s1.setRawLogicalAnd(true);
            s1.text();
            outfitChoices.add(s1);

            final Armor s3 = CMClass.getArmor("GenBelt");
            outfitChoices.add(s3);
        }
        return outfitChoices;
    }

    @Override
    public int getXPAdjustment() {
        return -25;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((msg.targetMinor() == CMMsg.TYP_EAT)
            && (myHost instanceof MOB)
            && (msg.amISource((MOB) myHost))
            && (!CMStrings.containsWord(msg.target().name(), brainStr))
            && (!CMStrings.containsWord(msg.target().name(), brainStrs))) {
            msg.source().tell(L("You can't eat that!"));
            return false;
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) - 4);
        affectableStats.setStat(CharStats.STAT_MAX_STRENGTH_ADJ, affectableStats.getStat(CharStats.STAT_MAX_STRENGTH_ADJ) - 4);
        affectableStats.setStat(CharStats.STAT_INTELLIGENCE, affectableStats.getStat(CharStats.STAT_INTELLIGENCE) + 4);
        affectableStats.setStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ, affectableStats.getStat(CharStats.STAT_MAX_INTELLIGENCE_ADJ) + 4);
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 2);
        affectableStats.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ) - 2);
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 2);
        affectableStats.setStat(CharStats.STAT_MAX_CHARISMA_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CHARISMA_ADJ) + 2);
        affectableStats.setStat(CharStats.STAT_SAVE_MAGIC, affectableStats.getStat(CharStats.STAT_SAVE_MAGIC) + 50);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 100);
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources = super.myResources();
                resources.add(makeResource
                    (L("a @x1 tenticle", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
            }
        }
        return resources;
    }
}
