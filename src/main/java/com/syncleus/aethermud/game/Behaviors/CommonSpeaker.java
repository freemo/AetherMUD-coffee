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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class CommonSpeaker extends StdBehavior {
    int tickTocker = 1;
    int tickTock = 0;
    String language = "Common";

    @Override
    public String ID() {
        return "CommonSpeaker";
    }

    @Override
    public String accountForYourself() {
        return language + " speaking";
    }

    @Override
    public void setParms(String parameters) {
        super.setParms(parameters);
        if (parameters.trim().length() > 0)
            language = parameters;
        else
            language = "Common";
        tickTocker = 1;
        tickTock = 0;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);

        if (tickID != Tickable.TICKID_MOB)
            return true;
        if (--tickTock > 0)
            return true;
        if (!(ticking instanceof Environmental))
            return true;

        final Ability L = CMClass.getAbilityPrototype(language);
        if (L == null)
            Log.errOut("CommonSpeaker on " + ticking.name() + " in " + CMLib.map().getExtendedRoomID(CMLib.map().roomLocation((Environmental) ticking))
                + " has unknown language '" + language + "'");
        else {
            final Ability A = ((MOB) ticking).fetchAbility(L.ID());
            if (A == null) {
                final Ability lA = CMClass.getAbility(L.ID());
                lA.setProficiency(100);
                lA.setSavable(false);
                ((MOB) ticking).addAbility(lA);
                lA.autoInvocation((MOB) ticking, false);
                lA.invoke((MOB) ticking, null, false, 0);
            } else
                A.invoke((MOB) ticking, null, false, 0);
        }
        if ((++tickTocker) == 100)
            tickTocker = 99;
        tickTock = tickTocker;
        return true;
    }
}
