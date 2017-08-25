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

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.MagicDust;
import com.planet_ink.coffee_mud.Items.interfaces.Perfume;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Apothecary extends Cooking {
    private final static String localizedName = CMLib.lang().L("Apothecary");
    private static final String[] triggerStrings = I(new String[]{"APOTHECARY", "MIX"});

    public Apothecary() {
        super();

        defaultFoodSound = "hotspring.wav";
        defaultDrinkSound = "hotspring.wav";
    }

    @Override
    public String ID() {
        return "Apothecary";
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
        return "MISC";
    }

    @Override
    public String cookWordShort() {
        return "mix";
    }

    @Override
    public String cookWord() {
        return "mixing";
    }

    @Override
    public boolean honorHerbs() {
        return false;
    }

    @Override
    protected ExpertiseLibrary.SkillCostDefinition getRawTrainingCost() {
        return CMProps.getNormalSkillGainCost(ID());
    }

    @Override
    public String parametersFile() {
        return "poisons.txt";
    }

    @Override
    protected List<List<String>> loadRecipes() {
        return super.loadRecipes(parametersFile());
    }

    @Override
    public boolean supportsDeconstruction() {
        return false;
    }

    @Override
    public boolean mayICraft(final Item I) {
        if (I == null)
            return false;
        if (!super.mayBeCrafted(I))
            return false;
        if (I instanceof Perfume) {
            return true;
        } else if (I instanceof Drink) {
            final Drink D = (Drink) I;
            if (D.liquidType() != RawMaterial.RESOURCE_POISON)
                return false;
            if (CMLib.flags().flaggedAffects(D, Ability.FLAG_INTOXICATING).size() > 0)
                return false;
            if (CMLib.flags().domainAffects(D, Ability.ACODE_POISON).size() > 0)
                return true;
            return true;
        } else if (I instanceof MagicDust) {
            final MagicDust M = (MagicDust) I;
            final List<Ability> spells = M.getSpells();
            if ((spells == null) || (spells.size() == 0))
                return false;
            return true;
        } else
            return false;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((!super.invoke(mob, commands, givenTarget, auto, asLevel)) || (buildingI == null))
            return false;
        final Ability A2 = buildingI.fetchEffect(0);
        if ((A2 != null)
            && (buildingI instanceof Drink)) {
            ((Drink) buildingI).setLiquidType(RawMaterial.RESOURCE_POISON);
            buildingI.setMaterial(RawMaterial.RESOURCE_POISON);
        }
        return true;
    }
}
