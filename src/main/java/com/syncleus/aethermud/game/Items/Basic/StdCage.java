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
package com.planet_ink.game.Items.Basic;

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.CagedAnimal;
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class StdCage extends StdContainer {
    public StdCage() {
        super();
        setName("a cage");
        setDisplayText("a cage sits here.");
        setDescription("It\\`s of solid wood construction with metal bracings.  The door has a key hole.");
        capacity = 1000;
        setContainTypes(Container.CONTAIN_BODIES | Container.CONTAIN_CAGED);
        material = RawMaterial.RESOURCE_OAK;
        baseGoldValue = 15;
        basePhyStats().setWeight(25);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdCage";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_EXIT_REOPEN) && (isOpen())) {
            final Room R = CMLib.map().roomLocation(this);
            if ((R != null) && (owner() instanceof Room) && (CMLib.flags().isInTheGame(this, true))) {
                final List<Item> mobContents = getContents();
                for (final Item item : mobContents) {
                    final Environmental E = item;
                    if (E instanceof CagedAnimal) {
                        final MOB M = ((CagedAnimal) E).unCageMe();
                        if (M != null)
                            M.bringToLife(R, true);
                        R.show(M, null, this, CMMsg.MSG_OK_ACTION, L("<S-NAME> escapes from <O-NAME>!"));
                        E.destroy();
                    }
                }
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_CLOSE:
                    if ((hasALid) && (isOpen)) {
                        if (CMLib.threads().isTicking(this, Tickable.TICKID_EXIT_REOPEN))
                            CMLib.threads().deleteTick(this, Tickable.TICKID_EXIT_REOPEN);
                    }
                    break;
                case CMMsg.TYP_OPEN:
                    if ((hasALid) && (!isOpen) && (!isLocked)) {
                        if ((owner() instanceof Room)
                            && (!CMLib.threads().isTicking(this, Tickable.TICKID_EXIT_REOPEN)))
                            CMLib.threads().startTickDown(this, Tickable.TICKID_EXIT_REOPEN, 30);
                    }
                    break;
                case CMMsg.TYP_LOOK:
                case CMMsg.TYP_EXAMINE: {
                    synchronized (this) {
                        if (!isOpen) {
                            isOpen = true;
                            super.executeMsg(myHost, msg);
                            isOpen = false;
                            return;
                        }
                    }
                    break;
                }
            }
        }
        super.executeMsg(myHost, msg);
    }
}
