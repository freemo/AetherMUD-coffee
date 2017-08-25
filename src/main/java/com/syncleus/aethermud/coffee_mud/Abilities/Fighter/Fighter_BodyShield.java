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
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Fighter_BodyShield extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Body Shield");
    public boolean doneThisRound = false;

    @Override
    public String ID() {
        return "Fighter_BodyShield";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_GRAPPLING;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (msg.amITarget(mob)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (!mob.amDead())
            && (!CMLib.flags().isSleeping(mob))
            && (msg.source() != mob.getVictim())
            && (msg.source() != mob)
            && ((msg.value()) > 0)
            && (msg.tool() instanceof Weapon)
            && (mob.getVictim() != null)
            && (mob.getVictim().fetchEffect("Fighter_Pin") != null)
            && (!doneThisRound)
            && (mob.getVictim().baseWeight() >= (mob.baseWeight() / 2))) {
            final Ability A = mob.fetchEffect("Fighter_Pin");
            if ((A != null) && (A.invoker() == mob)) {
                doneThisRound = true;
                final int regain = (int) Math.round(CMath.mul((msg.value()), CMath.div(proficiency(), 100.0)));
                msg.setValue(msg.value() - regain);
                final CMMsg msg2 = CMClass.getMsg(mob, mob.getVictim(), this, CMMsg.MSG_DAMAGE, L("<S-NAME> use(s) <T-NAMESELF> as a body shield!"));
                msg2.setValue(regain);
                msg.addTrailerMsg(msg2);
                helpProficiency(mob, 0);
            }
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB)
            doneThisRound = false;
        return super.tick(ticking, tickID);
    }
}
