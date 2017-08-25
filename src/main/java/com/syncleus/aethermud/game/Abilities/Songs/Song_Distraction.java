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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Song_Distraction extends Song {
    private final static String localizedName = CMLib.lang().L("Distraction");

    @Override
    public String ID() {
        return "Song_Distraction";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;
        if (affected == invoker)
            return true;

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if (msg.amISource(mob)) {
            if ((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
                && (mob.isInCombat())
                && (CMLib.dice().rollPercentage() > (mob.charStats().getSave(CharStats.STAT_SAVE_MIND) + 50 - super.adjustedLevel(invoker(), 0)))
                && ((msg.sourceMajor(CMMsg.MASK_HANDS))
                || (msg.sourceMajor(CMMsg.MASK_MOVE)))) {
                mob.location().show(mob, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> appear(s) distracted by the singing."));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

}
