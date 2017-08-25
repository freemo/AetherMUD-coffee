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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Skill_BellyRolling extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Belly Rolling");
    protected boolean doneThisRound = false;

    @Override
    public String ID() {
        return "Skill_BellyRolling";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_FOOLISHNESS;
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
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB)
            doneThisRound = false;
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (CMLib.flags().isAliveAwakeMobile(mob, true))
            && (CMLib.flags().isSitting(mob))
            && (!doneThisRound)
            && (msg.tool() instanceof Weapon)) {
            // can't use -NAME for msg.source() lest sitting prevent it
            final CMMsg msg2 = CMClass.getMsg(mob, msg.source(), null, CMMsg.MSG_SITMOVE, L("<S-NAME> roll(s) away from the attack by <T-NAMESELF>!"));
            if ((proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 50 + (super.getXLEVELLevel(mob) * 5), false))
                && ((msg.source().getVictim() == mob) || (msg.source().getVictim() == null))
                && (mob.location().okMessage(mob, msg2))) {
                doneThisRound = true;
                mob.location().send(mob, msg2);
                helpProficiency(mob, 0);
                return false;
            }
        }
        return true;
    }
}

