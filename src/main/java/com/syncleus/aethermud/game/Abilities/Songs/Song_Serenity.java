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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Song_Serenity extends Song {
    private final static String localizedName = CMLib.lang().L("Serenity");

    @Override
    public String ID() {
        return "Song_Serenity";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    protected boolean maliciousButNotAggressiveFlag() {
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected == null)
            return super.okMessage(myHost, msg);
        if (!(affected instanceof MOB))
            return super.okMessage(myHost, msg);
        if ((CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (CMLib.flags().canBeHeardSpeakingBy(invoker, msg.source()))
            && (msg.target() != null)) {
            msg.source().makePeace(true);
            msg.source().tell(L("You feel too peaceful to fight."));
            return false;
        }
        return super.okMessage(myHost, msg);
    }
}
