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
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.Log;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Rideable;

import java.util.Vector;


public class GenMobilePortal extends GenPortal implements Rideable, Exit {
    protected StdPortal myStationaryPortal = null;

    @Override
    public String ID() {
        return "GenMobilePortal";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        switch (msg.targetMinor()) {
            case CMMsg.TYP_DISMOUNT:
                break;
            case CMMsg.TYP_ENTER: {
                final Room R = CMLib.map().roomLocation(this);
                if ((myStationaryPortal != null)
                    && (!myStationaryPortal.amDestroyed()))
                    myStationaryPortal.setReadableText(CMLib.map().getExtendedRoomID(R));
                else {
                    myStationaryPortal = null;
                    final Room destR = getDestinationRoom(R);
                    if (destR == null) {
                        msg.source().tell(L("Something has gone wrong!"));
                        Log.errOut("GenMobilePortal", "Destination not found: " + readableText());
                        return;
                    }
                    final Vector<Item> choices = new Vector<Item>();
                    for (int i = 0; i < destR.numItems(); i++) {
                        final Item I = destR.getItem(i);
                        if ((I != null) && (I instanceof StdPortal))
                            choices.addElement(I);
                    }
                    MOB M = null;
                    for (int m = 0; m < destR.numInhabitants(); m++) {
                        M = destR.fetchInhabitant(m);
                        if (M != null) {
                            for (int i = 0; i < M.numItems(); i++) {
                                final Item I = M.getItem(i);
                                if ((I != null) && (I instanceof StdPortal))
                                    choices.addElement(I);
                            }
                        }
                    }
                    if (choices.size() > 0) {
                        if (choices.size() == 1)
                            myStationaryPortal = (StdPortal) choices.firstElement();
                        else {
                            if (((myStationaryPortal == null) || (myStationaryPortal.amDestroyed())) && (secretIdentity().length() > 0)) {
                                for (int i = 0; i < choices.size(); i++) {
                                    if (choices.elementAt(i).secretIdentity().equals(secretIdentity())) {
                                        myStationaryPortal = (StdPortal) choices.elementAt(i);
                                        break;
                                    }
                                }
                            }
                            if ((myStationaryPortal == null) || (myStationaryPortal.amDestroyed())) {
                                for (int i = 0; i < choices.size(); i++) {
                                    if (choices.elementAt(i).Name().equals(Name())) {
                                        myStationaryPortal = (StdPortal) choices.elementAt(i);
                                        break;
                                    }
                                }
                            }
                            if ((myStationaryPortal == null) || (myStationaryPortal.amDestroyed()))
                                myStationaryPortal = (StdPortal) choices.firstElement();
                        }
                    }
                }
                break;
            }
        }
    }
}
