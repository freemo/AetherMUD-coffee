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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.CharClasses.interfaces.CharClass;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class ThiefSkill extends StdAbility {
    private final static String localizedName = CMLib.lang().L("a Thief Skill");

    @Override
    public String ID() {
        return "ThiefSkill";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int enchantQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if ((!auto)
            && (!mob.isMonster())
            && (!disregardsArmorCheck(mob))
            && (!CMLib.utensils().armorCheck(mob, CharClass.ARMOR_LEATHER))
            && (mob.isMine(this))
            && (mob.location() != null)
            && (CMLib.dice().rollPercentage() < 50)) {
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> fumble(s) @x1 due to <S-HIS-HER> clumsy armor!", name()));
            return false;
        }
        return true;
    }

    public int getMOBLevel(MOB meMOB) {
        if (meMOB == null)
            return 0;
        return meMOB.phyStats().level();
    }

    public MOB getHighestLevelMOB(MOB meMOB, Vector<MOB> not) {
        if (meMOB == null)
            return null;
        final Room R = meMOB.location();
        if (R == null)
            return null;
        int highestLevel = 0;
        MOB highestMOB = null;
        final Set<MOB> H = meMOB.getGroupMembers(new HashSet<MOB>());
        if (not != null)
            H.addAll(not);
        for (int i = 0; i < R.numInhabitants(); i++) {
            final MOB M = R.fetchInhabitant(i);
            if ((M != null)
                && (M != meMOB)
                && (!CMLib.flags().isSleeping(M))
                && (!H.contains(M))
                && (highestLevel < M.phyStats().level())
                && (!CMSecurity.isASysOp(M))) {
                highestLevel = M.phyStats().level();
                highestMOB = M;
            }
        }
        return highestMOB;
    }

    public Physical getOpenable(MOB mob, Room room, Physical givenTarget, List<String> commands, int[] dirCode, boolean failOnOpen) {
        if ((room == null) || (mob == null))
            return null;
        final String whatToOpen = CMParms.combine(commands, 0);
        Physical unlockThis = null;
        dirCode[0] = CMLib.directions().getGoodDirectionCode(whatToOpen);
        if (dirCode[0] >= 0)
            unlockThis = room.getExitInDir(dirCode[0]);
        if (unlockThis == null)
            unlockThis = getTarget(mob, room, givenTarget, commands, Wearable.FILTER_ANY);
        else if (givenTarget != null)
            unlockThis = givenTarget;

        if (unlockThis instanceof Exit) {
            if (((Exit) unlockThis).isOpen() == failOnOpen) {
                if (failOnOpen)
                    mob.tell(mob, unlockThis, null, L("<T-NAME> is open!"));
                else
                    mob.tell(mob, unlockThis, null, L("<T-NAME> is closed!"));
                return null;
            }
        } else if (unlockThis instanceof Container) {
            if (((Container) unlockThis).isOpen() == failOnOpen) {
                if (failOnOpen)
                    mob.tell(mob, unlockThis, null, L("<T-NAME> is open!"));
                else
                    mob.tell(mob, unlockThis, null, L("<T-NAME> is closed!"));
                return null;
            }
        } else {
            mob.tell(mob, unlockThis, null, L("You can't do that to <T-NAME>."));
            return null;
        }
        return unlockThis;
    }
}
