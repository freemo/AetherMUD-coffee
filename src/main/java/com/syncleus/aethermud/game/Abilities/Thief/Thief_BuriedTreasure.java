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
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Thief_BuriedTreasure extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Buried Treasure");

    @Override
    public String ID() {
        return "Thief_BuriedTreasure";
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
        return CAN_ITEMS | CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_DECEPTIVE;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.target() instanceof Container)
            && (msg.source() == affected)
            && (msg.target().ID().equalsIgnoreCase("HoleInTheGround"))
            && (msg.targetMinor() == CMMsg.TYP_PUT)
            && (msg.tool() != null)
            && (proficiencyCheck(msg.source(), 0, false))) {
            final Room R = CMLib.map().roomLocation(msg.target());
            if ((R != null) && (CMLib.map().getExtendedRoomID(R).length() > 0)) {
                if ((msg.target() instanceof Physical)
                    && (((Physical) msg.target()).fetchEffect(ID()) == null)) {
                    Ability A = (Ability) this.copyOf();
                    A.setMiscText(msg.source().Name());
                    ((Physical) msg.target()).addNonUninvokableEffect(A);
                }
                this.helpProficiency(msg.source(), 0);
                msg.addTrailerMsg(CMClass.getMsg(msg.source(), null, null, CMMsg.MSG_OK_VISUAL,
                    L("You have buried some treasure!"), -1, null, -1, null));
            }
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected instanceof Item)
            && (msg.target() == ((Item) affected).owner())
            && (!text().equals(msg.source().Name()))) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_DIG:
                    final MOB M = CMLib.players().getLoadPlayer(text());
                    if (M != null) {
                        Ability A = M.fetchAbility(ID());
                        if ((A == null)
                            || (CMLib.dice().rollPercentage() < (50 + A.adjustedLevel(M, 0) + (5 * this.getXLEVELLevel(M))))) {
                            msg.setSourceCode(CMMsg.MSG_NOISYMOVEMENT);
                            msg.setTargetCode(CMMsg.MSG_NOISYMOVEMENT);
                            msg.setOthersCode(CMMsg.MSG_NOISYMOVEMENT);
                        }
                    }
                    break;
            }
        }
        return super.okMessage(myHost, msg);
    }
}