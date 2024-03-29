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
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Dance_Basse extends Dance {
    private final static String localizedName = CMLib.lang().L("Basse");

    @Override
    public String ID() {
        return "Dance_Basse";
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
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && ((msg.amITarget(affected)))) {
            final MOB target = (MOB) msg.target();
            if ((!target.isInCombat())
                && (msg.source().getVictim() != target)
                && (msg.source().location() == target.location())
                && (CMLib.dice().rollPercentage() > ((msg.source().phyStats().level() - (target.phyStats().level() + getXLEVELLevel(invoker())) * 10)))) {
                msg.source().tell(L("You are too much in awe of @x1", target.name(msg.source())));
                if (target.getVictim() == msg.source()) {
                    target.makePeace(true);
                    target.setVictim(null);
                }
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }
}
