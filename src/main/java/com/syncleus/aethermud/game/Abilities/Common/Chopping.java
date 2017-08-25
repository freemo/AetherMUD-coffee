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
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Chopping extends GatheringSkill {
    private final static String localizedName = CMLib.lang().L("Wood Chopping");
    private static final String[] triggerStrings = I(new String[]{"CHOP", "CHOPPING"});
    protected Item found = null;
    protected String foundShortName = "";

    public Chopping() {
        super();
        displayText = L("You are looking for a good tree...");
        verb = L("looking");
    }

    @Override
    public String ID() {
        return "Chopping";
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
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_GATHERINGSKILL;
    }

    @Override
    protected boolean allowedWhileMounted() {
        return false;
    }

    @Override
    public String supportedResourceString() {
        return "WOODEN";
    }

    protected int getDuration(MOB mob, int level) {
        return getDuration(40, mob, level, 15);
    }

    @Override
    protected int baseYield() {
        return 1;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if (tickUp == 6) {
                if (found != null) {
                    commonTell(mob, L("You have a good tree for @x1.", foundShortName));
                    displayText = L("You are chopping up @x1", foundShortName);
                    verb = L("chopping @x1", foundShortName);
                    playSound = "chopping.wav";
                } else {
                    final StringBuffer str = new StringBuffer(L("You can't seem to find any trees worth cutting around here.\n\r"));
                    final int d = lookingFor(RawMaterial.MATERIAL_WOODEN, mob.location());
                    if (d < 0)
                        str.append(L("You might try elsewhere."));
                    else
                        str.append(L("You might try @x1.", CMLib.directions().getInDirectionName(d)));
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
                if ((found != null) && (!aborted) && (mob.location() != null)) {
                    final CMMsg msg = CMClass.getMsg(mob, found, this, getCompletedActivityMessageType(), null);
                    msg.setValue(CMLib.dice().roll(1, 7, 3) * (baseYield() + abilityCode()));
                    if (mob.location().okMessage(mob, msg)) {
                        String s = "s";
                        if (msg.value() == 1)
                            s = "";
                        msg.modify(L("<S-NAME> manage(s) to chop up @x1 pound@x2 of @x3.", "" + msg.value(), s, foundShortName));
                        mob.location().send(mob, msg);
                        for (int i = 0; i < msg.value(); i++) {
                            final Item newFound = (Item) found.copyOf();
                            if (!dropAWinner(mob, newFound)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        bundling = false;
        if ((!auto)
            && (commands.size() > 0)
            && ((commands.get(0)).equalsIgnoreCase("bundle"))) {
            bundling = true;
            if (super.invoke(mob, commands, givenTarget, auto, asLevel))
                return super.bundle(mob, commands);
            return false;
        }

        verb = L("chopping");
        playSound = null;
        found = null;
        if (!confirmPossibleMaterialLocation(RawMaterial.MATERIAL_WOODEN, mob.location())) {
            commonTell(mob, L("You can't find anything to chop here."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final int resourceType = mob.location().myResource();
        if ((proficiencyCheck(mob, 0, auto))
            && ((resourceType & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_WOODEN)) {
            found = (Item) CMLib.materials().makeResource(resourceType, Integer.toString(mob.location().domainType()), false, null);
            foundShortName = "nothing";
            if (found != null)
                foundShortName = RawMaterial.CODES.NAME(found.material()).toLowerCase();
        }
        final int duration = getDuration(mob, 1);
        final CMMsg msg = CMClass.getMsg(mob, found, this, getActivityMessageType(), L("<S-NAME> start(s) looking for a good tree to chop."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            found = (Item) msg.target();
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
