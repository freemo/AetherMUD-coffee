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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Bat extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Bat");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Pteropine");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 0, 0, 0, 1, 2, 2, 1, 0, 1, 0, 1, 2};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"BatSpeak", "WingFlying"};
    private final int[] racialAbilityLevels = {1, 1};
    private final int[] racialAbilityProficiencies = {100, 100};
    private final boolean[] racialAbilityQuals = {false, false};
    private final String[] racialAbilityParms = {"", ""};
    private final int[] agingChart = {0, 1, 2, 4, 7, 15, 20, 21, 22};

    public Bat() {
        super();
        super.naturalAbilImmunities.add("Disease_Syphilis");
    }

    @Override
    public String ID() {
        return "Bat";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 2;
    }

    @Override
    public int shortestFemale() {
        return 2;
    }

    @Override
    public int heightVariance() {
        return 2;
    }

    @Override
    public int lightestWeight() {
        return 2;
    }

    @Override
    public int weightVariance() {
        return 0;
    }

    @Override
    public long forbiddenWornBits() {
        return ~(Wearable.WORN_NECK | Wearable.WORN_HEAD | Wearable.WORN_EARS | Wearable.WORN_EYES);
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
        if ((affectableStats.disposition() & (PhyStats.IS_SITTING | PhyStats.IS_SLEEPING)) == 0)
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_FLYING);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 3);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 13);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("some bat fangs"));
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
            case Race.AGE_CHILD:
                return name().toLowerCase() + " pup";
            default:
                return super.makeMobName(gender, age);
        }
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is fluttering around dripping blood everywhere!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is covered in bloody matted hair.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is bleeding badly from lots of wounds.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y has numerous bloody wounds and gashes.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y has some bloody wounds and gashes.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p has a few bloody wounds.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is cut and no longer flying straight.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g has some minor cuts and nicks.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g has a few nicks and scratches.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g has a few small scratches.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect health.^N", mob.name(viewer));
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("some @x1 hair", name().toLowerCase()), RawMaterial.RESOURCE_FUR));
                resources.addElement(makeResource
                    (L("a pair of @x1 wings", name().toLowerCase()), RawMaterial.RESOURCE_HIDE));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
