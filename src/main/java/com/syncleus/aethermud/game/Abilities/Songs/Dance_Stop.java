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
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Dance_Stop extends Dance {
    private final static String localizedName = CMLib.lang().L("Stop");

    public Dance_Stop() {
        super();
        setProficiency(100);
    }

    @Override
    public String ID() {
        return "Dance_Stop";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    protected boolean skipStandardSongInvoke() {
        return true;
    }

    @Override
    public void setProficiency(int newProficiency) {
        super.setProficiency(100);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        boolean foundOne = false;
        for (int a = 0; a < mob.numEffects(); a++) // personal affects
        {
            final Ability A = mob.fetchEffect(a);
            if ((A != null) && (A instanceof Dance))
                foundOne = true;
        }
        undanceAll(mob, null);
        if (!foundOne) {
            mob.tell(auto ? L("There is no dance going.") : L("You aren't dancing."));
            return true;
        }

        mob.location().show(mob, null, CMMsg.MSG_NOISE, auto ? L("Rest.") : L("<S-NAME> stop(s) dancing."));
        mob.location().recoverRoomStats();
        return true;
    }
}
