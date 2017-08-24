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

import com.planet_ink.coffee_mud.Abilities.interfaces.ItemCraftor;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.AchievementLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Cobbling extends EnhancedCraftingSkill implements ItemCraftor, MendingSkill {
    //protected static final int RCP_FINALNAME=0;
    //protected static final int RCP_LEVEL=1;
    //protected static final int RCP_TICKS=2;
    protected static final int RCP_WOOD = 3;
    protected static final int RCP_VALUE = 4;
    protected static final int RCP_CLASSTYPE = 5;
    protected static final int RCP_MISCTYPE = 6;
    protected static final int RCP_CAPACITY = 7;
    protected static final int RCP_ARMORDMG = 8;
    protected static final int RCP_CONTAINMASK = 9;
    protected static final int RCP_SPELL = 10;
    private final static String localizedName = CMLib.lang().L("Cobbling");
    private static final String[] triggerStrings = I(new String[]{"COBBLE", "COBBLING"});

    @Override
    public String ID() {
        return "Cobbling";
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
        return "WOODEN|METAL";
    }

    @Override
    public String parametersFormat() {
        return
            "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\tITEM_BASE_VALUE\t"
                + "ITEM_CLASS_ID\tCODED_WEAR_LOCATION\tCONTAINER_CAPACITY\tBASE_ARMOR_AMOUNT\tCONTAINER_TYPE\tCODED_SPELL_LIST";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            if (buildingI == null) {
                messedUp = true;
                unInvoke();
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public String parametersFile() {
        return "cobbler.txt";
    }

    @Override
    protected List<List<String>> loadRecipes() {
        return super.loadRecipes(parametersFile());
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((buildingI != null) && (!aborted)) {
                    if (messedUp) {
                        if (activity == CraftingActivity.MENDING)
                            messedUpCrafting(mob);
                        else if (activity == CraftingActivity.LEARNING) {
                            commonEmote(mob, L("<S-NAME> fail(s) to learn how to make @x1.", buildingI.name()));
                            buildingI.destroy();
                        } else if (activity == CraftingActivity.REFITTING)
                            commonEmote(mob, L("<S-NAME> mess(es) up refitting @x1.", buildingI.name()));
                        else
                            commonEmote(mob, L("<S-NAME> mess(es) up cobbling @x1.", buildingI.name()));
                    } else {
                        if (activity == CraftingActivity.MENDING) {
                            buildingI.setUsesRemaining(100);
                            CMLib.achievements().possiblyBumpAchievement(mob, AchievementLibrary.Event.MENDER, 1, this);
                        } else if (activity == CraftingActivity.LEARNING) {
                            deconstructRecipeInto(buildingI, recipeHolder);
                            buildingI.destroy();
                        } else if (activity == CraftingActivity.REFITTING) {
                            buildingI.basePhyStats().setHeight(0);
                            buildingI.recoverPhyStats();
                        } else {
                            dropAWinner(mob, buildingI);
                            CMLib.achievements().possiblyBumpAchievement(mob, AchievementLibrary.Event.CRAFTING, 1, this);
                        }
                    }
                }
                buildingI = null;
                activity = CraftingActivity.CRAFTING;
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean mayICraft(final Item I) {
        if (I == null)
            return false;
        if (!super.mayBeCrafted(I))
            return false;
        if (CMLib.flags().isDeadlyOrMaliciousEffect(I))
            return false;
        if (!(I instanceof Armor))
            return false;
        if (!I.fitsOn(Wearable.WORN_FEET))
            return false;
        return true;
    }

    @Override
    public boolean supportsMending(Physical item) {
        return canMend(null, item, true);
    }

    @Override
    protected boolean canMend(MOB mob, Environmental E, boolean quiet) {
        if (!super.canMend(mob, E, quiet))
            return false;
        if ((!(E instanceof Item))
            || (!mayICraft((Item) E))) {
            if (!quiet)
                commonTell(mob, L("That's not a cobbled item."));
            return false;
        }
        return true;
    }

    @Override
    public String getDecodedComponentsDescription(final MOB mob, final List<String> recipe) {
        return super.getComponentDescription(mob, recipe, RCP_WOOD);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        return autoGenInvoke(mob, commands, givenTarget, auto, asLevel, 0, false, new Vector<Item>(0));
    }

    @Override
    protected boolean autoGenInvoke(final MOB mob, List<String> commands, Physical givenTarget, final boolean auto,
                                    final int asLevel, int autoGenerate, boolean forceLevels, List<Item> crafted) {
        if (super.checkStop(mob, commands))
            return true;

        if (super.checkInfo(mob, commands))
            return true;

        final PairVector<EnhancedExpertise, Integer> enhancedTypes = enhancedTypes(mob, commands);
        randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands, autoGenerate);
        if (commands.size() == 0) {
            commonTell(mob, L("Make what? Enter \"cobble list\" for a list, \"cobble info <item>\", \"cobble refit <item>\" to resize, "
                + "\"cobble learn <item>\", \"cobble scan\", or \"cobble mend <item>\", \"cobble stop\" to cancel."));
            return false;
        }
        if ((!auto)
            && (commands.size() > 0)
            && ((commands.get(0)).equalsIgnoreCase("bundle"))) {
            bundling = true;
            if (super.invoke(mob, commands, givenTarget, auto, asLevel))
                return super.bundle(mob, commands);
            return false;
        }
        final List<List<String>> recipes = addRecipes(mob, loadRecipes());
        final String str = commands.get(0);
        String startStr = null;
        bundling = false;
        int duration = 4;
        final int[] cols = {
            CMLib.lister().fixColWidth(29, mob.session()),
            CMLib.lister().fixColWidth(3, mob.session()),
            CMLib.lister().fixColWidth(3, mob.session()),
        };
        if (str.equalsIgnoreCase("list")) {
            String mask = CMParms.combine(commands, 1);
            boolean allFlag = false;
            if (mask.equalsIgnoreCase("all")) {
                allFlag = true;
                mask = "";
            }
            final StringBuffer buf = new StringBuffer("");
            int toggler = 1;
            final int toggleTop = 2;
            for (int r = 0; r < toggleTop; r++)
                buf.append((r > 0 ? " " : "") + CMStrings.padRight(L("Item"), cols[0]) + " " + CMStrings.padRight(L("Lvl"), cols[1]) + " " + CMStrings.padRight(L("Amt"), cols[2]));
            buf.append("\n\r");
            for (int r = 0; r < recipes.size(); r++) {
                final List<String> V = recipes.get(r);
                if (V.size() > 0) {
                    final String item = replacePercent(V.get(RCP_FINALNAME), "");
                    final int level = CMath.s_int(V.get(RCP_LEVEL));
                    final String wood = getComponentDescription(mob, V, RCP_WOOD);
                    if (wood.length() > 5) {
                        if (toggler > 1)
                            buf.append("\n\r");
                        toggler = toggleTop;
                    }
                    if (((level <= xlevel(mob)) || allFlag)
                        && ((mask.length() == 0) || mask.equalsIgnoreCase("all") || CMLib.english().containsString(item, mask))) {
                        buf.append(CMStrings.padRight(item, cols[0]) + " " + CMStrings.padRight("" + level, cols[1]) + " " + CMStrings.padRightPreserve("" + wood, cols[2]) + ((toggler != toggleTop) ? " " : "\n\r"));
                        if (++toggler > toggleTop)
                            toggler = 1;
                    }
                }
            }
            if (toggler != 1)
                buf.append("\n\r");
            commonTell(mob, buf.toString());
            enhanceList(mob);
            return true;
        } else if (((commands.get(0))).equalsIgnoreCase("learn")) {
            return doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
        } else if (str.equalsIgnoreCase("scan"))
            return publicScan(mob, commands);
        else if (str.equalsIgnoreCase("mend")) {
            buildingI = null;
            activity = CraftingActivity.CRAFTING;
            messedUp = false;
            final Vector<String> newCommands = CMParms.parse(CMParms.combine(commands, 1));
            buildingI = getTarget(mob, mob.location(), givenTarget, newCommands, Wearable.FILTER_UNWORNONLY);
            if (!canMend(mob, buildingI, false))
                return false;
            activity = CraftingActivity.MENDING;
            if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
                return false;
            startStr = L("<S-NAME> start(s) mending @x1.", buildingI.name());
            displayText = L("You are mending @x1", buildingI.name());
            verb = L("mending @x1", buildingI.name());
        } else if (str.equalsIgnoreCase("refit")) {
            buildingI = null;
            activity = CraftingActivity.CRAFTING;
            messedUp = false;
            final Vector<String> newCommands = CMParms.parse(CMParms.combine(commands, 1));
            buildingI = getTarget(mob, mob.location(), givenTarget, newCommands, Wearable.FILTER_UNWORNONLY);
            if (buildingI == null)
                return false;
            if (!buildingI.fitsOn(Wearable.WORN_FEET)) {
                commonTell(mob, L("That's not footwear.  That can't be refitted."));
                return false;
            }
            if (!(buildingI instanceof Armor)) {
                commonTell(mob, L("You don't know how to refit that sort of thing."));
                return false;
            }
            if (buildingI.phyStats().height() == 0) {
                commonTell(mob, L("@x1 is already the right size.", buildingI.name(mob)));
                return false;
            }
            activity = CraftingActivity.REFITTING;
            if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
                return false;
            startStr = L("<S-NAME> start(s) refitting @x1.", buildingI.name());
            displayText = L("You are refitting @x1", buildingI.name());
            verb = L("refitting @x1", buildingI.name());
        } else {
            buildingI = null;
            activity = CraftingActivity.CRAFTING;
            messedUp = false;
            aborted = false;
            int amount = -1;
            if ((commands.size() > 1) && (CMath.isNumber(commands.get(commands.size() - 1)))) {
                amount = CMath.s_int(commands.get(commands.size() - 1));
                commands.remove(commands.size() - 1);
            }
            final String recipeName = CMParms.combine(commands, 0);
            List<String> foundRecipe = null;
            final List<List<String>> matches = matchingRecipeNames(recipes, recipeName, true);
            for (int r = 0; r < matches.size(); r++) {
                final List<String> V = matches.get(r);
                if (V.size() > 0) {
                    final int level = CMath.s_int(V.get(RCP_LEVEL));
                    if ((autoGenerate > 0) || (level <= xlevel(mob))) {
                        foundRecipe = V;
                        break;
                    }
                }
            }
            if (foundRecipe == null) {
                commonTell(mob, L("You don't know how to make a '@x1'.  Try \"cobble list\" for a list.", recipeName));
                return false;
            }

            final String woodRequiredStr = foundRecipe.get(RCP_WOOD);
            final int[] compData = new int[CF_TOTAL];
            final List<Object> componentsFoundList = getAbilityComponents(mob, woodRequiredStr, "make " + CMLib.english().startWithAorAn(recipeName), autoGenerate, compData);
            if (componentsFoundList == null)
                return false;
            int woodRequired = CMath.s_int(woodRequiredStr);
            woodRequired = adjustWoodRequired(woodRequired, mob);

            if (amount > woodRequired)
                woodRequired = amount;
            final String misctype = foundRecipe.get(RCP_MISCTYPE);
            final int[] pm = {RawMaterial.MATERIAL_METAL, RawMaterial.MATERIAL_MITHRIL, RawMaterial.MATERIAL_CLOTH, RawMaterial.MATERIAL_WOODEN, RawMaterial.MATERIAL_LEATHER};
            bundling = misctype.equalsIgnoreCase("BUNDLE");
            final int[][] data = fetchFoundResourceData(mob,
                woodRequired, "metal", pm,
                0, null, null,
                bundling,
                autoGenerate,
                enhancedTypes);
            if (data == null)
                return false;
            fixDataForComponents(data, componentsFoundList);
            woodRequired = data[0][FOUND_AMT];

            if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
                return false;
            final int lostValue = autoGenerate > 0 ? 0 :
                CMLib.materials().destroyResourcesValue(mob.location(), woodRequired, data[0][FOUND_CODE], 0, null)
                    + CMLib.ableComponents().destroyAbilityComponents(componentsFoundList);
            buildingI = CMClass.getItem(foundRecipe.get(RCP_CLASSTYPE));
            if (buildingI == null) {
                commonTell(mob, L("There's no such thing as a @x1!!!", foundRecipe.get(RCP_CLASSTYPE)));
                return false;
            }
            duration = getDuration(CMath.s_int(foundRecipe.get(RCP_TICKS)), mob, CMath.s_int(foundRecipe.get(RCP_LEVEL)), 4);
            buildingI.setMaterial(getBuildingMaterial(woodRequired, data, compData));
            String itemName = replacePercent(foundRecipe.get(RCP_FINALNAME), RawMaterial.CODES.NAME(buildingI.material())).toLowerCase();
            if (itemName.endsWith("s"))
                itemName = "some " + itemName;
            else
                itemName = CMLib.english().startWithAorAn(itemName);
            buildingI.setName(itemName);
            startStr = L("<S-NAME> start(s) cobbling @x1.", buildingI.name());
            displayText = L("You are cobbling @x1", buildingI.name());
            verb = L("cobbling @x1", buildingI.name());
            playSound = "sanding.wav";
            buildingI.setDisplayText(L("@x1 lies here", itemName));
            buildingI.setDescription(itemName + ". ");
            buildingI.basePhyStats().setWeight(getStandardWeight(woodRequired + compData[CF_AMOUNT], bundling));
            buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE)));
            final int hardness = RawMaterial.CODES.HARDNESS(buildingI.material()) - 6;
            buildingI.basePhyStats().setLevel(CMath.s_int(foundRecipe.get(RCP_LEVEL)) + (hardness * 3));
            if (buildingI.basePhyStats().level() < 1)
                buildingI.basePhyStats().setLevel(1);
            final int capacity = CMath.s_int(foundRecipe.get(RCP_CAPACITY));
            final long canContain = getContainerType(foundRecipe.get(RCP_CONTAINMASK));
            final int armordmg = CMath.s_int(foundRecipe.get(RCP_ARMORDMG));
            setBrand(mob, buildingI);
            final String spell = (foundRecipe.size() > RCP_SPELL) ? foundRecipe.get(RCP_SPELL).trim() : "";
            if (bundling)
                buildingI.setBaseValue(lostValue);
            addSpells(buildingI, spell);
            if ((buildingI instanceof Armor) && (!(buildingI instanceof FalseLimb))) {
                ((Armor) buildingI).basePhyStats().setArmor(0);
                if (armordmg != 0)
                    ((Armor) buildingI).basePhyStats().setArmor(armordmg + (baseYield() + abilityCode() - 1));
                setWearLocation(buildingI, misctype, hardness);
            }
            if (buildingI instanceof Container) {
                if (capacity > 0) {
                    ((Container) buildingI).setCapacity(capacity + woodRequired);
                    ((Container) buildingI).setContainTypes(canContain);
                }
            }
            buildingI.recoverPhyStats();
            buildingI.text();
            buildingI.recoverPhyStats();
        }

        messedUp = !proficiencyCheck(mob, 0, auto);

        if (bundling) {
            messedUp = false;
            duration = 1;
            verb = L("bundling @x1", RawMaterial.CODES.NAME(buildingI.material()).toLowerCase());
            startStr = L("<S-NAME> start(s) @x1.", verb);
            displayText = L("You are @x1", verb);
        }

        if (autoGenerate > 0) {
            crafted.add(buildingI);
            return true;
        }

        final CMMsg msg = CMClass.getMsg(mob, buildingI, this, getActivityMessageType(), startStr);
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            buildingI = (Item) msg.target();
            beneficialAffect(mob, mob, asLevel, duration);
            enhanceItem(mob, buildingI, enhancedTypes);
        } else if (bundling) {
            messedUp = false;
            aborted = false;
            unInvoke();
        }
        return true;
    }
}
