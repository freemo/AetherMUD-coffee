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

import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;


public class Walrus extends Seal {
    private final static String localizedStaticName = CMLib.lang().L("Walrus");
    private final int[] agingChart = {0, 2, 5, 8, 10, 15, 25, 28, 30};

    @Override
    public String ID() {
        return "Walrus";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 70;
    }

    @Override
    public int shortestFemale() {
        return 65;
    }

    @Override
    public int heightVariance() {
        return 10;
    }

    @Override
    public int lightestWeight() {
        return 800;
    }

    @Override
    public int weightVariance() {
        return 500;
    }

    @Override
    public int[] getBreathables() {
        return breatheAirWaterArray;
    }

    @Override
    public int[] getAgingChart() {
        return agingChart;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        //super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 17);
    }

    @Override
    public String arriveStr() {
        return "flops in";
    }

    @Override
    public String leaveStr() {
        return "flops";
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("a pair of tusks"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_PIERCING);
        }
        return naturalWeapon;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 18; i++) {
                    resources.addElement(makeResource
                        (L("some @x1", name().toLowerCase()), RawMaterial.RESOURCE_FISH));
                }
                for (int i = 0; i < 15; i++) {
                    resources.addElement(makeResource
                        (L("a thick @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_HIDE));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
