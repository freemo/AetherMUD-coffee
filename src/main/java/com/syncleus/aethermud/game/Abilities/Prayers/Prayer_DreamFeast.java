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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Prayer_DreamFeast extends Prayer {
    private final static String localizedName = CMLib.lang().L("Dream Feast");
    private final static String localizedDisplayText = CMLib.lang().L("(Dream Feast)");
    protected int ticksSleeping = 0;

    @Override
    public String ID() {
        return "Prayer_DreamFeast";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedDisplayText;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_RESTORATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        ticksSleeping = 0;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (CMLib.flags().isSleeping(affected)) {
            ticksSleeping++;
            if (ticksSleeping > 8) {
                if (affected instanceof MOB)
                    ((MOB) affected).tell(L("You have wonderful dreams of an abundant feasts and overflowing wines."));
            }
        } else if (ticksSleeping > 8) {

            if (affected instanceof MOB) {
                ((MOB) affected).tell(L("You wake up feeling full and content."));
                ((MOB) affected).curState().setHunger(CMProps.getIntVar(CMProps.Int.HUNGER_FULL));
                ((MOB) affected).curState().setThirst(CMProps.getIntVar(CMProps.Int.THIRST_FULL));
            }
            unInvoke();
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("^S<S-NAME> @x1 for <T-NAMESELF> to have dreams of feasts!^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                Ability A = beneficialAffect(mob, target, asLevel, 0);
                if (A != null)
                    A.setMiscText("");
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> @x1 for <T-NAMESELF> to have good dreams, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
