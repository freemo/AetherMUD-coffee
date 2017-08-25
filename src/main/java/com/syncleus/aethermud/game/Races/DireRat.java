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
import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;
import java.util.Vector;

public class DireRat extends GiantRat {
    private final static String localizedStaticName = CMLib.lang().L("Dire Rat");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Rodent");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "DireRat";
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
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 5; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_FUR));
                }
                for (int i = 0; i < 2; i++) {
                    resources.addElement(makeResource
                        (L("a pound of @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                }
                resources.addElement(makeResource
                    (L("a pair of @x1 teeth", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
