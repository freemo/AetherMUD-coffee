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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Food;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class MeatCuring extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("MeatCuring");
    private static final String[] triggerStrings = I(new String[]{"MEATCURING", "MEATCURE", "MCURING", "MCURE"});
    protected Item found = null;
    protected boolean success = false;

    public MeatCuring() {
        super();
        displayText = L("You are curing...");
        verb = L("curing");
    }

    @Override
    public String ID() {
        return "MeatCuring";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String supportedResourceString() {
        return "MISC";
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_CALLIGRAPHY;
    }

    @Override
    protected boolean canBeDoneSittingDown() {
        return true;
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if ((affected != null)
                && (affected instanceof MOB)
                && (!aborted)
                && (!helping)
                && (found != null)) {
                final MOB mob = (MOB) affected;
                Ability oldA = found.fetchEffect("Prayer_Purify");
                if ((oldA == null)
                    || (oldA.canBeUninvoked() && success)) {
                    if (oldA != null)
                        oldA.unInvoke();
                    final Ability A = CMClass.findAbility("Prayer_Purify");
                    if (success)
                        found.addNonUninvokableEffect(A);
                    else
                        A.startTickDown(mob, found, 20);
                } else if (mob != null)
                    commonEmote(mob, L("<S-NAME> mess(es) up curing @x1.", found.name()));
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        if (commands.size() < 1) {
            commonTell(mob, L("You must specify what meat you want to cure."));
            return false;
        }
        String what = commands.get(0);
        Item target = super.getTarget(mob, null, givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            commonTell(mob, L("You don't seem to have a '@x1'.", what));
            return false;
        }
        commands.remove(commands.get(0));

        if (((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_FLESH)
            || (!(target instanceof Food))) {
            commonTell(mob, L("You can't cure that."));
            return false;
        }

        if (target.fetchEffect("Poison_Rotten") != null) {
            commonTell(mob, L("That's already rotten and can't be cured."));
            return false;
        }

        if (target.fetchEffect("Prayer_Purify") != null) {
            commonTell(mob, L("That's already been cured."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        verb = L("curing @x1", target.name());
        displayText = L("You are @x1", verb);
        found = target;
        success = true;
        if (!proficiencyCheck(mob, 0, auto))
            success = false;
        final int duration = getDuration(1 + (target.phyStats().weight() * 2), mob, 1, 3);
        final CMMsg msg = CMClass.getMsg(mob, target, this, getActivityMessageType(), L("<S-NAME> start(s) curing <T-NAME>."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
