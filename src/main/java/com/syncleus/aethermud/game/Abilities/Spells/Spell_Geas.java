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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Libraries.interfaces.SlaveryLibrary;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Spell_Geas extends Spell {

    private final static String localizedName = CMLib.lang().L("Geas");
    public SlaveryLibrary.GeasSteps STEPS = null;

    @Override
    public String ID() {
        return "Spell_Geas";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return L("(Geas to " + text() + ")");
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(5);
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();
        if (canBeUninvoked()) {
            if ((STEPS == null) || (STEPS.size() == 0) || (STEPS.isDone()))
                mob.tell(L("You have completed your geas."));
            else
                mob.tell(L("You have been released from your geas."));

            if ((mob.isMonster())
                && (!mob.amDead())
                && (mob.location() != null)
                && (mob.location() != mob.getStartRoom()))
                CMLib.tracking().wanderAway(mob, true, true);
        }
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (msg.amITarget(mob)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && ((msg.value()) > 0))
            CMLib.combat().postPanic(mob, msg);
        if ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
            && (STEPS != null)
            && (msg.sourceMessage() != null)
            && ((msg.target() == null) || (msg.target() instanceof MOB))
            && (msg.sourceMessage().length() > 0)) {
            final String str = CMStrings.getSayFromMessage(msg.sourceMessage());
            if (str != null)
                STEPS.sayResponse(msg.source(), (MOB) msg.target(), str);
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);
        if ((tickID == Tickable.TICKID_MOB) && (STEPS != null)) {
            if ((STEPS != null) && ((STEPS.size() == 0) || (STEPS.isDone()))) {
                if (((MOB) ticking).isInCombat())
                    return true; // let them finish fighting.
                unInvoke();
                return false;
            }
            STEPS.step();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.isMonster()) {
            mob.location().show(mob, null, CMMsg.MSG_NOISE, L("<S-NAME> sigh(s)."));
            CMLib.commands().postSay(mob, null, L("You know, if I had any ambitions, I would put the geas on myself!"), false, false);
            return false;
        }

        if (commands.size() < 2) {
            mob.tell(L("You need to specify a target creature, and a geas to place on them."));
            return false;
        }
        final Vector<String> name = CMParms.parse(commands.get(0));
        commands.remove(commands.get(0));
        final MOB target = getTarget(mob, name, givenTarget);
        if (target == null)
            return false;
        if (target.charStats().getStat(CharStats.STAT_INTELLIGENCE) < 5) {
            mob.tell(L("@x1 is too stupid to understand the instructions!", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> place(s) a powerful geas upon <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                STEPS = CMLib.slavery().processRequest(mob, target, CMParms.combine(commands, 0));
                if ((STEPS == null) || (STEPS.size() == 0)) {
                    STEPS = null;
                    target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> look(s) confused."));
                    return false;
                }
                setMiscText(CMParms.combine(commands, 0));
                if (maliciousAffect(mob, target, asLevel, 2000, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0)) != null) {
                    target.makePeace(true);
                    if (mob.getVictim() == target)
                        mob.makePeace(true);
                    if (target.location() == mob.location()) {
                        for (int m = 0; m < target.location().numInhabitants(); m++) {
                            final MOB M = target.location().fetchInhabitant(m);
                            if ((M != null) && (M.getVictim() == target))
                                M.makePeace(true);
                        }
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to place a geas on <T-NAMESELF>, but fails."));

        // return whether it worked
        return success;
    }
}
