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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Dance_Foxtrot extends Dance {
    private final static String localizedName = CMLib.lang().L("Foxtrot");
    protected int ticks = 1;
    protected int increment = 1;

    @Override
    public String ID() {
        return "Dance_Foxtrot";
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
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if ((((MOB) target).curState().getMana() >= ((MOB) target).maxState().getMana() / 2)
                    && (((MOB) target).curState().getMovement() >= ((MOB) target).maxState().getMovement() / 2))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        final MOB mob = (MOB) affected;
        if (mob == null)
            return false;

        mob.curState().adjMovement((invokerManaCost / 15) + increment, mob.maxState());
        mob.curState().adjMana(increment, mob.maxState());
        if (increment <= 1 + (int) Math.round(CMath.div(adjustedLevel(invoker(), 0), 3))) {
            if ((++ticks) > 2) {
                increment++;
                ticks = 1;
            }
        }
        return true;
    }

}
