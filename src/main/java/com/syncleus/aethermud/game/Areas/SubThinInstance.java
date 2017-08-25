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
package com.planet_ink.game.Areas;

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.Resources;
import com.planet_ink.game.core.collections.STreeMap;
import com.planet_ink.game.core.collections.SVector;

import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.List;


public class SubThinInstance extends StdThinInstance {
    public SubThinInstance() {
        super.flags = Area.FLAG_THIN | Area.FLAG_INSTANCE_CHILD;
    }

    @Override
    public String ID() {
        return "SubThinInstance";
    }

    @Override
    protected boolean qualifiesToBeParentArea(Area parentA) {
        return (parentA != this);
    }

    @Override
    protected boolean doesManageChildAreas() {
        return true;
    }

    @Override
    protected boolean doesManageMobLists() {
        return true;
    }

    @Override
    protected Area getParentArea() {
        if ((parentArea != null) && (parentArea.get() != null))
            return parentArea.get();
        Area A = super.getParentArea();
        if (A != null)
            return A;
        int x = Name().indexOf('_');
        if (x < 0)
            x = Name().indexOf(' ');
        if (x < 0)
            return null;
        final Area parentA = CMLib.map().getArea(Name().substring(x + 1));
        if ((parentA == null)
            || (!qualifiesToBeParentArea(parentA)))
            return null;
        parentArea = new WeakReference<Area>(parentA);
        return parentA;
    }

    @Override
    public int[] getAreaIStats() {
        if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            return emptyStats;
        final Area parentArea = getParentArea();
        final String areaName = (parentArea == null) ? Name() : parentArea.Name();
        int[] statData = (int[]) Resources.getResource("STATS_" + areaName.toUpperCase());
        if (statData != null)
            return statData;
        if ((parentArea != null) && (parentArea != this))
            return parentArea.getAreaIStats();
        return super.getAreaIStats();
    }

    @Override
    protected Area createRedirectArea(List<MOB> mobs) {
        if (instanceChildren.size() == 0) {
            final Area oldArea = this.getParentArea();
            properRooms = new STreeMap<String, Room>(new Area.RoomIDComparator());
            properRoomIDSet = null;
            metroRoomIDSet = null;
            blurbFlags = new STreeMap<String, String>();
            for (final Enumeration<String> e = oldArea.getProperRoomnumbers().getRoomIDs(); e.hasMoreElements(); )
                addProperRoomnumber(convertToMyArea(e.nextElement()));
            setAreaState(Area.State.ACTIVE); // starts ticking
            final List<WeakReference<MOB>> newMobList = new SVector<WeakReference<MOB>>(5);
            for (final MOB mob : mobs)
                newMobList.add(new WeakReference<MOB>(mob));
            final AreaInstanceChild child = new AreaInstanceChild(this, newMobList);
            instanceChildren.add(child);
        } else {
            for (final MOB mob : mobs)
                instanceChildren.get(0).mobs.add(new WeakReference<MOB>(mob));
        }
        return this;
    }
}
