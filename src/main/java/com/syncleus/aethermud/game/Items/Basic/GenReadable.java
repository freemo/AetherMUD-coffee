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
package com.planet_ink.game.Items.Basic;

import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.core.CMLib;


public class GenReadable extends GenItem {
    public GenReadable() {
        super();
        setName("a generic readable thing");
        setDisplayText("a generic readable thing sits here.");
        setDescription("");
        setMaterial(RawMaterial.RESOURCE_WOOD);
        basePhyStats().setSensesMask(PhyStats.SENSE_ITEMREADABLE);
        basePhyStats().setWeight(1);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenReadable";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public void recoverPhyStats() {
        CMLib.flags().setReadable(this, true);
        super.recoverPhyStats();
    }
}
