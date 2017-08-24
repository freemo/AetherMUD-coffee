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

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class GreatCat extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Great Cat");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Feline");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"BigCatSpeak"};
    private final int[] racialAbilityLevels = {1};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {""};
    private final int[] agingChart = {0, 1, 2, 4, 7, 15, 20, 21, 22};

    @Override
    public String ID() {
        return "GreatCat";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 18;
    }

    @Override
    public int shortestFemale() {
        return 18;
    }

    @Override
    public int heightVariance() {
        return 10;
    }

    @Override
    public int lightestWeight() {
        return 100;
    }

    @Override
    public int weightVariance() {
        return 60;
    }

    @Override
    public long forbiddenWornBits() {
        return ~(Wearable.WORN_HEAD | Wearable.WORN_FEET | Wearable.WORN_NECK | Wearable.WORN_EARS | Wearable.WORN_EYES);
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
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
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 16);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 16);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
    }

    @Override
    public String arriveStr() {
        return "struts in";
    }

    @Override
    public String leaveStr() {
        return "struts";
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
                return "baby " + name().toLowerCase() + " kitten";
            case Race.AGE_CHILD:
                return "young " + name().toLowerCase() + " kitten";
            default:
                return super.makeMobName(gender, age);
        }
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("huge sharp claws"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_PIERCING);
        }
        return naturalWeapon;
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is down to one life!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is covered in blood and matted hair.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is bleeding badly from lots of wounds.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y has large patches of bloody matted hair.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y has some bloody matted hair.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p has a lot of cuts and gashes.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p has a few cut patches.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g has a cut patch of fur.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g has some disheveled hair.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g has some misplaced hairs.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect health.^N", mob.name(viewer));
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("some @x1 claws", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                for (int i = 0; i < 4; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_HIDE));
                }
                resources.addElement(makeResource
                    (L("a pound of @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource
                    (L("a pile of @x1 bones", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
            }
        }
        return resources;
    }
}
