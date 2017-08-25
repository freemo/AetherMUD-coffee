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
package com.planet_ink.game.Abilities.Common;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.Directions;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class PlantLore extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Plant Lore");
    private static final String[] triggerStrings = I(new String[]{"PLANTLORE", "PSPECULATE"});
    protected boolean success = false;

    public PlantLore() {
        super();
        displayText = L("You are observing plant growth...");
        verb = L("observing plant growths");
    }

    @Override
    public String ID() {
        return "PlantLore";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if (tickUp == 6) {
                if (success == false) {
                    final StringBuffer str = new StringBuffer(L("Your growth observation attempt failed.\n\r"));
                    commonTell(mob, str.toString());
                    unInvoke();
                }
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if ((affected != null) && (affected instanceof MOB) && (!helping)) {
                final MOB mob = (MOB) affected;
                final Room room = mob.location();
                if ((success) && (!aborted) && (room != null)) {
                    if ((room.domainType() & Room.INDOORS) == 0) {
                        final StringBuffer str = new StringBuffer("");
                        final Vector<Room> V = new Vector<Room>();
                        TrackingLibrary.TrackingFlags flags;
                        flags = CMLib.tracking().newFlags()
                            .plus(TrackingLibrary.TrackingFlag.OPENONLY)
                            .plus(TrackingLibrary.TrackingFlag.AREAONLY)
                            .plus(TrackingLibrary.TrackingFlag.NOAIR);
                        int range = 2 + (getXLEVELLevel(mob) / 2) + super.getXMAXRANGELevel(mob);
                        CMLib.tracking().getRadiantRooms(room, V, flags, null, range, null);
                        for (int v = 0; v < V.size(); v++) {
                            final Room R = V.elementAt(v);
                            final int material = R.myResource() & RawMaterial.MATERIAL_MASK;
                            final int resource = R.myResource() & RawMaterial.RESOURCE_MASK;
                            if (!RawMaterial.CODES.IS_VALID(resource))
                                continue;
                            if ((material != RawMaterial.MATERIAL_VEGETATION)
                                && (resource != RawMaterial.RESOURCE_COTTON)
                                && (resource != RawMaterial.RESOURCE_HEMP)
                                && (resource != RawMaterial.RESOURCE_SAP)
                                && (material != RawMaterial.MATERIAL_WOODEN))
                                continue;
                            final String resourceStr = RawMaterial.CODES.NAME(resource);
                            if (R == room)
                                str.append(L("You think this spot would be good for @x1.\n\r", resourceStr.toLowerCase()));
                            else {
                                int isAdjacent = -1;
                                for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                                    final Room room2 = room.getRoomInDir(d);
                                    if (room2 == R)
                                        isAdjacent = d;
                                }
                                if (isAdjacent >= 0)
                                    str.append(L("There looks like @x1 @x2.\n\r", resourceStr.toLowerCase(), CMLib.directions().getInDirectionName(isAdjacent)));
                                else {
                                    int d = CMLib.tracking().radiatesFromDir(R, V);
                                    if (d >= 0) {
                                        d = Directions.getOpDirectionCode(d);
                                        str.append(L("There looks like @x1 far @x2.\n\r", resourceStr.toLowerCase(), CMLib.directions().getInDirectionName(d)));
                                    }
                                }

                            }
                        }
                        commonTell(mob, str.toString());
                    } else
                        commonTell(mob, L("You don't find any good plant life around here."));
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        verb = L("observing plant growth");
        success = false;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (proficiencyCheck(mob, 0, auto))
            success = true;
        final int duration = getDuration(45, mob, 1, 5);
        final CMMsg msg = CMClass.getMsg(mob, null, this, getActivityMessageType(), L("<S-NAME> start(s) observing the growth in this area."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
