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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.Directions;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Speculate extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Speculating");
    private static final String[] triggerStrings = I(new String[]{"SPECULATE", "SPECULATING"});
    protected boolean success = false;

    public Speculate() {
        super();
        displayText = L("You are speculating...");
        verb = L("speculating");
    }

    @Override
    public String ID() {
        return "Speculate";
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
                    final StringBuffer str = new StringBuffer(L("Your speculate attempt failed.\n\r"));
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
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                final Room room = mob.location();
                if ((success) && (!aborted) && (room != null)) {
                    int resource = room.myResource() & RawMaterial.RESOURCE_MASK;
                    if (RawMaterial.CODES.IS_VALID(resource)) {
                        final StringBuffer str = new StringBuffer("");
                        String resourceStr = RawMaterial.CODES.NAME(resource);
                        str.append(L("You think this spot would be good for @x1.\n\r", resourceStr.toLowerCase()));
                        for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                            final Room room2 = room.getRoomInDir(d);
                            if ((room2 != null)
                                && (room.getExitInDir(d) != null)
                                && (room.getExitInDir(d).isOpen())) {
                                resource = room2.myResource() & RawMaterial.RESOURCE_MASK;
                                if (RawMaterial.CODES.IS_VALID(resource)) {
                                    resourceStr = RawMaterial.CODES.NAME(resource);
                                    str.append(L("There looks like @x1 @x2.\n\r", resourceStr.toLowerCase(), CMLib.directions().getInDirectionName(d)));
                                }
                            }
                        }
                        commonTell(mob, str.toString());
                    } else
                        commonTell(mob, L("You don't find any good resources around here."));
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        verb = L("speculating");
        success = false;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (proficiencyCheck(mob, 0, auto))
            success = true;
        final int duration = getDuration(45, mob, 1, 10);
        final CMMsg msg = CMClass.getMsg(mob, null, this, getActivityMessageType(), L("<S-NAME> start(s) speculating on this area."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
