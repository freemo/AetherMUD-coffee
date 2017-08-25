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
package com.syncleus.aethermud.game.CharClasses;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.CharClasses.interfaces.CharClass;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;
import java.util.Vector;


public class Templar extends Cleric {
    private final static String localizedStaticName = CMLib.lang().L("Templar");
    private final Set<Integer> disallowedWeapons = buildDisallowedWeaponClasses();
    @SuppressWarnings("unchecked")
    private final Pair<String, Integer>[] minimumStatRequirements = new Pair[]{
        new Pair<String, Integer>("Wisdom", Integer.valueOf(9)),
        new Pair<String, Integer>("Strength", Integer.valueOf(9))
    };
    protected int tickDown = 0;

    public Templar() {
        super();
        maxStatAdj[CharStats.STAT_STRENGTH] = 4;
        maxStatAdj[CharStats.STAT_WISDOM] = 4;
    }

    @Override
    public String ID() {
        return "Templar";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public String baseClass() {
        return "Cleric";
    }

    @Override
    public int getAttackAttribute() {
        return CharStats.STAT_WISDOM;
    }

    @Override
    public int allowedWeaponLevel() {
        return CharClass.WEAPONS_ANY;
    }

    @Override
    protected Set<Integer> disallowedWeaponClasses(MOB mob) {
        return disallowedWeapons;
    }

    @Override
    protected int alwaysFlunksThisQuality() {
        return 1000;
    }

    @Override
    public void initializeClass() {
        super.initializeClass();
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Recall", 100, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Swim", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Write", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Revoke", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_WandUse", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Convert", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Specialization_Sword", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Specialization_BluntWeapon", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_InfuseUnholiness", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_CauseLight", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_Annul", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_Divorce", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseGood", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseLife", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_CauseFatigue", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Prayer_Desecrate", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Specialization_EdgedWeapon", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtGood", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_UnholyArmament", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Specialization_FlailedWeapon", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_Deafness", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Skill_Parry", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_Heresy", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_CauseSerious", false, CMParms.parseSemicolons("Prayer_CauseLight", true));

        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Prayer_Curse", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Prayer_HuntGood", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Specialization_Polearm", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Prayer_Paralyze", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Prayer_Behemoth", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Specialization_Hammer", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Specialization_Ranged", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_DispelGood", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_CauseExhaustion", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_Poison", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_ProtPoison", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Skill_AttackHalf", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Prayer_Plague", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Prayer_ProtDisease", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_BloodMoon", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_DesecrateLand", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Specialization_Axe", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Skill_Bash", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Prayer_CauseCritical", false, CMParms.parseSemicolons("Prayer_CauseSerious", true));

        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Prayer_GreatCurse", true, CMParms.parseSemicolons("Prayer_Curse", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Specialization_Natural", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_Anger", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_BloodHearth", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_Blindness", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_BoneMoon", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Prayer_Tithe", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Prayer_Enervate", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Prayer_Drain", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_Hellfire", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_Maladiction", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Prayer_MassParalyze", true, CMParms.parseSemicolons("Prayer_Paralyze", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Prayer_Absorption", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Prayer_Corruption", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Skill_Attack2", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Prayer_Harm", false, CMParms.parseSemicolons("Prayer_CauseCritical", true));

        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_CurseItem", true, CMParms.parseSemicolons("Prayer_Curse", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_Haunted", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Prayer_CreateIdol", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Prayer_UnholyWord", true, CMParms.parseSemicolons("Prayer_GreatCurse", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Prayer_SunCurse", 0, "", false, false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Prayer_Regeneration", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Prayer_Avatar", true);
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(ticking instanceof MOB))
            return super.tick(ticking, tickID);
        final MOB myChar = (MOB) ticking;
        if ((tickID == Tickable.TICKID_MOB) && ((--tickDown) <= 0)) {
            tickDown = 5;
            if (myChar.fetchEffect("Prayer_AuraStrife") == null) {
                final Ability A = CMClass.getAbility("Prayer_AuraStrife");
                if (A != null)
                    A.invoke(myChar, myChar, true, 0);
            }
        }
        return super.tick(myChar, tickID);
    }

    @Override
    public String[] getRequiredRaceList() {
        return super.getRequiredRaceList();
    }

    @Override
    public Pair<String, Integer>[] getMinimumStatRequirements() {
        return minimumStatRequirements;
    }

    @Override
    public String getOtherBonusDesc() {
        return L("Receives Aura of Strife which increases in power.");
    }

    @Override
    public String getOtherLimitsDesc() {
        return L("Always fumbles good prayers.  Using non-evil prayers introduces failure chance.");
    }

    @Override
    public List<Item> outfit(MOB myChar) {
        if (outfitChoices == null) {
            final Weapon w = CMClass.getWeapon("Shortsword");
            if (w == null)
                return new Vector<Item>();
            outfitChoices = new Vector<Item>();
            outfitChoices.add(w);
        }
        return outfitChoices;
    }

}
