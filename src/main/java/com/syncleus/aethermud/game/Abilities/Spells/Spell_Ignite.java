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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Ignite extends Spell {

    private final static String localizedName = CMLib.lang().L("Ignite");

    @Override
    public String ID() {
        return "Spell_Ignite";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "Ignite";
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS | CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
    }

    public void ignite(MOB mob, Item I) {
        int durationOfBurn = 5;
        switch (I.material() & RawMaterial.MATERIAL_MASK) {
            case RawMaterial.MATERIAL_LEATHER:
                durationOfBurn = 20 + I.phyStats().weight();
                break;
            case RawMaterial.MATERIAL_CLOTH:
            case RawMaterial.MATERIAL_SYNTHETIC:
            case RawMaterial.MATERIAL_PAPER:
                durationOfBurn = 5 + I.phyStats().weight();
                break;
            case RawMaterial.MATERIAL_WOODEN:
                durationOfBurn = 40 + (I.phyStats().weight() * 2);
                break;
            default:
                switch (I.material()) {
                    case RawMaterial.RESOURCE_COAL:
                        durationOfBurn = 20 * (1 + I.phyStats().weight() * 3);
                        break;
                    case RawMaterial.RESOURCE_LAMPOIL:
                        durationOfBurn = 5 + I.phyStats().weight();
                        break;
                    default:
                        return;
                }
                break;
        }
        mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("@x1 ignites!", I.name()));
        final Ability B = CMClass.getAbility("Burning");
        if (B != null)
            B.invoke(mob, I, true, durationOfBurn);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;
        if ((!(target instanceof MOB))
            && (!(target instanceof Item))) {
            mob.tell(L("You can't ignite '@x1'!", target.name(mob)));
            return false;
        }

        if ((target instanceof Item) && (!CMLib.utensils().canBePlayerDestroyed(mob, (Item) target, false))) {
            mob.tell(L("You can't ignite '@x1'!", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> flares up!") : L("^S<S-NAME> evoke(s) a spell upon <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if (target instanceof Item)
                        ignite(mob, (Item) target);
                    else if (target instanceof MOB) {
                        final MOB mob2 = (MOB) target;
                        for (int i = 0; i < mob2.numItems(); i++) {
                            final Item I = mob2.getItem(i);
                            if ((I != null) && (I.container() == null))
                                ignite(mob2, I);
                        }
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> evoke(s) at <T-NAMESELF>, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
