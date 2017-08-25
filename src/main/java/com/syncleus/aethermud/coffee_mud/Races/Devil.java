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
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Devil extends Unique {
    private final static String localizedStaticName = CMLib.lang().L("Devil");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Devil");
    // 									   an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 2};
    private final String[] racialEffectNames = {"Prayer_TaintOfEvil"};
    private final int[] racialEffectLevels = {1};
    private final String[] racialEffectParms = {""};
    private final String[] culturalAbilityNames = {"Undercommon"};
    private final int[] culturalAbilityProficiencies = {25};
    private final String[] racialAbilityNames = {"WingFlying"};
    private final int[] racialAbilityLevels = {1};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {""};

    @Override
    public String ID() {
        return "Devil";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 72;
    }

    @Override
    public int shortestFemale() {
        return 70;
    }

    @Override
    public int heightVariance() {
        return 12;
    }

    @Override
    public int lightestWeight() {
        return 180;
    }

    @Override
    public int weightVariance() {
        return 50;
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("claws"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_PIERCING);
        }
        return naturalWeapon;
    }

    @Override
    public long forbiddenWornBits() {
        return 0;
    }

    @Override
    protected String[] racialEffectNames() {
        return racialEffectNames;
    }

    @Override
    protected int[] racialEffectLevels() {
        return racialEffectLevels;
    }

    @Override
    protected String[] racialEffectParms() {
        return racialEffectParms;
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_INFRARED);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_FIRE, affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_ACID, affectableStats.getStat(CharStats.STAT_SAVE_ACID) + 10);
        affectableStats.setStat(CharStats.STAT_SAVE_COLD, affectableStats.getStat(CharStats.STAT_SAVE_COLD) + 10);
    }
}
