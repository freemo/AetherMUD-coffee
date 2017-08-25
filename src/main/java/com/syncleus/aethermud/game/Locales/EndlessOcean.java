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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Directions;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;


public class EndlessOcean extends StdGrid {
    public EndlessOcean() {
        super();
        name = "the ocean";
        basePhyStats.setWeight(2);
        recoverPhyStats();
        climask = CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "EndlessOcean";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_WATERSURFACE;
    }

    @Override
    public String getGridChildLocaleID() {
        return "SaltWaterSurface";
    }

    @Override
    public List<Integer> resourceChoices() {
        return UnderSaltWater.roomResources;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        UnderWater.sinkAffects(this, msg);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        switch (CMLib.tracking().isOkWaterSurfaceAffect(this, msg)) {
            case CANCEL:
                return false;
            case FORCEDOK:
                return true;
            default:
            case CONTINUE:
                return super.okMessage(myHost, msg);
        }
    }

    @Override
    public void buildGrid() {
        super.buildGrid();
        if (subMap != null) {
            final Exit ox = CMClass.getExit("Open");
            if (rawDoors()[Directions.NORTH] == null) {
                for (final Room[] element : subMap) {
                    if (element[0] != null)
                        linkRoom(element[0], element[yGridSize() / 2], Directions.NORTH, ox, ox);
                }
            }
            if (rawDoors()[Directions.SOUTH] == null) {
                for (final Room[] element : subMap) {
                    if (element[yGridSize() - 1] != null)
                        linkRoom(element[yGridSize() - 1], element[yGridSize() / 2], Directions.SOUTH, ox, ox);
                }
            }
            if (rawDoors()[Directions.EAST] == null) {
                for (int i = 0; i < subMap[0].length; i++) {
                    if (subMap[xGridSize() - 1][i] != null)
                        linkRoom(subMap[xGridSize() - 1][i], subMap[xGridSize() / 2][i], Directions.EAST, ox, ox);
                }
            }
            if (rawDoors()[Directions.WEST] == null)
                for (int i = 0; i < subMap[0].length; i++)
                    if (subMap[0][i] != null)
                        linkRoom(subMap[0][i], subMap[xGridSize() / 2][i], Directions.WEST, ox, ox);
            if (Directions.NORTHEAST < Directions.NUM_DIRECTIONS()) {
                if (rawDoors()[Directions.NORTHEAST] == null) {
                    for (final Room[] element : subMap) {
                        if (element[0] != null)
                            linkRoom(element[0], subMap[xGridSize() / 2][yGridSize() / 2], Directions.NORTHEAST, ox, ox);
                    }
                    for (int i = 0; i < subMap[0].length; i++)
                        if (subMap[subMap.length - 1][i] != null)
                            linkRoom(subMap[subMap.length - 1][i], subMap[xGridSize() / 2][yGridSize() / 2], Directions.NORTHEAST, ox, ox);
                }
                if (rawDoors()[Directions.NORTHWEST] == null) {
                    for (final Room[] element : subMap) {
                        if (element[0] != null)
                            linkRoom(element[0], subMap[xGridSize() / 2][yGridSize() / 2], Directions.NORTHWEST, ox, ox);
                    }
                    for (int i = 0; i < subMap[0].length; i++)
                        if (subMap[0][i] != null)
                            linkRoom(subMap[0][i], subMap[xGridSize() / 2][yGridSize() / 2], Directions.NORTHWEST, ox, ox);
                }
                if (rawDoors()[Directions.SOUTHWEST] == null) {
                    for (final Room[] element : subMap) {
                        if (element[yGridSize() - 1] != null)
                            linkRoom(element[yGridSize() - 1], subMap[xGridSize() / 2][yGridSize() / 2], Directions.SOUTHWEST, ox, ox);
                    }
                    for (int i = 0; i < subMap[0].length; i++)
                        if (subMap[0][i] != null)
                            linkRoom(subMap[0][i], subMap[xGridSize() / 2][yGridSize() / 2], Directions.SOUTHWEST, ox, ox);
                }
                if (rawDoors()[Directions.SOUTHEAST] == null) {
                    for (final Room[] element : subMap) {
                        if (element[yGridSize() - 1] != null)
                            linkRoom(element[yGridSize() - 1], subMap[xGridSize() / 2][yGridSize() / 2], Directions.SOUTHEAST, ox, ox);
                    }
                    for (int i = 0; i < subMap[0].length; i++)
                        if (subMap[subMap.length - 1][i] != null)
                            linkRoom(subMap[subMap.length - 1][i], subMap[xGridSize() / 2][yGridSize() / 2], Directions.NORTHEAST, ox, ox);
                }
            }
        }
    }
}
