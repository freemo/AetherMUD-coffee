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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Chant_SummonInsects extends Chant {
    private final static String localizedName = CMLib.lang().L("Summon Insects");
    private final static String localizedStaticDisplay = CMLib.lang().L("(In a swarm of insects)");
    Room castingLocation = null;

    @Override
    public String ID() {
        return "Chant_SummonInsects";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(5);
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public long flags() {
        return Ability.FLAG_SUMMONING;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_MOB)
            && (affected instanceof MOB)) {
            final MOB M = (MOB) affected;
            if (M.location() != castingLocation)
                unInvoke();
            else if ((!M.amDead()) && (M.location() != null)) {
                final MOB invoker = (invoker() != null) ? invoker() : M;
                CMLib.combat().postDamage(invoker, M, this, CMLib.dice().roll(1, 3 + super.getXLEVELLevel(invoker), 0), CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, -1, L("<T-NAME> <T-IS-ARE> stung by the swarm!"));
                CMLib.combat().postRevengeAttack(M, invoker);
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked())
            if ((!mob.amDead()) && (mob.location() != null))
                mob.location().show(mob, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> manage(s) to escape the insect swarm!"));
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            final Room R = mob.location();
            if (R != null) {
                if ((R.domainType() & Room.INDOORS) > 0)
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (((mob.location().domainType() & Room.INDOORS) > 0) && (!auto)) {
            mob.tell(L("You must be outdoors for this chant to work."));
            return false;
        }

        final Set<MOB> h = properTargets(mob, givenTarget, auto);

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (h == null) {
                mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("A swarm of stinging insects appear, then flutter away!") : L("^S<S-NAME> chant(s) into the sky.  A swarm of stinging insects appear.  Finding no one to sting, they flutter away.^?"));
                return false;
            }
            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("A swarm of stinging insects appear, then flutter away!") : L("^S<S-NAME> chant(s) into the sky.  A swarm of stinging insects appears and attacks!^?"))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), null);
                    if ((mob.location().okMessage(mob, msg))
                        && (target.fetchEffect(this.ID()) == null)) {
                        mob.location().send(mob, msg);
                        if ((msg.value() <= 0) && (target.location() == mob.location())) {
                            castingLocation = mob.location();
                            success = maliciousAffect(mob, target, asLevel, ((mob.phyStats().level() + (2 * getXLEVELLevel(mob))) * 10), -1) != null;
                            target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> become(s) enveloped by the swarm of stinging insects!"));
                        }
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> chant(s), but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
