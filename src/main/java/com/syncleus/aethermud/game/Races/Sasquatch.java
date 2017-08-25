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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;
import java.util.Vector;


public class Sasquatch extends Gorilla {
    private final static String localizedStaticName = CMLib.lang().L("Sasquatch");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Primate");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] culturalAbilityNames = {"Gigantic"};
    private final int[] culturalAbilityProficiencies = {100};

    @Override
    public String ID() {
        return "Sasquatch";
    }

    @Override
    public String name() {
        return localizedStaticName;
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
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) + 5);
        affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 5);
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 4; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_FUR));
                }
                resources.addElement(makeResource
                    (L("a @x1 spleen", name().toLowerCase()), RawMaterial.RESOURCE_HIDE));
                resources.addElement(makeResource
                    (L("a pound of @x1 flesh", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource
                    (L("a pile of @x1 bones", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
            }
        }
        return resources;
    }
}
