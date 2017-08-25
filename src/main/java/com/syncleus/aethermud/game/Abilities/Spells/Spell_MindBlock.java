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


public class Spell_MindBlock extends Spell {

    private final static String localizedName = CMLib.lang().L("Mind Block");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mind Block)");
    int amountAbsorbed = 0;

    @Override
    public String ID() {
        return "Spell_MindBlock";
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
        if (canBeUninvoked()) {
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> anti-psionic field fades."));
        }

        super.unInvoke();

    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (!mob.amDead())
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, 0, false))) {
            boolean yep = (msg.targetMinor() == CMMsg.TYP_MIND);
            if ((!yep)
                && (msg.tool() instanceof Ability)) {
                final Ability A = (Ability) msg.tool();
                if (((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_ILLUSION)
                    || ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_ENCHANTMENT))
                    yep = true;
            }
            if (yep) {
                msg.source().tell(msg.source(), mob, null, L("<T-NAME> seem(s) unaffected by the enchantment."));
                return false;
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "A anti-psionic field envelopes <T-NAME>!" : "^S<S-NAME> invoke(s) an anti-psionic field of protection around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                amountAbsorbed = 0;
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke an anti-psionic field, but fail(s)."));

        return success;
    }
}
