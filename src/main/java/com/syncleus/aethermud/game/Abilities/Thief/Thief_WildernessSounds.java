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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Thief_WildernessSounds extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Wilderness Sounds");
    protected Thief_Listen myListen = null;

    @Override
    public String ID() {
        return "Thief_WildernessSounds";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return affected != invoker;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((myListen == null) && (ticking instanceof MOB)) {
            myListen = (Thief_Listen) ((MOB) ticking).fetchAbility("Thief_Listen");
            if (myListen != null) {
                myListen.flags.add(Thief_Listen.ListenFlag.OUTDOORS);
            }
        }
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((msg.source() == affected)
            && (myListen != null)
            && (msg.tool() == myListen)
            && (msg.target() instanceof Room)
            && (msg.target() != msg.source().location())
            && (!CMLib.flags().isUnderWateryRoom((Room) msg.target()))
            && ((((Room) msg.target()).domainType() & Room.INDOORS) == 0)) {
            if (msg.source().isMine(msg.tool())) {
                if (!super.proficiencyCheck(msg.source(), super.getXLEVELLevel(msg.source()) * 5, false)) {
                    msg.source().tell(L("You don't hear anything."));
                    return false;
                } else {
                    super.helpProficiency(msg.source(), super.getXLEVELLevel(msg.source()));
                }
            } else {
                myListen = null;
            }
        }
        return true;
    }
}
