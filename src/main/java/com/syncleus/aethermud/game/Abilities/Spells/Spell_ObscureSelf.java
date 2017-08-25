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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_ObscureSelf extends Spell {

    private final static String localizedName = CMLib.lang().L("Obscure Self");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Obscure Self)");
    private final static String[][] stuff =
        {
            {"<S-NAME>", "<T-NAME>", "someone"},
            {"<S-HIS-HER>", "<T-HIS-HER>", "his or her"},
            {"<S-HIM-HER>", "<T-HIM-HER>", "him or her"},
            {"<S-NAMESELF>", "<T-NAMESELF>", "someone"},
            {"<S-HE-SHE>", "<T-HE-SHE>", "he or she"},
            {"<S-YOUPOSS>", "<T-YOUPOSS>", "someone's"},
            {"<S-HIM-HERSELF>", "<T-HIM-HERSELF>", "him or herself"},
            {"<S-HIS-HERSELF>", "<T-HIS-HERSELF>", "his or herself"}
        };

    @Override
    public String ID() {
        return "Spell_ObscureSelf";
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
        return Ability.QUALITY_OK_SELF;
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
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        String othersMessage = msg.othersMessage();
        String sourceMessage = msg.sourceMessage();
        String targetMessage = msg.targetMessage();
        boolean somethingsChanged = false;
        int x = 0;
        if ((msg.amITarget(mob)) && (msg.targetMinor() != CMMsg.TYP_DAMAGE)) {
            if ((!msg.amISource(mob))
                && ((msg.targetMinor() == CMMsg.TYP_LOOK)
                || (msg.targetMinor() == CMMsg.TYP_EXAMINE)
                || (msg.targetMinor() == CMMsg.TYP_READ))) {
                msg.source().tell(L("He or she is too vague to make out any details."));
                return false;
            }

            if (othersMessage != null) {
                for (final String[] element : stuff) {
                    x = othersMessage.indexOf(element[1]);
                    while (x >= 0) {
                        somethingsChanged = true;
                        othersMessage = othersMessage.substring(0, x) + element[2] + othersMessage.substring(x + (element[1]).length());
                        x = othersMessage.indexOf(element[1]);
                    }
                }
            }
            if ((!msg.amISource(mob)) && (sourceMessage != null)) {
                for (final String[] element : stuff) {
                    x = sourceMessage.indexOf(element[1]);
                    while (x >= 0) {
                        somethingsChanged = true;
                        sourceMessage = sourceMessage.substring(0, x) + element[2] + sourceMessage.substring(x + (element[1]).length());
                        x = sourceMessage.indexOf(element[1]);
                    }
                }
            }
        }
        if (msg.amISource(mob)) {
            if (othersMessage != null) {
                for (final String[] element : stuff) {
                    x = othersMessage.indexOf(element[0]);
                    while (x >= 0) {
                        somethingsChanged = true;
                        othersMessage = othersMessage.substring(0, x) + element[2] + othersMessage.substring(x + (element[0]).length());
                        x = othersMessage.indexOf(element[0]);
                    }
                }
            }
            if ((!msg.amITarget(mob)) && (targetMessage != null)) {
                for (final String[] element : stuff) {
                    x = targetMessage.indexOf(element[0]);
                    while (x >= 0) {
                        somethingsChanged = true;
                        targetMessage = targetMessage.substring(0, x) + element[2] + targetMessage.substring(x + (element[0]).length());
                        x = targetMessage.indexOf(element[0]);
                    }
                }
            }
        }
        if (somethingsChanged)
            msg.modify(msg.source(), msg.target(), msg.tool(), msg.sourceCode(), sourceMessage, msg.targetCode(), targetMessage, msg.othersCode(), othersMessage);
        return true;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();

        if (canBeUninvoked()) {
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> seem(s) a bit less obscure."));
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already obscure."));
            return false;
        }

        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("^S<T-NAME> become(s) obscure!") : L("^S<S-NAME> whisper(s) to <S-HIM-HERSELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> whisper(s) to <S-HIM-HERSELF>, but nothing happens."));
        // return whether it worked
        return success;
    }
}
