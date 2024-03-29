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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Thief_AnalyzeMark extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Analyze Mark");

    @Override
    public String ID() {
        return "Thief_AnalyzeMark";
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
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
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
    public boolean disregardsArmorCheck(MOB mob) {
        return true;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_COMBATLORE;
    }

    public MOB getMark(MOB mob) {
        final Thief_Mark A = (Thief_Mark) mob.fetchEffect("Thief_Mark");
        if (A != null)
            return A.mark;
        return null;
    }

    public int getMarkTicks(MOB mob) {
        final Thief_Mark A = (Thief_Mark) mob.fetchEffect("Thief_Mark");
        if ((A != null) && (A.mark != null))
            return A.ticks;
        return -1;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (msg.amISource(mob)
                && ((msg.targetMinor() == CMMsg.TYP_LOOK) || (msg.targetMinor() == CMMsg.TYP_EXAMINE))
                && (msg.target() != null)
                && (getMark(mob) == msg.target())
                && (getMarkTicks(mob) > 15)
                && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(mob, 0, false))) {
                if (CMLib.dice().rollPercentage() > 50)
                    helpProficiency((MOB) affected, 0);
                final StringBuilder str = CMLib.commands().getScore((MOB) msg.target());
                if (!mob.isMonster())
                    mob.session().wraplessPrintln(str.toString());
            }
        }
        super.executeMsg(myHost, msg);
    }
}
