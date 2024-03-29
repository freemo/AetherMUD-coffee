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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_DeathsDoor extends Prayer {
    private final static String localizedName = CMLib.lang().L("Deaths Door");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Deaths Door)");

    @Override
    public String ID() {
        return "Prayer_DeathsDoor";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
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
    public boolean okMessage(Environmental host, CMMsg msg) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            final Room startRoom = mob.getStartRoom();
            if (msg.amISource(mob)
                && (msg.sourceMinor() == CMMsg.TYP_DEATH)
                && (startRoom != null)) {
                if (mob.fetchAbility("Dueling") != null)
                    return super.okMessage(host, msg);
                final Room oldRoom = mob.location();
                mob.resetToMaxState();
                oldRoom.show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> pulled back from death's door!"));
                startRoom.bringMobHere(mob, false);
                unInvoke();
                for (int a = mob.numEffects() - 1; a >= 0; a--) // personal effects
                {
                    final Ability A = mob.fetchEffect(a);
                    if (A != null)
                        A.unInvoke();
                }
                if ((oldRoom != startRoom) && oldRoom.isInhabitant(mob) && startRoom.isInhabitant(mob))
                    oldRoom.delInhabitant(mob); // hopefully unnecessary
                return false;
            }
        }
        return super.okMessage(host, msg);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your deaths door protection fades."));
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) guarded at deaths door!") : L("^S<S-NAME> @x1 for <T-NAME> to be guarded at deaths door!^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for <T-NAMESELF>, but there is no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
