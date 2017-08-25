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
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Deva extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Deva");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Celestial");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 2};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"WingFlying"};
    private final int[] racialAbilityLevels = {1};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {""};

    @Override
    public String ID() {
        return "Deva";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
    }

    @Override
    public long forbiddenWornBits() {
        return Wearable.WORN_BACK;
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
        affectableStats.setStat(CharStats.STAT_SAVE_COLD, affectableStats.getStat(CharStats.STAT_SAVE_COLD) + 20);
        affectableStats.setStat(CharStats.STAT_SAVE_ELECTRIC, affectableStats.getStat(CharStats.STAT_SAVE_ELECTRIC) + 20);
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 20);
        affectableStats.setStat(CharStats.STAT_SAVE_GAS, affectableStats.getStat(CharStats.STAT_SAVE_GAS) + 20);
        affectableStats.setStat(CharStats.STAT_SAVE_FIRE, affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + 10);
    }

    @Override
    public int lightestWeight() {
        return 250;
    }

    @Override
    public int shortestMale() {
        return 90;
    }

    @Override
    public int shortestFemale() {
        return 90;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK | PhyStats.CAN_SEE_EVIL);
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("some @x1 feathers", name().toLowerCase()), RawMaterial.RESOURCE_FEATHERS));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource
                    (L("a pile of @x1 bones", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
            }
        }
        return resources;
    }
}
