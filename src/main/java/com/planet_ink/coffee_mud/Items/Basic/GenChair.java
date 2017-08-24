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
package com.planet_ink.coffee_mud.Items.Basic;

import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;


public class GenChair extends GenRideable {
    public GenChair() {
        super();

        readableText = "";

        setName("a generic chair");
        basePhyStats.setWeight(150);
        setDisplayText("a generic chair is here.");
        setDescription("");
        material = RawMaterial.RESOURCE_OAK;
        baseGoldValue = 5;
        basePhyStats().setLevel(1);
        setRiderCapacity(1);
        setRideBasis(Rideable.RIDEABLE_SIT);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenChair";
    }
}
