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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.SLinkedList;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Prop_Trashcan extends Property {
    protected SLinkedList<Item> trashables = new SLinkedList<Item>();
    protected int tickDelay = 0;
    protected volatile long lastAddition = 0;

    @Override
    public String ID() {
        return "Prop_Trashcan";
    }

    @Override
    public String name() {
        return "Auto purges items put into a container";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_ROOMS;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (tickID == Tickable.TICKID_PROPERTY_SPECIAL) {
            synchronized (trashables) {
                if ((System.currentTimeMillis() - lastAddition) < ((tickDelay - 1) * CMProps.getTickMillis()))
                    return true;
                for (final Item I : trashables)
                    I.destroy();
                lastAddition = 0;
                trashables.clear();
                CMLib.threads().deleteTick(this, Tickable.TICKID_PROPERTY_SPECIAL);
            }
            return false;
        }
        return true;
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        tickDelay = CMParms.getParmInt(newMiscText, "DELAY", 0);
    }

    protected void process(Item I) {
        if (tickDelay <= 0)
            I.destroy();
        else
            synchronized (trashables) {
                if (lastAddition == 0) {
                    CMLib.threads().deleteTick(this, Tickable.TICKID_PROPERTY_SPECIAL);
                    CMLib.threads().startTickDown(this, Tickable.TICKID_PROPERTY_SPECIAL, tickDelay);
                }
                lastAddition = System.currentTimeMillis() - 10;
                trashables.add(I);
            }
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((affected instanceof Item)
            && (msg.targetMinor() == CMMsg.TYP_PUT)
            && (msg.amITarget(affected))
            && (msg.tool() instanceof Item))
            process((Item) msg.tool());
        else if ((affected instanceof Room)
            && (msg.targetMinor() == CMMsg.TYP_DROP)
            && (msg.target() instanceof Item))
            process((Item) msg.target());
    }
}
