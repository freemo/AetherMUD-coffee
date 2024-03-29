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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.MUDCmdProcessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Thief_FenceLoot extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Fence Loot");
    private static final String[] triggerStrings = I(new String[]{"FENCE", "FENCELOOT"});
    protected Map<Item, Ability> addBackMap = new HashMap<Item, Ability>();

    @Override
    public String ID() {
        return "Thief_FenceLoot";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
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
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_INFLUENTIAL;
    }

    @Override
    public boolean okMessage(Environmental myHost, CMMsg msg) {
        if ((msg.source() == affected)
            && (msg.targetMinor() == CMMsg.TYP_SELL)
            && (msg.tool() instanceof Item)) {
            Ability A = ((Item) msg.tool()).fetchEffect("Prop_PrivateProperty");
            if (A != null) {
                ((Item) msg.tool()).delEffect(A);
                addBackMap.put((Item) msg.tool(), A);
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 1) {
            mob.tell(L("You must specify an item to fence, and possibly a ShopKeeper (unless it is implied)."));
            return false;
        }

        commands.add(0, "SELL"); // will be instantly deleted by parseshopkeeper
        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, L("Fence what to whom?"));
        if (shopkeeper == null)
            return false;
        if (commands.size() == 0) {
            mob.tell(L("Fence what?"));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, shopkeeper, this, CMMsg.MSG_SPEAK, auto ? "" : L("<S-NAME> fence(s) stolen loot to <T-NAMESELF>."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                invoker = mob;
                addBackMap.clear();
                mob.addEffect(this);
                mob.recoverCharStats();
                commands.add(0, CMStrings.capitalizeAndLower("SELL"));
                mob.doCommand(commands, MUDCmdProcessor.METAFLAG_FORCED);
                commands.add(shopkeeper.name());
                mob.delEffect(this);
                for (Item I : addBackMap.keySet()) {
                    if (mob.isMine(I)) {
                        I.addEffect(addBackMap.get(I));
                    }
                }
                addBackMap.clear();
                mob.recoverCharStats();
            }
        } else
            beneficialWordsFizzle(mob, shopkeeper, L("<S-NAME> attempt(s) to fence stolen loot to <T-NAMESELF>, but make(s) <T-HIM-HER> too nervous."));

        // return whether it worked
        return success;
    }
}
