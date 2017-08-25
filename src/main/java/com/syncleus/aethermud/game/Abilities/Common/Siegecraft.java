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
package com.planet_ink.game.Abilities.Common;

import com.planet_ink.game.Items.interfaces.Ammunition;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.core.CMLib;


public class Siegecraft extends Fletching {
    private final static String localizedName = CMLib.lang().L("Siegecraft");
    private static final String[] triggerStrings = I(new String[]{"SIEGECRAFT"});

    @Override
    public String ID() {
        return "Siegecraft";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public String parametersFile() {
        return "siegecraft.txt";
    }

    @Override
    protected int getNumberOfColumns() {
        return 1;
    }

    @Override
    public boolean mayICraft(final Item I) {
        if (I == null)
            return false;
        if (!super.mayBeCrafted(I))
            return false;
        if (I instanceof Ammunition)
            return true;
        if ((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
            return false;
        if (!(I instanceof Weapon))
            return false;
        if (I.ID().toUpperCase().indexOf("SIEGE") >= 0)
            return true;
        return false;
    }

}
