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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.NoSuchElementException;


public class Chant_DistantWindColor extends Chant {
    private final static String localizedName = CMLib.lang().L("Distant Wind Color");

    @Override
    public String ID() {
        return "Chant_DistantWindColor";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
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
            mob.tell(L("Discern the wind color where?"));
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
                    && (!CMLib.flags().isWateryRoom(R))) {
                    newRoom = R;
                    break;
                }
            }
        } catch (final NoSuchElementException e) {
        }

        if (newRoom == null) {
            if (anyRoom == null)
                mob.tell(L("You don't know of a place called '@x1'.", CMParms.combine(commands, 0)));
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
                final String msg2 = new Chant_WindColor().getWindColor(mob, newRoom);
                if (msg2.length() == 0)
                    mob.tell(L("The winds at @x1 are clear.", newRoom.displayText(mob)));
                else
                    mob.tell(L("The winds at @x1 are @x2.", newRoom.displayText(mob), msg2));
            }
        } else
            beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) about a far away place, but the magic fades."));

        // return whether it worked
        return success;
    }
}
