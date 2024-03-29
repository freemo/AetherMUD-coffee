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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Chant_RustCurse extends Chant {
    private final static String localizedName = CMLib.lang().L("Rust Curse");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Rust Curse)");

    @Override
    public String ID() {
        return "Chant_RustCurse";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_DEEPMAGIC;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
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
    public void unInvoke() {
        MOB M = null;
        if (affected instanceof MOB)
            M = (MOB) affected;
        super.unInvoke();
        if ((canBeUninvoked()) && (M != null) && (!M.amDead()))
            M.tell(L("You don't feel so damp any more."));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected instanceof MOB) {
            boolean goodChoices = false;
            final Vector<Item> choices = new Vector<Item>();
            final MOB mob = (MOB) affected;
            for (int i = 0; i < mob.numItems(); i++) {
                final Item I = mob.getItem(i);
                if ((I != null) && (I.subjectToWearAndTear())
                    && (((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_METAL)
                    || ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_MITHRIL))) {
                    if (!I.amWearingAt(Wearable.IN_INVENTORY)) {
                        goodChoices = true;
                        choices.addElement(I);
                    } else if (!goodChoices)
                        choices.addElement(I);
                }
            }
            if (goodChoices)
                for (int i = choices.size() - 1; i >= 0; i--) {
                    if (choices.elementAt(i).amWearingAt(Wearable.IN_INVENTORY))
                        choices.removeElementAt(i);
                }
            if (choices.size() > 0) {
                final Item I = choices.elementAt(CMLib.dice().roll(1, choices.size(), -1));
                if (((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL)
                    || (CMLib.dice().rollPercentage() < 10))
                    CMLib.combat().postItemDamage(mob, I, null, 1, CMMsg.TYP_ACID, "<T-NAME> rusts!");
            }
        }

        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) at <T-NAME> rustily!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    maliciousAffect(mob, target, asLevel, 0, -1);
                    target.tell(L("You feel damp!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) at <T-NAME>, but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
