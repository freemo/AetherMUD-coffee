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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.NoSuchElementException;


public class Chant_DistantGrowth extends Chant {
    private final static String localizedName = CMLib.lang().L("Distant Growth");

    @Override
    public String ID() {
        return "Chant_DistantGrowth";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ROOMS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        if (commands.size() < 1) {
            mob.tell(L("Grow plants where?"));
            return false;
        }

        final String areaName = CMParms.combine(commands, 0).trim().toUpperCase();
        Room anyRoom = null;
        Room newRoom = null;
        try {
            final List<Room> rooms = CMLib.map().findRooms(CMLib.map().rooms(), mob, areaName, true, 10);
            for (final Room R : rooms) {
                anyRoom = R;
                if (((R.domainType() & Room.INDOORS) == 0)
                    && (R.domainType() != Room.DOMAIN_OUTDOORS_CITY)
                    && (R.domainType() != Room.DOMAIN_OUTDOORS_SPACEPORT)
                    && (!CMLib.flags().isWateryRoom(mob.location()))) {
                    newRoom = R;
                    break;
                }
            }
        } catch (final NoSuchElementException e) {
        }

        if (newRoom == null) {
            if (anyRoom == null)
                mob.tell(L("You don't know of a place called '@x1'.", CMParms.combine(commands, 0)));
            else if ((anyRoom.domainType() == Room.DOMAIN_OUTDOORS_CITY)
                || (anyRoom.domainType() == Room.DOMAIN_OUTDOORS_SPACEPORT))
                mob.tell(L("There IS such a place, but it is an overtrodden street, so your magic would fail."));
            else if (CMLib.flags().isWateryRoom(anyRoom))
                mob.tell(L("There IS such a place, but it is on or in the water, so your magic would fail."));
            else
                mob.tell(L("There IS such a place, but it is not outdoors, so your magic would fail."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), L("^S<S-NAME> chant(s) about a far away place.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Item newItem = new Chant_SummonPlants().buildPlant(mob, newRoom);
                mob.tell(L("You feel a distant connection with @x1", newItem.name()));
            }
        } else
            beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) about a far away place, but the magic fades."));

        // return whether it worked
        return success;
    }
}
