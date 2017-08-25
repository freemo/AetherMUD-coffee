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
package com.planet_ink.game.Abilities.Paladin;

import com.planet_ink.game.Abilities.StdAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.MUDCmdProcessor;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Paladin_CommandHorse extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Command Horse");
    private static final String[] triggerStrings = I(new String[]{"COMMANDHORSE"});

    @Override
    public String ID() {
        return "Paladin_CommandHorse";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int enchantQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((!auto) && (!(CMLib.flags().isGood(mob)))) {
            mob.tell(L("Your alignment has alienated you from your god."));
            return false;
        }
        final List<String> V = new Vector<String>();
        if (commands.size() > 0) {
            V.add(commands.get(0));
            commands.remove(0);
        }

        final MOB target = getTarget(mob, V, givenTarget);
        if (target == null)
            return false;

        if (commands.size() == 0) {
            if (mob.isMonster())
                commands.add("FLEE");
            else {
                if (V.size() > 0)
                    mob.tell(L("Command @x1 to do what?", V.get(0)));
                return false;
            }
        }

        if (!target.charStats().getMyRace().racialCategory().equals("Equine")) {
            mob.tell(L("@x1 is not a horse!", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalSpeakCode(mob, target, auto), auto ? "" : L("^S<S-NAME> command(s) <T-NAMESELF> to '@x1'.^?", CMParms.combine(commands, 0)));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | CMMsg.MASK_SOUND | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            final CMMsg omsg = CMClass.getMsg(mob, target, null, CMMsg.MSG_ORDER, null);
            if ((mob.location().okMessage(mob, msg))
                && ((mob.location().okMessage(mob, msg2)))
                && (mob.location().okMessage(mob, omsg))) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().send(mob, msg2);
                    mob.location().send(mob, omsg);
                    if ((msg2.value() <= 0) && (omsg.sourceMinor() == CMMsg.TYP_ORDER)) {
                        invoker = mob;
                        target.makePeace(true);
                        target.enqueCommand(commands, MUDCmdProcessor.METAFLAG_FORCED | MUDCmdProcessor.METAFLAG_ORDER, 0);
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to command <T-NAMESELF>, but it definitely didn't work."));

        // return whether it worked
        return success;
    }
}
