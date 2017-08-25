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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Draconian extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Draconian");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Dragon");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] culturalAbilityNames = {"Draconic", "Butchering"};
    private final int[] culturalAbilityProficiencies = {100, 50};
    private final int[] agingChart = {0, 5, 20, 110, 325, 500, 850, 950, 1050};

    @Override
    public String ID() {
        return "Draconian";
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
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) + 5);
        affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 5);
        affectableStats.setStat(CharStats.STAT_INTELLIGENCE, affectableStats.getStat(CharStats.STAT_INTELLIGENCE) + 5);
        affectableStats.setStat(CharStats.STAT_GENDER, 'N');
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_INFRARED);
    }

    @Override
    public Weapon myNaturalWeapon() {
        return funHumanoidWeapon();
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
                    (L("a @x1 claw", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                for (int i = 0; i < 10; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 scales", name().toLowerCase()), RawMaterial.RESOURCE_SCALES));
                }
                for (int i = 0; i < 5; i++) {
                    resources.addElement(makeResource
                        (L("a pound of @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_DRAGONMEAT));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_DRAGONBLOOD));
            }
        }
        return resources;
    }
}
