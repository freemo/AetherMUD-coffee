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
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class Fighter_CritStrike extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Critical Strike");

    @Override
    public String ID() {
        return "Fighter_CritStrike";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (msg.amISource(mob)
            && (CMLib.flags().isAliveAwakeMobile(mob, true))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.target() != null)
            && (mob.getVictim() == msg.target())
            && (mob.rangeToTarget() == 0)
            && (msg.tool() instanceof Weapon)
            && (((Weapon) msg.tool()).weaponClassification() != Weapon.CLASS_RANGED)
            && (((Weapon) msg.tool()).weaponClassification() != Weapon.CLASS_THROWN)
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, (-90) + mob.charStats().getStat(CharStats.STAT_STRENGTH) + (2 * getXLEVELLevel(mob)), false))) {
            final double pctRecovery = (CMath.div(proficiency(), 100.0) * Math.random());
            final int bonus = (int) Math.round(CMath.mul((msg.value()), pctRecovery));
            msg.setValue(msg.value() + bonus);
            helpProficiency(mob, 0);
        }
        return true;
    }
}
