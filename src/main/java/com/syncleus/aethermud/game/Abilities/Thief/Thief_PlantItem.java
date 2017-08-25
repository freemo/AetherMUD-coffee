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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_PlantItem extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Plant Item");
    private static final String[] triggerStrings = I(new String[]{"PLANTITEM"});
    public int code = 0;

    @Override
    public String ID() {
        return "Thief_PlantItem";
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
        return Ability.CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALING;
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
    public int abilityCode() {
        return code;
    }

    @Override
    public void setAbilityCode(int newCode) {
        code = newCode;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("What would you like to plant on whom?"));
            return false;
        }
        final MOB target = mob.location().fetchInhabitant(commands.get(commands.size() - 1));
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", commands.get(commands.size() - 1)));
            return false;
        }
        if (target == mob) {
            mob.tell(L("You cannot plant anything on yourself!"));
            return false;
        }
        commands.remove(commands.size() - 1);

        final Item item = super.getTarget(mob, null, givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (item == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + abilityCode() + (getXLEVELLevel(mob) * 2));
        if (levelDiff < 0)
            levelDiff = 0;
        levelDiff *= 5;
        final boolean success = proficiencyCheck(mob, -levelDiff, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, item, CMMsg.MSG_GIVE, L("<S-NAME> plant(s) <O-NAME> on <T-NAMESELF>."), CMMsg.MASK_ALWAYS | CMMsg.MSG_GIVE, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_GIVE, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (target.isMine(item)) {
                    item.basePhyStats().setDisposition(item.basePhyStats().disposition() | PhyStats.IS_HIDDEN);
                    item.recoverPhyStats();
                }
            }
        } else
            beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to plant @x1 on <T-NAMESELF>, but fail(s).", item.name()));
        return success;
    }
}
