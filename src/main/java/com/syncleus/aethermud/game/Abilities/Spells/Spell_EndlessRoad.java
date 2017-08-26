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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_EndlessRoad extends Spell {

    private final static String localizedName = CMLib.lang().L("Endless Road");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Endless Road)");

    @Override
    public String ID() {
        return "Spell_EndlessRoad";
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
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked())
            mob.tell(L("You feel like you are finally getting somewhere."));
        CMLib.commands().postStand(mob, true);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return super.okMessage(myHost, msg);
        final MOB mob = (MOB) affected;
        if (msg.amISource(mob)
            && (mob.location() != null)
            && (msg.targetMinor() == CMMsg.TYP_ENTER)
            && (msg.target() instanceof Room)
            && (msg.target() != mob.location())) {
            msg.modify(msg.source(),
                mob.location(),
                msg.tool(),
                msg.sourceCode(),
                msg.sourceMessage(),
                msg.targetCode(),
                msg.targetMessage(),
                msg.othersCode(),
                msg.othersMessage());
        }

        return super.okMessage(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if ((mob.isMonster()) && (mob.isInCombat()))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> incant(s) to <T-NAMESELF>!^?"));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg2);
                if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                    success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                    if (success)
                        if (target.location() == mob.location())
                            target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> seem(s) lost!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> incant(s) to <T-NAMESELF>, but the spell fizzles"));

        // return whether it worked
        return success;
    }
}