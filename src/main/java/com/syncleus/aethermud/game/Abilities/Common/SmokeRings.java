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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Light;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class SmokeRings extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Smoke Rings");

    public SmokeRings() {
        super();
        displayText = "";
        canBeUninvoked = false;
    }

    @Override
    public String ID() {
        return "SmokeRings";
    }

    @Override
    public String name() {
        return localizedName;
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
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ARTISTIC;
    }

    public String getSmokeStr() {
        String str = L("<S-NAME> blow(s) out a perfect smoke ring.");
        switch (CMLib.dice().roll(1, 10, 0)) {
            case 1:
                str = L("<S-NAME> blow(s) out a perfect smoke ring.");
                break;
            case 2:
                str = L("<S-NAME> blow(s) out a swirling string of smoke.");
                break;
            case 3:
                str = L("<S-NAME> blow(s) out a huge smoke ring.");
                break;
            case 4:
                str = L("<S-NAME> blow(s) out a train of tiny smoke rings.");
                break;
            case 5:
                str = L("<S-NAME> blow(s) out a couple of tiny smoke rings.");
                break;
            case 6:
                str = L("<S-NAME> blow(s) out a nice round smoke ring.");
                break;
            case 7:
                str = L("<S-NAME> blow(s) out three big smoke rings.");
                break;
            case 8:
                str = L("<S-NAME> blow(s) out an ENORMOUS smoke ring.");
                break;
            case 9:
                str = L("<S-NAME> blow(s) out a swirl of tiny smoke rings.");
                break;
            case 10:
                str = L("<S-NAME> blow(s) out a smoke ring shaped like a galley.");
                break;
        }
        return str;
    }

    @Override
    public void executeMsg(Environmental affected, CMMsg msg) {
        if (((affected instanceof MOB)
            && (msg.amISource((MOB) affected)))
            && (msg.targetMinor() == CMMsg.TYP_HANDS)
            && (msg.target() instanceof Light)
            && (msg.tool() instanceof Light)
            && (msg.target() == msg.tool())
            && (((Light) msg.target()).amWearingAt(Wearable.WORN_MOUTH))
            && (((Light) msg.target()).isLit())
            && (proficiencyCheck(null, (10 * getXLEVELLevel((MOB) affected)), false))) {
            if (CMLib.dice().rollPercentage() == 1)
                helpProficiency((MOB) affected, 0);
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), null, msg.tool(), CMMsg.MSG_OK_VISUAL, getSmokeStr()));
        }
        super.executeMsg(affected, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        return true;
    }
}
