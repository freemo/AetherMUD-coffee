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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;


public class Prop_PeaceMaker extends Property {
    @Override
    public String ID() {
        return "Prop_PeaceMaker";
    }

    @Override
    public String name() {
        return "Strike Neuralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "Peace Maker";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((CMath.bset(msg.sourceMajor(), CMMsg.MASK_MALICIOUS))
            || (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            || (CMath.bset(msg.othersMajor(), CMMsg.MASK_MALICIOUS))) {
            if ((msg.target() != null)
                && (msg.source() != affected)
                && (msg.source() != msg.target())) {
                if (affected instanceof MOB) {
                    final MOB mob = (MOB) affected;
                    if ((CMLib.flags().isAliveAwakeMobileUnbound(mob, true))
                        && (!mob.isInCombat())) {
                        String t = "No fighting!";
                        if (text().length() > 0) {
                            final List<String> V = CMParms.parseSemicolons(text(), true);
                            t = V.get(CMLib.dice().roll(1, V.size(), -1));
                        }
                        CMLib.commands().postSay(mob, msg.source(), t, false, false);
                    } else
                        return super.okMessage(myHost, msg);
                } else {
                    String t = "You feel too peaceful here.";
                    if (text().length() > 0) {
                        final List<String> V = CMParms.parseSemicolons(text(), true);
                        t = V.get(CMLib.dice().roll(1, V.size(), -1));
                    }
                    msg.source().tell(t);
                }
                final MOB victim = msg.source().getVictim();
                if (victim != null)
                    victim.makePeace(true);
                msg.source().makePeace(true);
                msg.modify(msg.source(), msg.target(), msg.tool(), CMMsg.NO_EFFECT, "", CMMsg.NO_EFFECT, "", CMMsg.NO_EFFECT, "");
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }
}
