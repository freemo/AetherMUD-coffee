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
package com.planet_ink.coffee_mud.Abilities.Common;

import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class CageBuilding extends Wainwrighting {
    private final static String localizedName = CMLib.lang().L("Cage Building");
    private static final String[] triggerStrings = I(new String[]{"BUILDCAGE", "CAGEBUILDING"});

    @Override
    public String ID() {
        return "CageBuilding";
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
    public String supportedResourceString() {
        return "WOODEN";
    }

    @Override
    public String parametersFile() {
        return "cagebuilding.txt";
    }

    @Override
    protected List<List<String>> loadRecipes() {
        return super.loadRecipes(parametersFile());
    }

    @Override
    public boolean mayICraft(final Item I) {
        if (I == null)
            return false;
        if (!super.mayBeCrafted(I))
            return false;
        if ((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
            return false;
        if (CMLib.flags().isDeadlyOrMaliciousEffect(I))
            return false;
        if (!(I instanceof Container))
            return false;
        final Container C = (Container) I;
        if ((C.containTypes() == Container.CONTAIN_CAGED)
            || (C.containTypes() == (Container.CONTAIN_BODIES | Container.CONTAIN_CAGED)))
            return true;
        if (isANativeItem(I.Name()))
            return true;
        return false;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        if (commands.size() == 0) {
            commonTell(mob, L("Build what? Enter \"buildcage list\" for a list, \"buildcage learn <item>\" to gain recipes, or \"buildcage stop\" to cancel."));
            return false;
        }
        return super.invoke(mob, commands, givenTarget, auto, asLevel);
    }
}
