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

import com.syncleus.aethermud.game.Abilities.interfaces.ItemCraftor;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.*;
import com.syncleus.aethermud.game.Libraries.interfaces.AchievementLibrary;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.interfaces.*;

import java.util.List;
import java.util.Vector;


public class GlassBlowing extends CraftingSkill implements ItemCraftor {
    //protected static final int RCP_FINALNAME=0;
    //protected static final int RCP_LEVEL=1;
    //protected static final int RCP_TICKS=2;
    protected static final int RCP_WOOD = 3;
    protected static final int RCP_VALUE = 4;
    protected static final int RCP_CLASSTYPE = 5;
    protected static final int RCP_MISCTYPE = 6;
    protected static final int RCP_CAPACITY = 7;
    protected static final int RCP_SPELL = 8;
    private final static String localizedName = CMLib.lang().L("Glass Blowing");
    private static final String[] triggerStrings = I(new String[]{"GLASSBLOW", "GLASSBLOWING"});

    @Override
    public String ID() {
        return "GlassBlowing";
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
        return "_GLASS|SAND";
    }

    @Override
    public String parametersFormat() {
        return
            "ITEM_NAME\tITEM_LEVEL\tBUILD_TIME_TICKS\tMATERIALS_REQUIRED\tITEM_BASE_VALUE\t"
                + "ITEM_CLASS_ID\tLID_LOCK\tCONTAINER_CAPACITY||LIQUID_CAPACITY\tCODED_SPELL_LIST";
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
    public String parametersFile() {
        return "glassblowing.txt";
    }

    @Override
    protected List<List<String>> loadRecipes() {
        return super.loadRecipes(parametersFile());
    }

    @Override
    protected boolean doLearnRecipe(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        fireRequired = false;
        return super.doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((buildingI != null) && (!aborted)) {
                    if (messedUp) {
                        if (activity == CraftingActivity.LEARNING)
                            commonEmote(mob, L("<S-NAME> fail(s) to learn how to make @x1.", buildingI.name()));
                        else
                            commonTell(mob, L("@x1 explodes!", CMStrings.capitalizeAndLower(buildingI.name(mob))));
                        buildingI.destroy();
                    } else if (activity == CraftingActivity.LEARNING) {
                        deconstructRecipeInto(buildingI, recipeHolder);
                        buildingI.destroy();
                    } else {
                        dropAWinner(mob, buildingI);
                        CMLib.achievements().possiblyBumpAchievement(mob, AchievementLibrary.Event.CRAFTING, 1, this);
                    }
                }
                buildingI = null;
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean supportsDeconstruction() {
        return true;
    }

    @Override
    public boolean mayICraft(final Item I) {
        if (I == null)
            return false;
        if (!super.mayBeCrafted(I))
            return false;
        if (I.material() != RawMaterial.RESOURCE_GLASS)
            return false;
        if (CMLib.flags().isDeadlyOrMaliciousEffect(I))
            return false;
        if (I instanceof Rideable) {
            final Rideable R = (Rideable) I;
            final int rideType = R.rideBasis();
            switch (rideType) {
                case Rideable.RIDEABLE_LADDER:
                case Rideable.RIDEABLE_SLEEP:
                case Rideable.RIDEABLE_SIT:
                case Rideable.RIDEABLE_TABLE:
                    return true;
                default:
                    return false;
            }
        }
        if (I instanceof Shield)
            return true;
        if (I instanceof Weapon) {
            final Weapon W = (Weapon) I;
            if ((W.weaponClassification() != Weapon.CLASS_BLUNT)
                || ((W instanceof AmmunitionWeapon) && ((AmmunitionWeapon) W).requiresAmmunition()))
                return false;
            return true;
        }
        if (I instanceof Light)
            return true;
        if (I instanceof Armor)
            return false;
        if ((I instanceof Drink) && (!(I instanceof Potion)))
            return true;
        if (I instanceof Potion)
            return false;
        if (I instanceof Container)
            return true;
        if (I instanceof FalseLimb)
            return true;
        if (I instanceof Wand)
            return true;
        if (I.rawProperLocationBitmap() == Wearable.WORN_HELD)
            return true;
        return (isANativeItem(I.Name()));
    }

    public boolean supportsMending(Physical I) {
        return canMend(null, I, true);
    }

    @Override
    protected boolean canMend(MOB mob, Environmental E, boolean quiet) {
        if (!super.canMend(mob, E, quiet))
            return false;
        if ((!(E instanceof Item))
            || (!mayICraft((Item) E))) {
            if (!quiet)
                commonTell(mob, L("That's not a glassblown item."));
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
        fireRequired = true;

        if (super.checkInfo(mob, commands))
            return true;

        randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands, autoGenerate);
        if (commands.size() == 0) {
            commonTell(mob, L("Make what? Enter \"glassblow list\" for a list, \"glassblow info <item>\", \"glassblow learn <item>\" to gain recipes,"
                + " or \"glassblow stop\" to cancel."));
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
        if (str.equalsIgnoreCase("list")) {
            String mask = CMParms.combine(commands, 1);
            boolean allFlag = false;
            if (mask.equalsIgnoreCase("all")) {
                allFlag = true;
                mask = "";
            }
            final int[] cols = {
                CMLib.lister().fixColWidth(29, mob.session()),
                CMLib.lister().fixColWidth(3, mob.session())
            };
            final StringBuffer buf = new StringBuffer(L("@x1 @x2 Sand required\n\r", CMStrings.padRight(L("Item"), cols[0]), CMStrings.padRight(L("Lvl"), cols[1])));
            for (int r = 0; r < recipes.size(); r++) {
                final List<String> V = recipes.get(r);
                if (V.size() > 0) {
                    final String item = replacePercent(V.get(RCP_FINALNAME), "");
                    final int level = CMath.s_int(V.get(RCP_LEVEL));
                    final String wood = getComponentDescription(mob, V, RCP_WOOD);
                    if (((level <= xlevel(mob)) || allFlag)
                        && ((mask.length() == 0) || mask.equalsIgnoreCase("all") || CMLib.english().containsString(item, mask)))
                        buf.append(CMStrings.padRight(item, cols[0]) + " " + CMStrings.padRight("" + level, cols[1]) + " " + wood + "\n\r");
                }
            }
            commonTell(mob, buf.toString());
            return true;
        } else if (((commands.get(0))).equalsIgnoreCase("learn")) {
            return doLearnRecipe(mob, commands, givenTarget, auto, asLevel);
        }
        final Item fire = getRequiredFire(mob, autoGenerate);
        if (fire == null)
            return false;
        activity = CraftingActivity.CRAFTING;
        buildingI = null;
        messedUp = false;
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
            commonTell(mob, L("You don't know how to make a '@x1'.  Try \"glassblow list\" for a list.", recipeName));
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
        bundling = misctype.equalsIgnoreCase("BUNDLE");
        final int[] pm = {RawMaterial.RESOURCE_SAND, RawMaterial.RESOURCE_CRYSTAL, RawMaterial.RESOURCE_GLASS};
        final int[][] data = fetchFoundResourceData(mob,
            woodRequired, "sand", pm,
            0, null, null,
            bundling,
            autoGenerate,
            null);
        if (data == null)
            return false;
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
        if (data[0][FOUND_CODE] == RawMaterial.RESOURCE_SAND)
            buildingI.setMaterial(RawMaterial.RESOURCE_GLASS);
        else
            buildingI.setMaterial(super.getBuildingMaterial(woodRequired, data, compData));
        String itemName = replacePercent(foundRecipe.get(RCP_FINALNAME), RawMaterial.CODES.NAME(buildingI.material())).toLowerCase();
        if (bundling)
            itemName = "a " + woodRequired + "# " + itemName;
        else
            itemName = CMLib.english().startWithAorAn(itemName);
        buildingI.setName(itemName);
        startStr = L("<S-NAME> start(s) blowing @x1.", buildingI.name());
        displayText = L("You are blowing @x1", buildingI.name());
        verb = L("blowing @x1", buildingI.name());
        playSound = "fire.wav";
        buildingI.setDisplayText(L("@x1 lies here", itemName));
        buildingI.setDescription(itemName + ". ");
        buildingI.basePhyStats().setWeight(getStandardWeight(woodRequired + compData[CF_AMOUNT], bundling));
        buildingI.setBaseValue(CMath.s_int(foundRecipe.get(RCP_VALUE)));
        buildingI.basePhyStats().setLevel(CMath.s_int(foundRecipe.get(RCP_LEVEL)));
        setBrand(mob, buildingI);
        final int capacity = CMath.s_int(foundRecipe.get(RCP_CAPACITY));
        final String spell = (foundRecipe.size() > RCP_SPELL) ? foundRecipe.get(RCP_SPELL).trim() : "";
        addSpells(buildingI, spell);
        if (buildingI instanceof Container) {
            if (capacity > 0)
                ((Container) buildingI).setCapacity(capacity + woodRequired);
            if (misctype.equalsIgnoreCase("LID"))
                ((Container) buildingI).setDoorsNLocks(true, false, true, false, false, false);
            else if (misctype.equalsIgnoreCase("LOCK")) {
                ((Container) buildingI).setDoorsNLocks(true, false, true, true, false, true);
                ((Container) buildingI).setKeyName(Double.toString(Math.random()));
            }
            ((Container) buildingI).setContainTypes(Container.CONTAIN_ANYTHING);
        }
        if (buildingI instanceof Drink) {
            if (CMLib.flags().isGettable(buildingI)) {
                ((Drink) buildingI).setLiquidRemaining(0);
                ((Drink) buildingI).setLiquidHeld(capacity * 50);
                ((Drink) buildingI).setThirstQuenched(250);
                if ((capacity * 50) < 250)
                    ((Drink) buildingI).setThirstQuenched(capacity * 50);
            }
        }
        if (bundling)
            buildingI.setBaseValue(lostValue);
        buildingI.recoverPhyStats();
        buildingI.text();
        buildingI.recoverPhyStats();

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
        } else if (bundling) {
            messedUp = false;
            aborted = false;
            unInvoke();
        }
        return true;
    }
}
