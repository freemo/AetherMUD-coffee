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
package com.planet_ink.coffee_mud.Abilities.Fighter;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class Fighter_CoverDefence extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Cover Defence");
    public int hits = 0;

    @Override
    public String ID() {
        return "Fighter_CoverDefence";
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
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_EVASIVE;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (CMLib.flags().isAliveAwakeMobile(mob, true))
            && (msg.source().rangeToTarget() > 0)
            && (mob.phyStats().height() < 84)
            && (msg.tool() instanceof Weapon)
            && ((((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_RANGED)
            || (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_THROWN))
            && (proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 90 + (2 * getXLEVELLevel(mob)), false))
            && (msg.source().getVictim() == mob)) {
            final CMMsg msg2 = CMClass.getMsg(msg.source(), mob, null, CMMsg.MSG_QUIETMOVEMENT, L("<T-NAME> take(s) cover from <S-YOUPOSS> attack!"));
            if (mob.location().okMessage(mob, msg2)) {
                mob.location().send(mob, msg2);
                helpProficiency(mob, 0);
                return false;
            }
        }
        return true;
    }
}
