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
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Food;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_Mold extends Chant {
    private final static String localizedName = CMLib.lang().L("Mold");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mold)");

    @Override
    public String ID() {
        return "Chant_Mold";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if ((affected == null) || (!(affected instanceof Item)))
            return;
        final Item item = (Item) affected;
        super.unInvoke();

        if (canBeUninvoked())
            item.destroy();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;
        if (((target instanceof Item) && (!(target instanceof Food)))
            || (target instanceof Room)
            || (target instanceof Exit)) {
            mob.tell(L("You can't cast this on @x1.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if (target instanceof Item) {
                        final Ability A = CMClass.getAbility("Disease_Lockjaw");
                        if (A != null) {
                            A.setInvoker(mob);
                            target.addNonUninvokableEffect(A);
                        }
                        maliciousAffect(mob, target, asLevel, (CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH) * 3), -1);
                    } else if (target instanceof MOB)
                        for (int i = 0; i < ((MOB) target).numItems(); i++) {
                            final Item I = ((MOB) target).getItem(i);
                            if ((I != null) && (I instanceof Food)) {
                                final Ability A = CMClass.getAbility("Disease_Lockjaw");
                                if (A != null) {
                                    A.setInvoker(mob);
                                    I.addNonUninvokableEffect(A);
                                }
                                maliciousAffect(mob, I, asLevel, (CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH) * 3), -1);
                            }
                        }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades."));
        // return whether it worked
        return success;
    }
}
