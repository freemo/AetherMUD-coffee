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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Fighter_Roll extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Roll With Blows");
    public boolean doneThisRound = false;
    int regain = -1;

    @Override
    public String ID() {
        return "Fighter_Roll";
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
        return Ability.QUALITY_OK_SELF;
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
        regain = -1;
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (msg.amITarget(mob)
            && (CMLib.flags().isAliveAwakeMobile(mob, true))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && ((msg.value()) > 0)
            && (msg.tool() instanceof Weapon)
            && (mob.rangeToTarget() == 0)
            && (!doneThisRound)
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, -85 + mob.charStats().getStat(CharStats.STAT_DEXTERITY) + (2 * getXLEVELLevel(mob)), false))) {
            doneThisRound = true;
            final double pctRecovery = (CMath.div(proficiency(), 100.0) * Math.random());
            regain = (int) Math.round(CMath.mul((msg.value()), pctRecovery));
            msg.setValue(msg.value() - regain);
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB)
            doneThisRound = false;
        return super.tick(ticking, tickID);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if (!(affected instanceof MOB))
            return;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (regain > 0)) {
            msg.addTrailerMsg(CMClass.getMsg(mob, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> roll(s) with the hit.")));
            helpProficiency(mob, 0);
            regain = -1;
        }
    }
}
