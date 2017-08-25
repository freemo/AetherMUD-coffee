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

import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;
import java.util.Vector;


public class GiantAmphibian extends GreatAmphibian {
    private final static String localizedStaticName = CMLib.lang().L("Giant Amphibian");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Amphibian");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "GiantAmphibian";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 50;
    }

    @Override
    public int shortestFemale() {
        return 55;
    }

    @Override
    public int heightVariance() {
        return 20;
    }

    @Override
    public int lightestWeight() {
        return 1955;
    }

    @Override
    public int weightVariance() {
        return 405;
    }

    @Override
    public long forbiddenWornBits() {
        return ~(Wearable.WORN_EYES);
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 25; i++)
                    resources.addElement(makeResource
                        (L("some @x1", name().toLowerCase()), RawMaterial.RESOURCE_FISH));
                for (int i = 0; i < 15; i++)
                    resources.addElement(makeResource
                        (L("a @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_HIDE));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
