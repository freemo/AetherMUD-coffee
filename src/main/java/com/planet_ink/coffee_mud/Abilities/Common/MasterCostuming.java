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

import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class MasterCostuming extends Costuming {
    private final static String localizedName = CMLib.lang().L("Master Costuming");
    private static final String[] triggerStrings = I(new String[]{"MASTERCOSTUME", "MCOSTUME", "MCOSTUMING", "MASTERCOSTUMING"});

    @Override
    public String ID() {
        return "MasterCostuming";
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
        return "mastercostume.txt";
    }

    @Override
    protected boolean masterCraftCheck(final Item I) {
        if (I.name().toUpperCase().startsWith("DESIGNER") || (I.name().toUpperCase().indexOf(" DESIGNER ") > 0))
            return true;
        if (I.basePhyStats().level() < 31)
            return false;
        return true;
    }

    @Override
    protected boolean autoGenInvoke(final MOB mob, List<String> commands, Physical givenTarget, final boolean auto,
                                    final int asLevel, int autoGenerate, boolean forceLevels, List<Item> crafted) {
        if (super.checkStop(mob, commands))
            return true;

        if (super.checkInfo(mob, commands))
            return true;

        randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands, autoGenerate);
        if (commands.size() == 0) {
            commonTell(mob, L("Make what? Enter \"mcostume list\" for a list, \"mcostume info <item>\", \"mcostume scan\", \"mcostume refit\","
                + " \"mcostume learn <item>\", \"mcostume mend <item>\", or \"mcostume stop\" to cancel."));
            return false;
        }
        return super.autoGenInvoke(mob, commands, givenTarget, auto, asLevel, autoGenerate, forceLevels, crafted);
    }
}

