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
package com.planet_ink.game.Abilities.Misc;

import com.planet_ink.game.Abilities.StdAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Tickable;


public class BadReputation extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Bad Reputation");
    double changePct = 0.45;

    @Override
    public String ID() {
        return "BadReputation";
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
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PROPERTY;
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
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        if ((newMiscText.length() > 0) && (CMath.isPct(newMiscText))) {
            changePct = CMath.s_pct(newMiscText);
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        return true;
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if (affected instanceof MOB) {
        }
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (affected instanceof MOB) {
            if ((msg.source() == affected)
                && (msg.sourceMinor() == CMMsg.TYP_FACTIONCHANGE)
                && (msg.othersMessage() != null)
                && (msg.value() > 0)
                && (msg.value() < Integer.MAX_VALUE)
                && (CMLib.factions().getFaction(msg.othersMessage()) != null)) {
                double prof;
                if ((invoker() != affected) && (canBeUninvoked()))
                    prof = super.getXLEVELLevel(invoker());
                else
                    prof = 0.0;
                msg.setValue((int) Math.round(msg.value() * (changePct + (prof * 0.05))));
            }
            super.executeMsg(myHost, msg);
        }
        super.executeMsg(myHost, msg);
    }
}
