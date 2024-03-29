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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;

import java.util.List;
import java.util.Vector;


public class Wyvern extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Wyvern");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Reptile");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1, 1, 1, 2};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] culturalAbilityNames = {"Draconic"};
    private final int[] culturalAbilityProficiencies = {50};
    private final String[] racialAbilityNames = {"WingFlying", "Poison_Sting"};
    private final int[] racialAbilityLevels = {1, 1};
    private final int[] racialAbilityProficiencies = {100, 100};
    private final boolean[] racialAbilityQuals = {false, false};
    private final String[] racialAbilityParms = {"", ""};
    private final int[] agingChart = {0, 5, 20, 110, 325, 500, 850, 950, 1050};

    @Override
    public String ID() {
        return "Wyvern";
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
        return 58;
    }

    @Override
    public int heightVariance() {
        return 40;
    }

    @Override
    public int lightestWeight() {
        return 1500;
    }

    @Override
    public int weightVariance() {
        return 500;
    }

    @Override
    public long forbiddenWornBits() {
        return Wearable.WORN_HELD | Wearable.WORN_WIELD | Wearable.WORN_WAIST;
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
    }

    @Override
    protected String[] culturalAbilityNames() {
        return culturalAbilityNames;
    }

    @Override
    protected int[] culturalAbilityProficiencies() {
        return culturalAbilityProficiencies;
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
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 18);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 13);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 10);
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("huge talons"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_PIERCING);
        }
        return naturalWeapon;
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
                return name().toLowerCase() + " hatchling";
            case Race.AGE_CHILD:
            default:
                return super.makeMobName(gender, age);
        }
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is raging in bloody pain!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is covered in blood.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is bleeding badly from lots of wounds.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y has some bloody wounds and gashed scales.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p has a few bloody wounds.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is cut and bruised heavily.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g has a few bruises and scratched scales.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g has a few small bruises.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect health.^N", mob.name(viewer));
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("a @x1 tail", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                for (int i = 0; i < 50; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 scales", name().toLowerCase()), RawMaterial.RESOURCE_SCALES));
                }
                for (int i = 0; i < 20; i++) {
                    resources.addElement(makeResource
                        (L("a pound of @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
