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
package com.syncleus.aethermud.game.Abilities.Diseases;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.DiseaseAffect;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Disease_Lepresy extends Disease {
    private final static String localizedName = CMLib.lang().L("Leprosy");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Leprosy)");
    protected int lastHP = Integer.MAX_VALUE;

    @Override
    public String ID() {
        return "Disease_Lepresy";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 999999;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 10;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your leprosy is cured!");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> look(s) pale!^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return "";
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_CONSUMPTION;
    }

    @Override
    public int difficultyLevel() {
        return 4;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return super.okMessage(myHost, msg);

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if ((msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.targetMessage() != null)) {
            if ((msg.targetMessage().indexOf("<DAMAGE>") >= 0)
                || (msg.targetMessage().indexOf("<DAMAGES>") >= 0))
                msg.modify(msg.source(),
                    msg.target(),
                    msg.tool(),
                    msg.sourceCode(), msg.sourceMessage(),
                    msg.targetCode(), CMLib.combat().replaceDamageTag(msg.targetMessage(), 1, 0, CMMsg.View.TARGET),
                    msg.othersCode(), msg.othersMessage());
            else if (msg.tool() instanceof Weapon)
                msg.modify(msg.source(),
                    msg.target(),
                    msg.tool(),
                    msg.sourceCode(), msg.sourceMessage(),
                    msg.targetCode(), "^e^<FIGHT^>" + ((Weapon) msg.tool()).hitString(1) + "^</FIGHT^>^?",
                    msg.othersCode(), msg.othersMessage());
        }
        return super.okMessage(myHost, msg);
    }

}
