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
import com.syncleus.aethermud.game.CharClasses.interfaces.CharClass;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Trapper extends Thief {
    private final static String localizedStaticName = CMLib.lang().L("Trapper");
    @SuppressWarnings("unchecked")
    private final Pair<String, Integer>[] minimumStatRequirements = new Pair[]{
        new Pair<String, Integer>("Dexterity", Integer.valueOf(9)),
        new Pair<String, Integer>("Constitution", Integer.valueOf(9))
    };

    public Trapper() {
        super();
        maxStatAdj[CharStats.STAT_DEXTERITY] = 4;
        maxStatAdj[CharStats.STAT_CONSTITUTION] = 4;
    }

    @Override
    public String ID() {
        return "Trapper";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public void initializeClass() {
        super.initializeClass();
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Write", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Specialization_Ranged", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Specialization_EdgedWeapon", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Specialization_Sword", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Apothecary", false, "+WIS 12");
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "ThievesCant", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Recall", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Swim", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Skill_Climb", 50, false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Taxidermy", 50, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Carpentry", 0, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Wainwrighting", 0, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Thief_Caltrops", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Thief_Hide", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 2, "Thief_TrophyCount", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Thief_AvoidTraps", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Thief_IdentifyBombs", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Thief_DetectTraps", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Ranger_FindWater", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Thief_StrategicRetreat", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Thief_IdentifyTraps", false, CMParms.parseSemicolons("Thief_DetectTraps", true), null);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Thief_Sneak", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Skill_Dodge", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Thief_UsePoison", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Thief_MarkTrapped", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Thief_RemoveTraps", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Thief_Trap", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Thief_SneakAttack", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Thief_Listen", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Thief_WildernessSounds", false, CMParms.parseSemicolons("Thief_Listen", true), null);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Thief_AutoMarkTraps", false, CMParms.parseSemicolons("Thief_MarkTrapped;Thief_DetectTraps", true), null);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Thief_AutoDetectTraps", false, CMParms.parseSemicolons("Thief_DetectTraps", true), null);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Fighter_TrueShot", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Skill_Parry", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Ranger_Track", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 12, "Thief_Autocaltrops", false, CMParms.parseSemicolons("Thief_Caltrops", true), null);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Thief_Sap", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Thief_Observation", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Skill_WildernessLore", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Thief_Lure", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 14, "Thief_PlantItem", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Thief_BackStab", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Spell_ReadMagic", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Thief_DazzlingCaltrops", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 16, "Thief_Bind", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Skill_Attack2", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Thief_MakeBomb", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "Thief_Detection", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 18, "AnimalTaming", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Thief_RunningFight", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "CageBuilding", 25, true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Thief_SetAlarm", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Fighter_Pin", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Scrapping", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Skill_Cage", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Domesticating", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Thief_Snipe", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Skill_AttackHalf", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "AnimalTrading", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Thief_Shadow", true);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "AnimalTraining", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 24, "Thief_DisablingCaltrops", false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Thief_TrapImmunity", true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Thief_Kamikaze", true);

        // still not sure if this skill can be gamed or not, for infinite stuff
        //CMLib.ableMapper().addCharAbilityMapping(ID(),27,"Thief_DisassembleTrap",false);

        CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Thief_DeathTrap", true);
    }

    @Override
    public String getOtherBonusDesc() {
        return L("Benefits from animal followers leveling.  Gets experience for selling foreign unconjured animals of comparable level.");
    }

    @Override
    public String getOtherLimitsDesc() {
        return L("Sneak and Hide attempts will fail outside of the wild.");
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        Druid.doAnimalFollowerLevelingCheck(this, host, msg);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(myHost instanceof MOB))
            return super.okMessage(myHost, msg);
        final MOB myChar = (MOB) myHost;
        if (msg.amISource(myChar)
            && (!myChar.isMonster())
            && (msg.tool() instanceof Ability)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (myChar.location() != null)
            && (myChar.isMine(msg.tool()))) {
            // animal trade must be here because execute of trade kills the mob object
            // also, an add trailer is done, which only hits if this msg is not cancelled,
            // so ALL GOOD
            if ((msg.tool().ID().equalsIgnoreCase("AnimalTrading"))
                && (msg.value() < 0)
                && (msg.target() instanceof MOB)
                && (CMLib.flags().isAnimalIntelligence((MOB) msg.target()))
                && (((MOB) msg.target()).getStartRoom() != null)
                && (CMLib.map().areaLocation(myChar) != CMLib.map().getStartArea(msg.target()))) {
                int xp = 125;
                if ((xp > 0) && CMLib.leveler().postExperience(myChar, null, null, xp, true))
                    msg.addTrailerMsg(CMClass.getMsg(myChar, null, null, CMMsg.MSG_OK_VISUAL, L("You gain @x1 experience for selling @x2.", "" + xp, ((MOB) msg.target()).name(myChar)), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null));
            }
            if ((((myChar.location().domainType() & Room.INDOORS) > 0))
                || (myChar.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)) {
                if ((msg.tool().ID().equalsIgnoreCase("Thief_Hide"))
                    || (msg.tool().ID().equalsIgnoreCase("Thief_Sneak"))) {
                    CharClass C = null;
                    CharClass chosenC = null;
                    for (int c = 0; c < myChar.charStats().numClasses(); c++) {
                        C = myChar.charStats().getMyClass(c);
                        if (C == null)
                            continue;
                        final int qlvl = CMLib.ableMapper().getQualifyingLevel(C.ID(), false, msg.tool().ID());
                        if ((qlvl >= 0)
                            && (myChar.charStats().getClassLevel(C) >= qlvl)
                            && ((chosenC == null) || (chosenC.ID().equals(ID()))))
                            chosenC = C;
                    }
                    if ((chosenC != null) && (chosenC.ID().equals(ID()))) {
                        if (msg.tool().ID().equalsIgnoreCase("Thief_Hide")) {
                            myChar.tell(L("You don't know how to hide outside the wilderness."));
                            return false;
                        } else if (msg.tool().ID().equalsIgnoreCase("Thief_Sneak")) {
                            myChar.tell(L("You don't know how to sneak outside the wilderness."));
                            return false;
                        }
                    }
                }
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public String[] getRequiredRaceList() {
        return super.getRequiredRaceList();
    }

    @Override
    public Pair<String, Integer>[] getMinimumStatRequirements() {
        return minimumStatRequirements;
    }

}
