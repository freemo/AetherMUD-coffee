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
package com.planet_ink.game.Behaviors;

import com.planet_ink.game.Behaviors.interfaces.Behavior;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.Vector;


public class RandomTeleporter extends ActiveTicker {
    protected Vector<Integer> restrictedLocales = null;
    protected boolean nowander = false;

    public RandomTeleporter() {
        super();
        minTicks = 1;
        maxTicks = 5;
        chance = 100;
        restrictedLocales = null;
        tickReset();
    }

    @Override
    public String ID() {
        return "RandomTeleporter";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public long flags() {
        return Behavior.FLAG_MOBILITY;
    }

    @Override
    public String accountForYourself() {
        return "random teleporting";
    }

    public boolean okRoomForMe(Room currentRoom, Room newRoom) {
        if (currentRoom == null)
            return false;
        if (newRoom == null)
            return false;
        if ((nowander) && ((currentRoom.getArea() != newRoom.getArea())))
            return false;
        if (restrictedLocales == null)
            return true;
        return !restrictedLocales.contains(Integer.valueOf(newRoom.domainType()));
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        nowander = false;
        restrictedLocales = null;
        final Vector<String> V = CMParms.parse(newParms);
        for (int v = 0; v < V.size(); v++) {
            String s = V.elementAt(v);
            if (s.toUpperCase().startsWith("NOWANDER"))
                nowander = true;
            else if ((s.startsWith("+") || (s.startsWith("-"))) && (s.length() > 1)) {
                if (restrictedLocales == null)
                    restrictedLocales = new Vector<Integer>();
                if (s.equalsIgnoreCase("+ALL"))
                    restrictedLocales.clear();
                else if (s.equalsIgnoreCase("-ALL")) {
                    restrictedLocales.clear();
                    for (int i = 0; i < Room.DOMAIN_INDOORS_DESCS.length; i++)
                        restrictedLocales.addElement(Integer.valueOf(Room.INDOORS + i));
                    for (int i = 0; i < Room.DOMAIN_OUTDOOR_DESCS.length; i++)
                        restrictedLocales.addElement(Integer.valueOf(i));
                } else {
                    final char c = s.charAt(0);
                    s = s.substring(1).toUpperCase().trim();
                    int code = -1;
                    for (int i = 0; i < Room.DOMAIN_INDOORS_DESCS.length; i++) {
                        if (Room.DOMAIN_INDOORS_DESCS[i].startsWith(s))
                            code = Room.INDOORS + i;
                    }
                    if (code >= 0) {
                        if ((c == '+') && (restrictedLocales.contains(Integer.valueOf(code))))
                            restrictedLocales.removeElement(Integer.valueOf(code));
                        else if ((c == '-') && (!restrictedLocales.contains(Integer.valueOf(code))))
                            restrictedLocales.addElement(Integer.valueOf(code));
                    }
                    code = -1;
                    for (int i = 0; i < Room.DOMAIN_OUTDOOR_DESCS.length; i++) {
                        if (Room.DOMAIN_OUTDOOR_DESCS[i].startsWith(s))
                            code = i;
                    }
                    if (code >= 0) {
                        if ((c == '+') && (restrictedLocales.contains(Integer.valueOf(code))))
                            restrictedLocales.removeElement(Integer.valueOf(code));
                        else if ((c == '-') && (!restrictedLocales.contains(Integer.valueOf(code))))
                            restrictedLocales.addElement(Integer.valueOf(code));
                    }

                }
            }
        }
        if ((restrictedLocales != null) && (restrictedLocales.size() == 0))
            restrictedLocales = null;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);
        if ((canAct(ticking, tickID)) && (ticking instanceof MOB)) {
            final MOB mob = (MOB) ticking;
            if ((!CMLib.flags().canTrack(mob)) && (CMLib.dice().roll(1, 100, 0) > 1)) {
                return true;
            }
            int tries = 0;
            Room R = null;
            while (((++tries) < 250) && (R == null)) {
                R = CMLib.map().getRandomRoom();
                if (R != null) {
                    if ((!CMLib.flags().isInFlight(mob))
                        && ((R.domainType() == Room.DOMAIN_INDOORS_AIR)
                        || (R.domainType() == Room.DOMAIN_OUTDOORS_AIR)))
                        R = null;
                    else if ((!CMLib.flags().isSwimming(mob))
                        && (CMLib.flags().isUnderWateryRoom(R)))
                        R = null;
                    else if (!okRoomForMe(mob.location(), R))
                        R = null;
                }
            }
            final Room oldRoom = mob.location();
            CMLib.tracking().wanderAway(mob, true, false);
            if (R != null)
                R.bringMobHere(mob, true);
            if (mob.location() == oldRoom)
                tickDown = 0;
        }
        return true;
    }
}
