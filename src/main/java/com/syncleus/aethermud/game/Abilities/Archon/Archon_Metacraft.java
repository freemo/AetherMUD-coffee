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
package com.syncleus.aethermud.game.Abilities.Archon;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.ItemCraftor;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Clan;
import com.syncleus.aethermud.game.Items.interfaces.ClanItem;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.*;


public class Archon_Metacraft extends ArchonSkill {
    private final static String localizedName = CMLib.lang().L("Metacrafting");
    private static final String[] triggerStrings = I(new String[]{"METACRAFT"});
    public static List<Ability> craftingSkills = new Vector<Ability>();

    @Override
    public String ID() {
        return "Archon_Metacraft";
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
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (craftingSkills.size() == 0) {
            final Vector<Ability> V = new Vector<Ability>();
            for (final Enumeration<Ability> e = CMClass.abilities(); e.hasMoreElements(); ) {
                final Ability A = e.nextElement();
                if (A instanceof ItemCraftor)
                    V.addElement((Ability) A.copyOf());
            }
            while (V.size() > 0) {
                int lowest = Integer.MAX_VALUE;
                Ability lowestA = null;
                for (int i = 0; i < V.size(); i++) {
                    final Ability A = V.elementAt(i);
                    final int ii = CMLib.ableMapper().lowestQualifyingLevel(A.ID());
                    if (ii < lowest) {
                        lowest = ii;
                        lowestA = A;
                    }
                }
                if (lowestA == null)
                    lowestA = V.firstElement();
                if (lowestA != null) {
                    V.removeElement(lowestA);
                    craftingSkills.add(lowestA);
                } else
                    break;
            }
        }
        if (commands.size() < 1) {
            mob.tell(L("Metacraft what ([recipe], everything, every [recipe], all [skill name]), (optionally) out of what material, and (optionally) to self, to here, or to file [FILENAME]?"));
            return false;
        }
        String mat = null;
        String toWHERE = "SELF";
        if (commands.size() > 1) {
            for (int x = 1; x < commands.size() - 1; x++) {
                if (commands.get(x).equalsIgnoreCase("to")) {
                    if (commands.get(x + 1).equalsIgnoreCase("self")) {
                        toWHERE = "SELF";
                        commands.remove(x);
                        commands.remove(x);
                        break;
                    }
                    if (commands.get(x + 1).equalsIgnoreCase("here")) {
                        toWHERE = "HERE";
                        commands.remove(x);
                        commands.remove(x);
                        break;
                    }
                    if (commands.get(x + 1).equalsIgnoreCase("file") && (x < commands.size() - 2)) {
                        toWHERE = commands.get(x + 2);
                        commands.remove(x);
                        commands.remove(x);
                        commands.remove(x);
                        break;
                    }
                }
            }
            if (commands.size() > 1) {
                mat = (commands.get(commands.size() - 1)).toUpperCase();
                commands.remove(commands.size() - 1);
            }
        }
        int material = -1;
        if (mat != null)
            material = RawMaterial.CODES.FIND_StartsWith(mat);
        if ((mat != null) && (material < 0)) {
            mob.tell(L("'@x1' is not a recognized material.", mat));
            return false;
        }
        ItemCraftor skill = null;
        String recipe = CMParms.combine(commands, 0);
        List<Ability> skillsToUse = new Vector<Ability>();
        boolean everyFlag = false;
        if (recipe.equalsIgnoreCase("everything")) {
            skillsToUse = new XVector<Ability>(craftingSkills);
            everyFlag = true;
            recipe = null;
        } else if (recipe.toUpperCase().startsWith("EVERY ")) {
            everyFlag = true;
            recipe = recipe.substring(6).trim();
            for (int i = 0; i < craftingSkills.size(); i++) {
                skill = (ItemCraftor) craftingSkills.get(i);
                final List<List<String>> V = skill.matchingRecipeNames(recipe, false);
                if ((V != null) && (V.size() > 0))
                    skillsToUse.add(skill);
            }
            if (skillsToUse.size() == 0)
                for (int i = 0; i < craftingSkills.size(); i++) {
                    skill = (ItemCraftor) craftingSkills.get(i);
                    final List<List<String>> V = skill.matchingRecipeNames(recipe, true);
                    if ((V != null) && (V.size() > 0))
                        skillsToUse.add(skill);
                }
        } else if (recipe.toUpperCase().startsWith("ALL ")) {
            everyFlag = true;
            String skillName = recipe.toUpperCase().substring(4);
            skill = (ItemCraftor) CMLib.english().fetchEnvironmental(craftingSkills, skillName, true);
            if (skill == null)
                skill = (ItemCraftor) CMLib.english().fetchEnvironmental(craftingSkills, skillName, false);
            if (skill == null) {
                mob.tell(L("'@x1' is not a known crafting skill.", recipe));
                return false;
            }
            skillsToUse = new XVector<Ability>(skill);
            recipe = null;
        } else {
            for (int i = 0; i < craftingSkills.size(); i++) {
                skill = (ItemCraftor) craftingSkills.get(i);
                final List<List<String>> V = skill.matchingRecipeNames(recipe, false);
                if ((V != null) && (V.size() > 0)) {
                    skillsToUse.add(skill);
                }
            }
            if (skillsToUse.size() == 0)
                for (int i = 0; i < craftingSkills.size(); i++) {
                    skill = (ItemCraftor) craftingSkills.get(i);
                    final List<List<String>> V = skill.matchingRecipeNames(recipe, true);
                    if ((V != null) && (V.size() > 0)) {
                        skillsToUse.add(skill);
                    }
                }
        }
        if (skillsToUse.size() == 0) {
            mob.tell(L("'@x1' can not be made with any of the known crafting skills.", recipe));
            return false;
        }

        boolean success = false;
        final StringBuffer xml = new StringBuffer("<ITEMS>");
        final HashSet<String> files = new HashSet<String>();
        for (int s = 0; s < skillsToUse.size(); s++) {
            skill = (ItemCraftor) skillsToUse.get(s);
            final List<Item> items = new Vector<Item>();
            if (everyFlag) {
                if (recipe == null) {
                    List<ItemCraftor.ItemKeyPair> V = null;
                    if (material >= 0)
                        V = skill.craftAllItemSets(material, false);
                    else
                        V = skill.craftAllItemSets(false);

                    if (V != null) {
                        for (final ItemCraftor.ItemKeyPair L : V) {
                            items.add(L.item);
                            if (L.key != null)
                                items.add(L.key);
                        }
                    }
                } else if (material >= 0) {
                    final ItemCraftor.ItemKeyPair pair = skill.craftItem(recipe, material, false);
                    if (pair != null)
                        items.addAll(pair.asList());
                } else {
                    final ItemCraftor.ItemKeyPair pair = skill.craftItem(recipe);
                    if (pair != null)
                        items.addAll(pair.asList());
                }
            } else if (material >= 0) {
                final ItemCraftor.ItemKeyPair pair = skill.craftItem(recipe, material, false);
                if (pair != null)
                    items.addAll(pair.asList());
            } else {
                final ItemCraftor.ItemKeyPair pair = skill.craftItem(recipe);
                if (pair != null)
                    items.addAll(pair.asList());
            }
            if (items.size() == 0)
                continue;
            success = true;
            if (toWHERE.equals("SELF") || toWHERE.equals("HERE")) {
                for (final Item building : items) {
                    if (building instanceof ClanItem) {
                        final Pair<Clan, Integer> p = CMLib.clans().findPrivilegedClan(mob, Clan.Function.ENCHANT);
                        if (p != null) {
                            final Clan C = p.first;
                            final String clanName = (" " + C.getGovernmentName() + " " + C.name());
                            building.setName(CMStrings.replaceFirst(building.Name(), " Clan None", clanName));
                            building.setDisplayText(CMStrings.replaceFirst(building.displayText(), " Clan None", clanName));
                            building.setDescription(CMStrings.replaceFirst(building.description(), " Clan None", clanName));
                            ((ClanItem) building).setClanID(C.clanID());
                        }
                    }
                    if (toWHERE.equals("HERE")) {
                        mob.location().addItem(building, ItemPossessor.Expire.Player_Drop);
                        mob.location().show(mob, null, null, CMMsg.MSG_OK_ACTION, L("@x1 appears here.", building.name()));
                    } else {
                        mob.moveItemTo(building);
                        mob.location().show(mob, null, null, CMMsg.MSG_OK_ACTION, L("@x1 appears in <S-YOUPOSS> hands.", building.name()));
                    }
                }
            } else
                xml.append(CMLib.aetherMaker().getItemsXML(items, new Hashtable<String, List<Item>>(), files, null));
            mob.location().recoverPhyStats();
            if (!everyFlag)
                break;
        }
        if (success
            && (!toWHERE.equals("SELF"))
            && (!toWHERE.equals("HERE"))) {
            final CMFile file = new CMFile(toWHERE, mob);
            if (!file.canWrite())
                mob.tell(L("Unable to open file '@x1' for writing.", toWHERE));
            else {
                xml.append("</ITEMS>");
                if (files.size() > 0) {
                    final StringBuffer str = new StringBuffer("<FILES>");
                    for (final Iterator<String> i = files.iterator(); i.hasNext(); ) {
                        final String filename = i.next();
                        final StringBuffer buf = new CMFile(Resources.makeFileResourceName(filename), null, CMFile.FLAG_LOGERRORS).text();
                        if ((buf != null) && (buf.length() > 0)) {
                            str.append("<FILE NAME=\"" + filename + "\">");
                            str.append(buf);
                            str.append("</FILE>");
                        }
                    }
                    str.append("</FILES>");
                    xml.append(str);
                }
                file.saveText(xml);
                mob.tell(L("File '@x1' written.", file.getAbsolutePath()));
            }
        }
        if (!success) {
            mob.tell(L("The metacraft failed."));
            return false;
        }
        return true;
    }
}
