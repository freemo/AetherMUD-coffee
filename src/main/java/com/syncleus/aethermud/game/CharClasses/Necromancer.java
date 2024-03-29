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

import com.syncleus.aethermud.game.CharClasses.interfaces.CharClass;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Set;
import java.util.Vector;


public class Necromancer extends Cleric {
    private final static String localizedStaticName = CMLib.lang().L("Necromancer");
    private final Set<Integer> disallowedWeapons = buildDisallowedWeaponClasses();
    @SuppressWarnings("unchecked")
    private final Pair<String, Integer>[] minimumStatRequirements = new Pair[]{
        new Pair<String, Integer>("Wisdom", Integer.valueOf(9)),
        new Pair<String, Integer>("Constitution", Integer.valueOf(9))
    };
    protected boolean registeredAsListener = false;

    public Necromancer() {
        super();
        maxStatAdj[CharStats.STAT_WISDOM] = 4;
        maxStatAdj[CharStats.STAT_CONSTITUTION] = 4;
    }

    @Override
    public String ID() {
        return "Necromancer";
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
        return CharClass.WEAPONS_EVILCLERIC;
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
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_ControlUndead", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Specialization_EdgedWeapon", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_AnimateSkeleton", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_UndeadInvisibility", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Prayer_Divorce", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_SenseLife", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Prayer_PreserveBody", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Prayer_Desecrate", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Prayer_FeedTheDead", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_AnimateZombie", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Prayer_ProtUndead", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_Deafness", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Prayer_DarkSenses", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_AnimateGhoul", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Prayer_CallUndead", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Prayer_Curse", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Prayer_CauseFatigue", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Prayer_Paralyze", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Prayer_ProtParalyzation", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Prayer_AnimateGhast", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_SenseMagic", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_SenseInvisible", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Prayer_Silence", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_Poison", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Prayer_ProtPoison", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Prayer_Plague", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Prayer_ProtDisease", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_BloodMoon", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Prayer_SenseHidden", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Prayer_AnimateSpectre", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Prayer_HealUndead", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Prayer_GreatCurse", true, CMParms.parseSemicolons("Prayer_Curse", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Prayer_FeignLife", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_Anger", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Prayer_AuraFear", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_Blindness", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Prayer_Blindsight", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Skill_AttackHalf", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Prayer_AnimateGhost", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Prayer_InfuseUnholiness", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_Hellfire", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Prayer_Designation", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Prayer_MassParalyze", true, CMParms.parseSemicolons("Prayer_Paralyze", true));

        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Prayer_AnimateMummy", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Prayer_Vampirism", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_CurseItem", true, CMParms.parseSemicolons("Prayer_Curse", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Prayer_Disenchant", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Prayer_AnimateDead", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Prayer_CauseExhaustion", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Prayer_UnholyWord", true, CMParms.parseSemicolons("Prayer_GreatCurse", true));
        CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Prayer_Nullification", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Prayer_AnimateVampire", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Prayer_Regeneration", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Prayer_MassGrave", false);

        if (!registeredAsListener) {
            synchronized (this) {
                if (!registeredAsListener) {
                    CMLib.map().addGlobalHandler(this, CMMsg.TYP_DEATH);
                    registeredAsListener = true;
                }
            }
        }
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
        return L("Can sense deaths at Necromancer level 15, and becomes a Lich upon death at 30.  Undead followers will not drain experience.");
    }

    @Override
    public String getOtherLimitsDesc() {
        return L("Always fumbles good prayers.  Qualifies and receives evil prayers.  Using non-aligned prayers introduces failure chance.");
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(myHost instanceof MOB))
            return super.okMessage(myHost, msg);
        final MOB myChar = (MOB) myHost;
        if (!super.okMessage(myChar, msg))
            return false;

        if (msg.amISource(myChar)
            && (!myChar.isMonster())
            && (msg.sourceMinor() == CMMsg.TYP_DEATH)
            && (myChar.baseCharStats().getClassLevel(this) >= 30)
            && (!myChar.baseCharStats().getMyRace().ID().equals("Lich"))) {
            final Race newRace = CMClass.getRace("Lich");
            if (newRace != null) {
                myChar.tell(L("The dark powers are transforming you into a @x1!!", newRace.name()));
                myChar.baseCharStats().setMyRace(newRace);
                myChar.recoverCharStats();
            }
        }
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (!(myHost instanceof MOB)) {
            super.executeMsg(myHost, msg);
        } else if ((msg.sourceCode() == CMMsg.TYP_DEATH) && (msg.othersCode() == CMMsg.TYP_DEATH)) {
            final MOB aChar = (MOB) myHost;
            super.executeMsg(myHost, msg);
            MOB M = null;
            final Room myRoom = aChar.location();
            if ((myRoom != null)
                && (myRoom.getArea() != null)) {
                for (final Session S : CMLib.sessions().localOnlineIterable()) {
                    M = S.mob();
                    if ((M != null)
                        && (M.baseCharStats().getCurrentClass() == this)
                        && (aChar != M)
                        && (M.baseCharStats().getClassLevel(this) > 14)
                        && (CMLib.flags().isInTheGame(M, true))
                        && (!M.isAttributeSet(MOB.Attrib.QUIET))) {
                        if (!aChar.isMonster())
                            M.tell(L("^RYou just felt the death of @x1.^N", aChar.Name()));
                        else if ((M.location() != myRoom) && (myRoom.getArea().Name().equals(M.location().getArea().Name())))
                            M.tell(L("^RYou just felt the death of @x1 somewhere nearby.^N", aChar.Name()));
                    }
                }
            }
        } else
            super.executeMsg(myHost, msg);
    }

    @Override
    public boolean isValidClassDivider(MOB killer, MOB killed, MOB mob, Set<MOB> followers) {
        if ((mob != null)
            && (mob != killed)
            && (!mob.amDead())
            && ((!mob.isMonster()) || (!CMLib.flags().isUndead(mob)))
            && ((mob.getVictim() == killed)
            || (followers.contains(mob))
            || (mob == killer)))
            return true;
        return false;
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
