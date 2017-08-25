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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.ItemPossessor.Expire;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.ArrayList;
import java.util.List;


public class Chant_FindDriftwood extends Chant_FindPlant {
    private final static String localizedName = CMLib.lang().L("Find Driftwood");
    private final int[] myMats = {RawMaterial.MATERIAL_WOODEN};
    private Item theDriftwood = null;
    private Room theDriftroom = null;

    public Chant_FindDriftwood() {
        super();

        lookingFor = "driftwood";
    }

    @Override
    public String ID() {
        return "Chant_FindDriftwood";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public String displayText() {
        return L("(Finding Diftwood)");
    }

    @Override
    public long flags() {
        return Ability.FLAG_TRACKING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int[] okMaterials() {
        return myMats;
    }

    @Override
    protected int[] okResources() {
        return null;
    }

    @Override
    public String itsHere(MOB mob, Room R) {
        if (R == null)
            return "";
        if ((theDriftwood != null)
            && (theDriftwood.owner() instanceof Room)
            && (CMLib.map().roomLocation(theDriftwood) == R))
            return "There seems to be " + lookingFor + " around here.\n\r";
        return "";
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.target() == theDriftwood)
            && (msg.targetMinor() == CMMsg.TYP_GET)) {
            msg.addTrailerRunnable(new Runnable() {

                @Override
                public void run() {
                    unInvoke();
                }
            });
        }
    }

    @Override
    protected boolean findWhatImLookingFor(MOB mob, String s) {
        TrackingLibrary.TrackingFlags flags = getTrackingFlags();
        int limit = 50 - (super.getXLEVELLevel(mob) + super.getXMAXRANGELevel(mob));
        final List<Room> checkSet = CMLib.tracking().getRadiantRooms(mob.location(), flags, limit);
        if ((checkSet == null) || (checkSet.size() < limit)) {
            mob.tell(L("You don't sense any driftwood around here.  Perhaps no wrecks have occurred?"));
            return false;
        }
        final int[] choices = new int[]{
            RawMaterial.RESOURCE_OAK,
            RawMaterial.RESOURCE_WOOD,
            RawMaterial.RESOURCE_PINE,
            RawMaterial.RESOURCE_BALSA,
            RawMaterial.RESOURCE_MAPLE,
            RawMaterial.RESOURCE_REDWOOD,
            RawMaterial.RESOURCE_HICKORY,
            RawMaterial.RESOURCE_IRONWOOD,
            RawMaterial.RESOURCE_YEW,
            RawMaterial.RESOURCE_TEAK,
            RawMaterial.RESOURCE_CEDAR,
            RawMaterial.RESOURCE_ELM,
            RawMaterial.RESOURCE_CHERRYWOOD,
            RawMaterial.RESOURCE_BEECHWOOD,
            RawMaterial.RESOURCE_WILLOW,
            RawMaterial.RESOURCE_SYCAMORE,
            RawMaterial.RESOURCE_SPRUCE,
            RawMaterial.RESOURCE_MESQUITE,
            RawMaterial.RESOURCE_BAMBOO,
            RawMaterial.RESOURCE_REED,
        };
        this.whatImLookingFor = choices[CMLib.dice().roll(1, choices.length, -1)];
        limit = 150 - (2 * super.getXLEVELLevel(mob));
        if (checkSet.size() > limit)
            theDriftroom = checkSet.get(limit + CMLib.dice().roll(1, checkSet.size() - limit, -1));
        else
            theDriftroom = checkSet.get(checkSet.size() - 1);
        theDriftwood = CMLib.materials().makeItemResource(this.whatImLookingFor);
        final int amount = CMLib.dice().roll(1, 4, 3);
        theDriftwood.basePhyStats().setWeight(amount);
        theDriftwood.phyStats().setWeight(amount);
        CMLib.materials().adjustResourceName(theDriftwood);
        theDriftroom.addItem(theDriftwood, Expire.Player_Drop);
        theTrail = null;
        return true;
    }

    @Override
    protected List<Room> makeTheTrail(MOB mob, MOB target, Room mobRoom) {
        if (theDriftroom == null) {
            theTrail = null;
            theDriftroom = null;
            if (theDriftwood != null)
                theDriftwood.destroy();
            theDriftwood = null;
            return null;
        }

        TrackingLibrary.TrackingFlags flags = getTrackingFlags();
        List<Room> rooms = new XVector<Room>(theDriftroom);
        int limit = 50 - (super.getXLEVELLevel(mob));
        if (rooms.size() > 0)
            theTrail = CMLib.tracking().findTrailToAnyRoom(mobRoom, rooms, flags, limit);
        return theTrail;
    }

    @Override
    public void unInvoke() {
        Physical affected = this.affected;
        super.unInvoke();
        if (theDriftwood != null) {
            if ((theDriftwood.owner() instanceof Room)
                && (CMLib.map().roomLocation(affected) != CMLib.map().roomLocation(theDriftwood))) {
                theDriftwood.destroy();
            }
        }
        theDriftwood = null;
        theDriftroom = null;
        theTrail = null;
    }

    @Override
    protected TrackingLibrary.TrackingFlags getTrackingFlags() {
        TrackingLibrary.TrackingFlags flags;
        flags = CMLib.tracking().newFlags().plus(TrackingLibrary.TrackingFlag.WATERSURFACEONLY);
        return flags;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands == null)
            commands = new ArrayList<String>(1);
        if (commands.size() == 0)
            commands.add("driftwood");
        final Room R = mob.location();
        if (R == null)
            return false;
        if (!CMLib.flags().isWaterySurfaceRoom(R)) {
            mob.tell(L("You must be on the surface of the water to find driftwood."));
            return false;
        }
        return super.invoke(mob, commands, givenTarget, auto, asLevel);
    }
}
