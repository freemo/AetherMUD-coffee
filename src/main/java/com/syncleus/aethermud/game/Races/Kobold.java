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

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Kobold extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Kobold");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Reptile");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] culturalAbilityNames = {"Draconic"};
    private final int[] culturalAbilityProficiencies = {75};
    private final int[] agingChart = {0, 1, 2, 12, 20, 30, 45, 47, 49};

    public Kobold() {
        super();
        super.naturalAbilImmunities.add("Disease_Syphilis");
    }

    @Override
    public String ID() {
        return "Kobold";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 45;
    }

    @Override
    public int shortestFemale() {
        return 40;
    }

    @Override
    public int heightVariance() {
        return 6;
    }

    @Override
    public int lightestWeight() {
        return 50;
    }

    @Override
    public int weightVariance() {
        return 50;
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
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_INFRARED);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 6);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 13);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 10);
    }

    @Override
    public String arriveStr() {
        return "creeps in";
    }

    @Override
    public String leaveStr() {
        return "creeps";
    }

    @Override
    public Weapon myNaturalWeapon() {
        return funHumanoidWeapon();
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is near a pitiful death!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is covered in blood.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is bleeding badly from lots of wounds.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y has numerous bloody wounds and gashes.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y has some bloody wounds and gashes.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p has a few bloody wounds.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is cut and bruised heavily.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g has some minor cuts and bruises.^N", mob.name(viewer));
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
                    (L("some nobby @x1 horns", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                for (int i = 0; i < 2; i++) {
                    resources.addElement(makeResource
                        (L("some @x1 scales", name().toLowerCase()), RawMaterial.RESOURCE_SCALES));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource
                    (L("a pile of @x1 bones", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
            }
        }
        final Vector<RawMaterial> rsc = new XVector<RawMaterial>(resources);
        final Item meat = makeResource
            (L("some @x1 flesh", name().toLowerCase()), RawMaterial.RESOURCE_MEAT);
        if ((CMLib.dice().rollPercentage() < 5) && (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
            final Ability A = CMClass.getAbility("Disease_Lepresy");
            if ((A != null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                meat.addNonUninvokableEffect(A);
        }
        return rsc;
    }
}
