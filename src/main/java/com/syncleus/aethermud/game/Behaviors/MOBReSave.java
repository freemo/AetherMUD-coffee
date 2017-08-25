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

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.lang.ref.WeakReference;
import java.util.HashSet;


@SuppressWarnings({"unchecked", "rawtypes"})
public class MOBReSave extends ActiveTicker {
    protected static HashSet<Room> roomsReset = new HashSet<Room>();
    protected boolean noRecurse = false;
    protected CharStats startStats = null;
    protected WeakReference host = null;
    public MOBReSave() {
        super();
        minTicks = 140;
        maxTicks = 140;
        chance = 100;
        tickReset();
    }

    @Override
    public String ID() {
        return "MOBReSave";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public long flags() {
        return 0;
    }

    @Override
    public String accountForYourself() {
        return "persisting";
    }

    @Override
    public String getParms() {
        if (host == null)
            return super.getParms();
        final MOB M = (MOB) host.get();
        if (M == null)
            return super.getParms();
        final StringBuffer rebuiltParms = new StringBuffer(super.rebuildParms());
        for (final int c : CharStats.CODES.ALLCODES())
            rebuiltParms.append(" " + CharStats.CODES.ABBR(c) + "=" + M.baseCharStats().getStat(c));
        return rebuiltParms.toString();
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        startStats = (CharStats) CMClass.getCommon("DefaultCharStats");
        for (final int c : CharStats.CODES.ALLCODES())
            startStats.setStat(c, CMParms.getParmInt(parms, CharStats.CODES.ABBR(c), -1));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((ticking instanceof MOB)
            && (tickID == Tickable.TICKID_MOB)
            && (!((MOB) ticking).amDead())
            && (!noRecurse)
            && (CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            && (((MOB) ticking).getStartRoom() != null)
            && (((MOB) ticking).getStartRoom().roomID().length() > 0)
            && (((MOB) ticking).databaseID().length() > 0)) {
            final MOB mob = (MOB) ticking;
            if ((host == null) || (host.get() == null))
                host = new WeakReference(mob);
            noRecurse = true;
            if (startStats != null) {
                synchronized (startStats) {
                    for (final int c : CharStats.CODES.ALLCODES()) {
                        if (startStats.getStat(c) > 0)
                            mob.baseCharStats().setStat(c, startStats.getStat(c));
                    }
                    startStats = null;
                }
            }
            if (canAct(ticking, tickID)) {
                final Room R = mob.getStartRoom();
                CMLib.database().DBUpdateMOB(R.roomID(), mob);
            }
        }
        noRecurse = false;
        return true;
    }

}
