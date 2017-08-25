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

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MasterFoodPrep extends Baking {
    private static final String[] triggerStrings = I(new String[]{"MFOODPREPPING", "MFPREP", "MASTERFOODPREPPING", "MASTERFPREP"});
    protected List<String> noUninvokes = new ArrayList<String>(0);
    private String cookingID = "";

    @Override
    public String ID() {
        return "MasterFoodPrep" + cookingID;
    }

    @Override
    public String name() {
        return L("Master Food Prep" + cookingID);
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected List<String> getUninvokeException() {
        return noUninvokes;
    }

    @Override
    protected int getDuration(MOB mob, int level) {
        return getDuration(60, mob, 1, 8);
    }

    @Override
    protected int baseYield() {
        return 2;
    }

    @Override

    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        try {
            cookingID = "";
            int num = 1;
            while (mob.fetchEffect("MasterFoodPrep" + cookingID) != null)
                cookingID = Integer.toString(++num);
            final List<String> noUninvokes = new Vector<String>(1);
            for (int i = 0; i < mob.numEffects(); i++) {
                final Ability A = mob.fetchEffect(i);
                if (((A instanceof MasterFoodPrep) || A.ID().equals("FoodPrep"))
                    && (noUninvokes.size() < 5))
                    noUninvokes.add(A.ID());
            }
            this.noUninvokes = noUninvokes;
            return super.invoke(mob, commands, givenTarget, auto, asLevel);
        } finally {
            cookingID = "";
        }
    }
}
