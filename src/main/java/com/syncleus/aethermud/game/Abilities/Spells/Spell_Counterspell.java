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
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Counterspell extends Spell {

    private final static String localizedName = CMLib.lang().L("Counterspell");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Counterspell)");
    public boolean ticked = false;

    @Override
    public String ID() {
        return "Spell_Counterspell";
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
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("Your counterspell fades."));

        super.unInvoke();

    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (msg.targetMinor() == CMMsg.TYP_CAST_SPELL)
            && (msg.tool() instanceof Ability)
            && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
            && (invoker != null)
            && (!mob.amDead())
            && (CMLib.dice().rollPercentage() < (70 + (2 * ((mob.phyStats().level() + (2 * getXLEVELLevel(invoker()))) - msg.source().phyStats().level()))))) {
            mob.location().show(mob, msg.source(), CMMsg.MSG_OK_VISUAL, L("The barrier around <S-NAME> dispels the @x1 from <T-NAME>!", msg.tool().name()));
            tickDown = 0;
            return false;
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                final MOB victim = ((MOB) target).getVictim();
                if ((victim != null) && (CMLib.flags().domainAbilities(victim, Ability.ACODE_SPELL).size() == 0))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> feel(s) protected from spells.") : L("^S<S-NAME> invoke(s) a counterspell barrier around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                ticked = false;
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke a counterspell barrier, but fail(s)."));

        return success;
    }
}
