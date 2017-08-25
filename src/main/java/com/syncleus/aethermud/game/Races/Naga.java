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
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;
import java.util.Vector;


public class Naga extends Python {
    private final static String localizedStaticName = CMLib.lang().L("Naga");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Ophidian");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 0, 0, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] culturalAbilityNames = {"Draconic", "SnakeSpeak"};
    private final int[] culturalAbilityProficiencies = {100, 100};

    @Override
    public String ID() {
        return "Naga";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 59;
    }

    @Override
    public int shortestFemale() {
        return 59;
    }

    @Override
    public int heightVariance() {
        return 12;
    }

    @Override
    public int lightestWeight() {
        return 80;
    }

    @Override
    public int weightVariance() {
        return 80;
    }

    @Override
    public long forbiddenWornBits() {
        return Wearable.WORN_LEGS | Wearable.WORN_FEET;
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
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
        affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 5);
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("a pair of @x1 fangs", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                for (int i = 0; i < 5; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 scales", name().toLowerCase()), RawMaterial.RESOURCE_SCALES));
                }
                resources.addElement(makeResource
                    (L("a pound of @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
