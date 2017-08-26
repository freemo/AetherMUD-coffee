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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;

import java.util.List;
import java.util.Vector;


public class UmberHulk extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Umber Hulk");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Giant-kin");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"Chant_Stonewalking", "Spell_Confusion"};
    private final int[] racialAbilityLevels = {1, 10};
    private final int[] racialAbilityProficiencies = {100, 50};
    private final boolean[] racialAbilityQuals = {false, false};
    private final String[] racialAbilityParms = {"", ""};

    @Override
    public String ID() {
        return "UmberHulk";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 96;
    }

    @Override
    public int shortestFemale() {
        return 90;
    }

    @Override
    public int heightVariance() {
        return 24;
    }

    @Override
    public int lightestWeight() {
        return 800;
    }

    @Override
    public int weightVariance() {
        return 200;
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
    public int availabilityCode() {
        return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
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
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 16);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 8);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 9);
    }

    @Override
    public String arriveStr() {
        return "thunders in";
    }

    @Override
    public String leaveStr() {
        return "leaves";
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("huge clawed hands"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_SLASHING);
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
                return "young " + name().toLowerCase();
            default:
                return super.makeMobName('N', age);
        }
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is near to heartless death!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is covered in torn slabs of flesh.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is gored badly with lots of tears.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y has numerous gory tears and gashes.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y has some gory tears and gashes.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p has a few gory wounds.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is cut and bruised heavily.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g has some minor cuts and bruises.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g has a few bruises and scratches.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g has a few small bruises.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect health.^N", mob.name(viewer));
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 4; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_LEATHER));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource
                    (L("a pile of @x1 bones", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
            }
        }
        return resources;
    }
}