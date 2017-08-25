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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Spell_ManaShield extends Spell {

    private final static String localizedName = CMLib.lang().L("Mana Shield");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mana Shield)");

    @Override
    public String ID() {
        return "Spell_ManaShield";
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
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
    }

    protected double protection() {
        return 0.5;
    }

    protected String adjective() {
        return " a";
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("The mana shield around <S-NAME> fades."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)) {
            int recovery = (int) Math.round(CMath.mul((msg.value()), protection()));
            if (recovery > mob.curState().getMana())
                recovery = mob.curState().getMana();
            if (recovery > 0) {
                msg.setValue(msg.value() - recovery);
                mob.curState().adjMana(-recovery, mob.maxState());
            }
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Physical target = mob;
        if ((auto) && (givenTarget != null))
            target = givenTarget;
        boolean oldOne = false;
        for (final Enumeration<Ability> a = target.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if (A instanceof Spell_ManaShield)
                oldOne = true;
        }
        if (oldOne) {
            mob.tell(mob, target, null, L("<T-NAME> <T-IS-ARE> already affected by @x1.", name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> invoke(s)@x1 protective shield.^?", adjective()));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("@x1 protective aura of mana surrounds <T-NAME>.", CMStrings.capitalizeAndLower(adjective()).trim()));
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke@x1 protective shield, but mess(es) up.", adjective()));

        // return whether it worked
        return success;
    }
}
