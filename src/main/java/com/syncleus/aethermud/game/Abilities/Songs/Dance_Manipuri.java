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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Dance_Manipuri extends Dance {
    private final static String localizedName = CMLib.lang().L("Manipuri");
    protected Room lastRoom = null;
    protected int count = 3;

    @Override
    public String ID() {
        return "Dance_Manipuri";
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
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return true;
        final MOB mob = (MOB) affected;
        if (mob.location() != lastRoom) {
            count = 3 + getXLEVELLevel(invoker());
            lastRoom = mob.location();
        } else
            count--;
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        final MOB mob = (MOB) affected;
        if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (mob.location() != null)
            && ((msg.amITarget(mob)))
            && ((count > 0) || (lastRoom == null) || (lastRoom != mob.location()))) {
            final MOB target = (MOB) msg.target();
            if ((!target.isInCombat())
                && (msg.source().getVictim() != target)) {
                msg.source().tell(L("You feel like letting @x1 be for awhile.", target.name(msg.source())));
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

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        count = 3;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        count = 3;
        return true;
    }

}
