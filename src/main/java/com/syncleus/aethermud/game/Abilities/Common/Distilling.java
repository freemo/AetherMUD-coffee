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
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Drink;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Distilling extends Cooking {
    private final static String localizedName = CMLib.lang().L("Distilling");
    private static final String[] triggerStrings = I(new String[]{"DISTILLING"});

    public Distilling() {
        super();

        defaultFoodSound = "hotspring.wav";
        defaultDrinkSound = "hotspring.wav";
    }

    @Override
    public String ID() {
        return "Distilling";
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
        return "distill";
    }

    @Override
    public String cookWord() {
        return "distilling";
    }

    @Override
    public boolean honorHerbs() {
        return false;
    }

    @Override
    public String supportedResourceString() {
        return "MISC";
    }

    @Override
    public String parametersFile() {
        return "liquors.txt";
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
        if (I instanceof Drink) {
            final Drink D = (Drink) I;
            if (D.liquidType() != RawMaterial.RESOURCE_LIQUOR)
                return false;
            if (CMLib.flags().flaggedAffects(D, Ability.FLAG_INTOXICATING).size() > 0)
                return true;
        }
        return false;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((!super.invoke(mob, commands, givenTarget, auto, asLevel)) || (buildingI == null))
            return false;
        final Ability A2 = buildingI.fetchEffect(0);
        if ((A2 != null)
            && (buildingI instanceof Drink)) {
            ((Drink) buildingI).setLiquidType(RawMaterial.RESOURCE_LIQUOR);
            buildingI.setMaterial(RawMaterial.RESOURCE_LIQUOR);
        }
        return true;
    }
}
