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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Shark extends GiantFish {
    private final static String localizedStaticName = CMLib.lang().L("Shark");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "Shark";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 10;
    }

    @Override
    public int shortestFemale() {
        return 15;
    }

    @Override
    public int heightVariance() {
        return 20;
    }

    @Override
    public int lightestWeight() {
        return 355;
    }

    @Override
    public int weightVariance() {
        return 105;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 16);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 15);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + (affectableStats.level() / 3));
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 25; i++) {
                    resources.addElement(makeResource
                        (L("some @x1", name().toLowerCase()), RawMaterial.RESOURCE_FISH));
                }
                for (int i = 0; i < 15; i++) {
                    resources.addElement(makeResource
                        (L("a @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_HIDE));
                }
                for (int i = 0; i < 5; i++) {
                    resources.addElement(makeResource
                        (L("a @x1 tooth", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
