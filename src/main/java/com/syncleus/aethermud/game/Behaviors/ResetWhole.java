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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;


@SuppressWarnings("rawtypes")
public class ResetWhole extends StdBehavior {
    protected long lastAccess = -1;
    long time = 1800000;

    @Override
    public String ID() {
        return "ResetWhole";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_ROOMS | Behavior.CAN_AREAS;
    }

    @Override
    public String accountForYourself() {
        return "periodic resetting";
    }

    @Override
    public void setParms(String parameters) {
        super.setParms(parameters);
        try {
            time = Long.parseLong(parameters);
            time = time * CMProps.getTickMillis();
        } catch (final Exception e) {
        }
    }

    @Override
    public void executeMsg(Environmental E, CMMsg msg) {
        super.executeMsg(E, msg);
        if (!msg.source().isMonster()) {
            final Room R = msg.source().location();
            if (R != null) {
                if ((E instanceof Area)
                    && (((Area) E).inMyMetroArea(R.getArea())))
                    lastAccess = System.currentTimeMillis();
                else if ((E instanceof Room) && (R == E))
                    lastAccess = System.currentTimeMillis();
            }
        }
    }

    private boolean isRoomBeingCamped(final Room R) {
        if (CMLib.flags().canNotBeCamped(R)
            && (R.numPCInhabitants() > 0)
            && (!CMLib.tracking().isAnAdminHere(R, false))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);
        if (lastAccess < 0)
            return true;

        if ((lastAccess + time) < System.currentTimeMillis()) {
            if (ticking instanceof Area) {
                for (final Enumeration r = ((Area) ticking).getMetroMap(); r.hasMoreElements(); ) {
                    Room R = (Room) r.nextElement();
                    for (final Enumeration<Behavior> e = R.behaviors(); e.hasMoreElements(); ) {
                        final Behavior B = e.nextElement();
                        if ((B != null) && (B.ID().equals(ID()))) {
                            R = null;
                            break;
                        }
                    }
                    if ((R != null) && (!this.isRoomBeingCamped(R))) {
                        CMLib.map().resetRoom(R, true);
                    }
                }
            } else if (ticking instanceof Room) {
                if (!this.isRoomBeingCamped((Room) ticking))
                    CMLib.map().resetRoom((Room) ticking, true);
            } else {
                final Room room = super.getBehaversRoom(ticking);
                if ((room != null) && (!this.isRoomBeingCamped(room)))
                    CMLib.map().resetRoom(room, true);
            }
            lastAccess = System.currentTimeMillis();
        }
        return true;
    }
}
