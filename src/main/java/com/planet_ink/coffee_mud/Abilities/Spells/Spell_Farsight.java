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
package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Spell_Farsight extends Spell {

    private final static String localizedName = CMLib.lang().L("Farsight");

    @Override
    public String ID() {
        return "Spell_Farsight";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        boolean success = proficiencyCheck(mob, 0, auto);
        if (!success)
            this.beneficialVisualFizzle(mob, null, L("<S-NAME> get(s) a far off look, but the spell fizzles."));
        else {
            final CMMsg msg = CMClass.getMsg(mob, null, null, verbalCastCode(mob, null, auto), L("^S<S-NAME> get(s) a far off look in <S-HIS-HER> eyes.^?"));
            int limit = mob.phyStats().level() / 5;
            if (limit < 0)
                limit = 1;
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                Room thatRoom = mob.location();
                if (commands.size() == 0) {
                    for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                        Exit exit = thatRoom.getExitInDir(d);
                        Room room = thatRoom.getRoomInDir(d);

                        if ((exit != null) && (room != null) && (CMLib.flags().canBeSeenBy(exit, mob) && (exit.isOpen()))) {
                            mob.tell("^D" + CMStrings.padRight(CMLib.directions().getDirectionName(d), 5) + ":^.^N ^d" + exit.viewableText(mob, room) + "^N");
                            exit = room.getExitInDir(d);
                            room = room.getRoomInDir(d);
                            if ((exit != null) && (room != null) && (CMLib.flags().canBeSeenBy(exit, mob) && (exit.isOpen()))) {
                                mob.tell(CMStrings.padRight("", 5) + ":^N ^d" + exit.viewableText(mob, room) + "^N");
                                exit = room.getExitInDir(d);
                                room = room.getRoomInDir(d);
                                if ((exit != null) && (room != null) && (CMLib.flags().canBeSeenBy(exit, mob) && (exit.isOpen()))) {
                                    mob.tell(CMStrings.padRight("", 5) + ":^N ^d" + exit.viewableText(mob, room) + "^N");
                                }
                            }
                        }
                    }
                } else
                    while (commands.size() > 0) {
                        final String whatToOpen = commands.get(0);
                        final int dirCode = CMLib.directions().getGoodDirectionCode(whatToOpen);
                        if (limit <= 0) {
                            mob.tell(L("Your sight has reached its limit."));
                            success = true;
                            break;
                        } else if (dirCode < 0) {
                            mob.tell(L("\n\r'@x1' is not a valid direction.", whatToOpen));
                            commands.clear();
                            success = false;
                        } else {
                            final Exit exit = thatRoom.getExitInDir(dirCode);
                            final Room room = thatRoom.getRoomInDir(dirCode);

                            if ((exit == null) || (room == null) || (!CMLib.flags().canBeSeenBy(exit, mob)) || (!exit.isOpen())) {
                                mob.tell(L("\n\rSomething has obstructed your vision."));
                                success = false;
                                commands.clear();
                            } else {
                                commands.remove(0);
                                thatRoom = room;
                                limit--;
                                mob.tell(L("\n\r"));
                                final CMMsg msg2 = CMClass.getMsg(mob, thatRoom, CMMsg.MSG_LOOK, null);
                                thatRoom.executeMsg(mob, msg2);
                            }
                        }
                    }
            }
        }

        return success;
    }
}
