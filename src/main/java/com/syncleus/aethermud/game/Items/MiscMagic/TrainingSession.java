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
package com.planet_ink.game.Items.MiscMagic;

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.Basic.StdItem;
import com.planet_ink.game.Items.interfaces.MiscMagic;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class TrainingSession extends StdItem implements MiscMagic {
    public TrainingSession() {
        super();
        setName("a training session");
        setDisplayText("A shiny gold coin has been left here.");
        myContainer = null;
        setDescription("A shiny gold coin with magical script around the edges.");
        myUses = Integer.MAX_VALUE;
        myWornCode = 0;
        material = 0;
        basePhyStats.setWeight(0);
        basePhyStats.setSensesMask(basePhyStats().sensesMask() | PhyStats.SENSE_ITEMNORUIN | PhyStats.SENSE_ITEMNOWISH);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "TrainingSession";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)) {
            final MOB mob = msg.source();
            switch (msg.targetMinor()) {
                case CMMsg.TYP_GET:
                case CMMsg.TYP_REMOVE: {
                    setContainer(null);
                    unWear();
                    if (!mob.isMine(this))
                        mob.setTrains(mob.getTrains() + 1);
                    if (!CMath.bset(msg.targetMajor(), CMMsg.MASK_OPTIMIZE))
                        mob.location().recoverRoomStats();
                    destroy();
                    return;
                }
                default:
                    break;
            }
        }
        super.executeMsg(myHost, msg);
    }
}
