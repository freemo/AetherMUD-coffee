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
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Prop_NoOrdering extends Property {
    @Override
    public String ID() {
        return "Prop_NoOrdering";
    }

    @Override
    public String name() {
        return "Group/Ordering Neutralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_MOBS;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if ((msg.targetMinor() == CMMsg.TYP_ORDER)
            && (msg.source().location() != null)
            && (msg.target() instanceof MOB)
            && ((msg.source().location() == affected)
            || ((affected instanceof Area) && (((Area) affected).inMyMetroArea(msg.source().location().getArea())))
            || (msg.target() == affected))
            && (!CMSecurity.isAllowed(msg.source(), msg.source().location(), CMSecurity.SecFlag.CMDMOBS))) {
            if (affected instanceof MOB)
                msg.source().tell(L("You don't feel very commanding around here."));
            else
                msg.source().tell(msg.source(), msg.target(), null, L("<T-NAME> isn't paying any attention to you."));
            return false;
        }
        return true;
    }
}
