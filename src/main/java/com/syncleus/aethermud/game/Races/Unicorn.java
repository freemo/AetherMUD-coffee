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


public class Unicorn extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Unicorn");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Equine");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"HorseSpeak"};
    private final int[] racialAbilityLevels = {1};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {""};
    private final int[] agingChart = {0, 2, 4, 8, 18, 36, 48, 56, 64};

    @Override
    public String ID() {
        return "Unicorn";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 60;
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
        return 350;
    }

    @Override
    public int weightVariance() {
        return 100;
    }

    @Override
    public long forbiddenWornBits() {
        return ~(Wearable.WORN_HEAD | Wearable.WORN_EARS | Wearable.WORN_EYES);
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
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 18);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 6);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("a Unicorn Horn"));
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
                return name().toLowerCase() + " foal";
            case Race.AGE_CHILD:
            case Race.AGE_YOUNGADULT:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return name().toLowerCase() + " colt";
                    case 'F':
                    case 'f':
                        return name().toLowerCase() + " filly";
                    default:
                        return "young " + name().toLowerCase();
                }
            case Race.AGE_MATURE:
            case Race.AGE_MIDDLEAGED:
            default:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return name().toLowerCase() + " stud";
                    case 'F':
                    case 'f':
                        return name().toLowerCase() + " stallion";
                    default:
                        return name().toLowerCase();
                }
            case Race.AGE_OLD:
            case Race.AGE_VENERABLE:
            case Race.AGE_ANCIENT:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "old male " + name().toLowerCase();
                    case 'F':
                    case 'f':
                        return "old female " + name().toLowerCase();
                    default:
                        return "old " + name().toLowerCase();
                }
        }
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is hovering on deaths door!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is covered in blood and matted hair.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is bleeding badly from lots of wounds.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y has large patches of bloody matted fur.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y has some bloody matted fur.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p has a lot of cuts and gashes.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p has a few cut patches.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g has a cut patch of fur.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g has some disheveled fur.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g has some misplaced hairs.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect health.^N", mob.name(viewer));
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource("a " + name().toLowerCase() + " mane", RawMaterial.RESOURCE_FUR));
                for (int i = 0; i < 2; i++)
                    resources.addElement(makeResource("a piece of " + name().toLowerCase() + " leather", RawMaterial.RESOURCE_LEATHER));
                resources.addElement(makeResource("a pound of " + name().toLowerCase() + " meat", RawMaterial.RESOURCE_BEEF));
                resources.addElement(makeResource("a pint of " + name().toLowerCase() + " blood", RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource("a " + name().toLowerCase() + " horn", RawMaterial.RESOURCE_BONE));
            }
        }
        return resources;
    }
}
