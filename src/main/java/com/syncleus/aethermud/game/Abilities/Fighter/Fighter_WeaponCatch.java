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
package com.planet_ink.game.Abilities.Fighter;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;


public class Fighter_WeaponCatch extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Weapon Catch");

    @Override
    public String ID() {
        return "Fighter_WeaponCatch";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (CMLib.flags().isAliveAwakeMobileUnbound(mob, true))
            && (msg.tool() instanceof Ability)
            && (msg.tool().ID().equals("Skill_Disarm"))
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(mob, 0, false))
            && (mob.rangeToTarget() == 0)) {
            final CMMsg msg2 = CMClass.getMsg(mob, msg.source(), this, CMMsg.MSG_NOISYMOVEMENT,
                L("<T-NAME> disarms <S-NAMESELF>, but <S-NAME> catch the weapon!"),
                L("<T-NAME> disarm <S-NAMESELF>, but <S-NAME> catches the weapon!"),
                L("<T-NAME> disarms <S-NAMESELF>, but <S-NAME> catches the weapon!"));
            if (mob.location().okMessage(mob, msg2)) {
                mob.location().send(mob, msg2);
                helpProficiency(mob, 0);
                return false;
            }
        }
        return true;
    }
}
