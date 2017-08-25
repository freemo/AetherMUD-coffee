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
import com.syncleus.aethermud.game.Abilities.interfaces.TriggeredAffect;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Rideable;


public class Prop_ReqHeight extends Property implements TriggeredAffect {
    @Override
    public String ID() {
        return "Prop_ReqHeight";
    }

    @Override
    public String name() {
        return "Height Restrictions";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_EXITS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_ZAPPER;
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_ENTER;
    }

    @Override
    public String accountForYourself() {
        return "Height limit: " + CMath.s_int(text());
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null)
            && (((msg.target() instanceof Room) && (msg.targetMinor() == CMMsg.TYP_ENTER))
            || ((msg.target() instanceof Rideable) && (msg.targetMinor() == CMMsg.TYP_SIT)))
            && ((msg.amITarget(affected)) || (msg.tool() == affected) || (affected instanceof Area))) {
            int height = 100;
            if (CMath.isInteger(text()))
                height = CMath.s_int(text());
            if (msg.source().phyStats().height() > height) {
                msg.source().tell(L("You are too tall to fit in there."));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }
}
