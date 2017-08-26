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
package com.syncleus.aethermud.game.Abilities.Paladin;

import com.syncleus.aethermud.game.Abilities.Common.EnhancedCraftingSkill;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.collections.PairVector;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Paladin_CraftHolyAvenger extends EnhancedCraftingSkill {
    private final static String localizedName = CMLib.lang().L("Craft Holy Avenger");
    private static final String[] triggerStrings = I(new String[]{"CRAFTHOLY", "CRAFTHOLYAVENGER", "CRAFTAVENGER"});

    @Override
    public String ID() {
        return "Paladin_CraftHolyAvenger";
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
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if ((buildingI == null)
                || (getRequiredFire(mob, 0) == null)) {
                messedUp = true;
                unInvoke();
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((buildingI != null) && (!aborted)) {
                    if (messedUp)
                        commonEmote(mob, L("<S-NAME> mess(es) up crafting the Holy Avenger."));
                    else
                        mob.location().addItem(buildingI, ItemPossessor.Expire.Player_Drop);
                }
                buildingI = null;
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        int completion = 16;
        final Item fire = getRequiredFire(mob, 0);
        if (fire == null)
            return false;
        final PairVector<EnhancedExpertise, Integer> enhancedTypes = enhancedTypes(mob, commands);
        buildingI = null;
        messedUp = false;
        int woodRequired = 50;
        final int[] pm = {RawMaterial.MATERIAL_METAL, RawMaterial.MATERIAL_MITHRIL};
        final int[][] data = fetchFoundResourceData(mob,
            woodRequired, "metal", pm,
            0, null, null,
            false,
            auto ? RawMaterial.RESOURCE_MITHRIL : 0,
            enhancedTypes);
        if (data == null)
            return false;
        woodRequired = data[0][FOUND_AMT];

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (!auto)
            CMLib.materials().destroyResourcesValue(mob.location(), woodRequired, data[0][FOUND_CODE], 0, null);
        buildingI = CMClass.getWeapon("GenWeapon");
        completion = 50 - CMLib.ableMapper().qualifyingClassLevel(mob, this);
        final String itemName = "the Holy Avenger";
        buildingI.setName(itemName);
        final String startStr = L("<S-NAME> start(s) crafting @x1.", buildingI.name());
        displayText = L("You are crafting @x1", buildingI.name());
        verb = L("crafting @x1", buildingI.name());
        buildingI.setDisplayText(L("@x1 lies here", itemName));
        buildingI.setDescription(itemName + ". ");
        buildingI.basePhyStats().setWeight(woodRequired);
        buildingI.setBaseValue(0);
        buildingI.setMaterial(data[0][FOUND_CODE]);
        buildingI.basePhyStats().setLevel(mob.phyStats().level());
        buildingI.basePhyStats().setAbility(5);
        final Weapon w = (Weapon) buildingI;
        w.setWeaponClassification(Weapon.CLASS_SWORD);
        w.setWeaponDamageType(Weapon.TYPE_SLASHING);
        w.setRanges(w.minRange(), 1);
        buildingI.setRawLogicalAnd(true);
        Ability A = CMClass.getAbility("Prop_HaveZapper");
        A.setMiscText("-CLASS +Paladin -ALIGNMENT +Good");
        buildingI.addNonUninvokableEffect(A);
        A = CMClass.getAbility("Prop_Doppleganger");
        A.setMiscText("120%");
        buildingI.addNonUninvokableEffect(A);

        buildingI.recoverPhyStats();
        buildingI.text();
        buildingI.recoverPhyStats();

        messedUp = !proficiencyCheck(mob, 0, auto);
        if (completion < 6)
            completion = 6;
        final CMMsg msg = CMClass.getMsg(mob, null, CMMsg.MSG_NOISYMOVEMENT, startStr);
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, completion);
            enhanceItem(mob, buildingI, enhancedTypes);
        }
        return true;
    }
}