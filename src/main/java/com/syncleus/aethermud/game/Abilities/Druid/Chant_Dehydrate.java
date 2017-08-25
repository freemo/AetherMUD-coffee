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
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Drink;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_Dehydrate extends Chant {
    private final static String localizedName = CMLib.lang().L("Dehydrate");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Dehydrate)");

    @Override
    public String ID() {
        return "Chant_Dehydrate";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if (target instanceof MOB) {
                        ((MOB) target).curState().adjThirst(-150 - ((mob.phyStats().level() + (2 * getXLEVELLevel(mob))) * 100), ((MOB) target).maxState().maxThirst(((MOB) target).baseWeight()));
                        mob.location().show(((MOB) target), null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> feel(s) incredibly thirsty!"));
                    } else if (target instanceof Item) {
                        if (target instanceof Container) {
                            final List<Item> V = ((Container) target).getDeepContents();
                            for (int i = 0; i < V.size(); i++) {
                                final Item I = V.get(i);
                                if (I instanceof Drink) {
                                    if (((Drink) I).liquidRemaining() < 10000)
                                        ((Drink) I).setLiquidRemaining(0);
                                    if (I instanceof RawMaterial)
                                        I.destroy();
                                }
                            }
                            if (target instanceof Drink) {
                                if (((Drink) target).liquidRemaining() < 10000)
                                    ((Drink) target).setLiquidRemaining(0);
                            }
                            if (target instanceof RawMaterial)
                                ((Item) target).destroy();
                        }
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));
        // return whether it worked
        return success;
    }
}
