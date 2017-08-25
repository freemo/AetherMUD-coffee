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
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_LedFoot extends Spell {

    private final static String localizedName = CMLib.lang().L("Lead Foot");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Lead Foot)");

    @Override
    public String ID() {
        return "Spell_LedFoot";
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your feet feel lighter."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if (msg.amISource(mob)) {
            switch (msg.sourceMinor()) {
                case CMMsg.TYP_ENTER:
                case CMMsg.TYP_ADVANCE:
                case CMMsg.TYP_LEAVE:
                case CMMsg.TYP_FLEE:
                    if ((!(msg.tool() instanceof Ability))
                        || (((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_CHANT)
                        && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
                        && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
                        && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SONG))) {
                        mob.tell(L("Your feet are just too heavy to move."));
                        return false;
                    }
                    break;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if ((!auto) && (target.charStats().getBodyPart(Race.BODY_FOOT) == 0)) {
            mob.tell(L("@x1 has no feet, and would not be affected.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^SYou invoke a heavy spell into <T-NAME>s feet.^?"), verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> invoke(s) a heavy spell into your feet.^?"), CMMsg.MSG_CAST_ATTACK_VERBAL_SPELL, auto ? "" : L("^S<S-NAME> invokes a heavy spell into <T-NAME>s feet.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> feet seem as heavy as lead!"));
                    success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> cast(s) a spell at <T-NAMESELF>, but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
