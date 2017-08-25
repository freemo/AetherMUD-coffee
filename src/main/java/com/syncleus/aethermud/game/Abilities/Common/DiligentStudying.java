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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class DiligentStudying extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Diligent Studying");

    @Override
    public String ID() {
        return "DiligentStudying";
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
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.sourceMinor() == CMMsg.TYP_LEVEL) {
            if (msg.source() == affected) {
                int amt = (msg.value() - msg.source().basePhyStats().level());
                int multiplier = CMath.s_int(text());
                if (multiplier != 0)
                    amt = amt * multiplier;
                if (amt == 1)
                    msg.source().tell(L("^NYou gain ^H1^N practice point.\n\r^N"));
                else if (amt > 1)
                    msg.source().tell(L("^NYou gain ^H@x1^N practice points.\n\r^N", "" + amt));
                else if (amt == -1)
                    msg.source().tell(L("^NYou lose ^H1^N practice point.\n\r^N"));
                else if (amt < -1)
                    msg.source().tell(L("^NYou lose ^H@x1^N practice points.\n\r^N", "" + (-amt)));

                msg.source().setPractices(msg.source().getPractices() + amt);
                if (msg.source().getPractices() < 0)
                    msg.source().setPractices(0);
            }
        }
        super.executeMsg(myHost, msg);
    }
}
