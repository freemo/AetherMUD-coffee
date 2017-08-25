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
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Play_Ditty extends Play {
    private final static String localizedName = CMLib.lang().L("Ditty");

    @Override
    public String ID() {
        return "Play_Ditty";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected String songOf() {
        return CMLib.english().startWithAorAn(name());
    }

    @Override
    public long flags() {
        return Ability.FLAG_HEALINGMAGIC;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected != null) && (affected instanceof MOB) && (invoker() != null)) {
            final MOB mob = (MOB) affected;
            final int healing = 4 + (int) Math.round(CMath.mul(adjustedLevel(invoker(), 0), 0.25));
            CMLib.combat().postHealing(invoker(), mob, this, healing, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
        }
        return true;
    }
}
