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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;


public class FoodPrep extends Cooking {
    private final static String localizedName = CMLib.lang().L("Food Prep");
    private static final String[] triggerStrings = I(new String[]{"FOODPREPPING", "FPREP"});

    public FoodPrep() {
        super();

        defaultFoodSound = "chopchop.wav";
    }

    @Override
    public String ID() {
        return "FoodPrep";
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
    public String cookWordShort() {
        return "make";
    }

    @Override
    public String cookWord() {
        return "making";
    }

    @Override
    public boolean honorHerbs() {
        return false;
    }

    @Override
    public boolean requireFire() {
        return false;
    }

    @Override
    public String parametersFile() {
        return "foodprep.txt";
    }

    @Override
    protected List<List<String>> loadRecipes() {
        return super.loadRecipes(parametersFile());
    }

}
