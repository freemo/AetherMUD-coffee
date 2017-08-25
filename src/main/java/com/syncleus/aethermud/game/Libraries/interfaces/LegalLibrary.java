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
package com.syncleus.aethermud.game.Libraries.interfaces;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.LegalBehavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Law;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.Deity;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.LandTitle;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.PrivateProperty;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public interface LegalLibrary extends CMLibrary {
    /**
     * The default room description for a room for sale
     * These get localized later.
     */
    public final static String SALESTR = " This lot is for sale (look id).";

    /**
     * The default room description for a room for rent
     * These get localized later.
     */
    public final static String RENTSTR = " This lot (look id) is for rent on a monthly basis.";

    /**
     * The default room title for an indoor room
     * These get localized later.
     */
    public final static String INDOORSTR = " An empty room";

    /**
     * The default room title for an outdoor room
     * These get localized later.
     */
    public final static String OUTDOORSTR = " An empty plot";

    public Law getTheLaw(Room R, MOB mob);

    public LegalBehavior getLegalBehavior(Area A);

    public LegalBehavior getLegalBehavior(Room R);

    public Area getLegalObject(Area A);

    public Area getLegalObject(Room R);

    public String getLandOwnerName(Room room);

    public String getPropertyOwnerName(Room room);

    public void colorRoomForSale(Room R, LandTitle title, boolean reset);

    public LandTitle getLandTitle(Area area);

    public LandTitle getLandTitle(Room room);

    public PrivateProperty getPropertyRecord(Area area);

    public PrivateProperty getPropertyRecord(Room room);

    public PrivateProperty getPropertyRecord(Item item);

    public boolean isRoomSimilarlyTitled(LandTitle title, Room R);

    public Set<Room> getHomePeersOnThisFloor(Room room, Set<Room> doneRooms);

    public boolean isHomeRoomDownstairs(Room room);

    public boolean isHomeRoomUpstairs(Room room);

    public boolean doesHavePriviledgesHere(MOB mob, Room room);

    public boolean doesAnyoneHavePrivilegesHere(MOB mob, String overrideID, Room R);

    public boolean doesHavePriviledgesInThisDirection(MOB mob, Room room, Exit exit);

    public boolean doesHavePrivilegesWith(final MOB mob, final PrivateProperty record);

    public boolean doesHaveWeakPrivilegesWith(final MOB mob, final PrivateProperty record);

    public boolean doesHaveWeakPriviledgesHere(MOB mob, Room room);

    public boolean doesOwnThisLand(String name, Room room);

    public boolean doesOwnThisLand(MOB mob, Room room);

    public boolean mayOwnThisItem(MOB mob, Item item);

    public boolean doesOwnThisProperty(String name, Room room);

    public boolean doesOwnThisProperty(MOB mob, Room room);

    public boolean doesOwnThisProperty(MOB mob, PrivateProperty record);

    public boolean robberyCheck(PrivateProperty record, CMMsg msg);

    public MOB getPropertyOwner(PrivateProperty record);

    public boolean canAttackThisProperty(MOB mob, PrivateProperty record);

    public List<LandTitle> getAllUniqueLandTitles(Enumeration<Room> e, String owner, boolean includeRentals);

    public Ability getClericInfusion(Physical room);

    public Deity getClericInfused(Room room);

    public boolean isLegalOfficerHere(MOB mob);

    public boolean isLegalJudgeHere(MOB mob);

    public boolean isLegalOfficialHere(MOB mob);

    public boolean isACity(Area A);
}
