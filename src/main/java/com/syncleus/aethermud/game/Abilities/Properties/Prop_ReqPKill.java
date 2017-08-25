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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Rideable;


public class Prop_ReqPKill extends Property implements TriggeredAffect {
    @Override
    public String ID() {
        return "Prop_ReqPKill";
    }

    @Override
    public String name() {
        return "Playerkill ONLY Zone";
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
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null)
            && (((msg.target() instanceof Room) && (msg.targetMinor() == CMMsg.TYP_ENTER))
            || ((msg.target() instanceof Rideable) && (msg.targetMinor() == CMMsg.TYP_SIT)))
            && (!CMLib.flags().isFalling(msg.source()))
            && ((msg.amITarget(affected)) || (msg.tool() == affected) || (affected instanceof Area))) {
            if ((!msg.source().isMonster())
                && (!msg.source().isAttributeSet(MOB.Attrib.PLAYERKILL))) {
                msg.source().tell(L("You must have your playerkill flag set to enter here."));
                return false;
            }
        }
        if ((!msg.source().isMonster())
            && (!msg.source().isAttributeSet(MOB.Attrib.PLAYERKILL))) {
            final Room R = CMLib.map().roomLocation(msg.source());
            if ((R != null) && ((R == affected) || (R.getArea() == affected) || ((affected instanceof Area) && (((Area) affected).inMyMetroArea(R.getArea()))))) {
                msg.source().tell(L("Your PLAYERKILL flag is now ON!"));
                msg.source().setAttribute(MOB.Attrib.PLAYERKILL, true);
            }
        }
        return super.okMessage(myHost, msg);
    }
}
