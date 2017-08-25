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
package com.planet_ink.game.Abilities.Thief;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Thief_PowerGrab extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Power Grab");
    private static final String[] triggerStrings = I(new String[]{"POWERGRAB"});

    @Override
    public String ID() {
        return "Thief_PowerGrab";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public double castingTime(final MOB mob, final List<String> cmds) {
        return CMProps.getSkillActionCost(ID(), 0.0);
    }

    @Override
    public double combatCastingTime(final MOB mob, final List<String> cmds) {
        return CMProps.getSkillCombatActionCost(ID(), 0.0);
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALING;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item possibleContainer = possibleContainer(mob, commands, true, Wearable.FILTER_UNWORNONLY);
        final Item target = super.getTarget(mob, mob.location(), givenTarget, possibleContainer, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (!success)
            beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to power grab something and fail(s)."));
        else {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_DELICATE_SMALL_HANDS_ACT | CMMsg.MASK_MAGIC, auto ? "" : L("^S<S-NAME> carefully attempt(s) to acquire <T-NAME>^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final int level = target.basePhyStats().level();
                final int level2 = target.phyStats().level();
                target.basePhyStats().setLevel(1);
                target.phyStats().setLevel(1);
                CMLib.commands().postGet(mob, possibleContainer, target, false);
                target.basePhyStats().setLevel(level);
                target.phyStats().setLevel(level2);
                target.recoverPhyStats();
                mob.location().recoverRoomStats();
            }
        }
        return success;
    }
}
