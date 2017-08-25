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
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Dance_Jingledress extends Dance {
    private final static String localizedName = CMLib.lang().L("Jingledress");

    @Override
    public String ID() {
        return "Dance_Jingledress";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected String danceOf() {
        return name() + " Dance";
    }

    @Override
    public long flags() {
        return Ability.FLAG_HEALINGMAGIC;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        final MOB mob = (MOB) affected;
        if (mob == null)
            return false;

        if (invoker() != null) {
            final int healing = CMLib.dice().roll(2, adjustedLevel(invoker(), 0), 4);
            CMLib.combat().postHealing(invoker(), mob, this, healing, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
        }
        return true;
    }

}
