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
import com.syncleus.aethermud.game.Common.interfaces.TimeClock;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Chant_PiercingMoon extends Chant {
    private final static String localizedName = CMLib.lang().L("Piercing Moon");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Piercing Moon)");

    @Override
    public String ID() {
        return "Chant_PiercingMoon";
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
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONSUMMONING;
    }

    @Override
    public long flags() {
        return FLAG_WEATHERAFFECTING;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (affected == null)
            return;
        if (canBeUninvoked()) {
            final Room R = CMLib.map().roomLocation(affected);
            if ((R != null) && (CMLib.flags().isInTheGame(affected, true)))
                R.showHappens(CMMsg.MSG_OK_VISUAL, L("The piercing moon sets."));
        }
        super.unInvoke();

    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected == null)
            return false;
        if (affected instanceof Room) {
            final Room R = (Room) affected;
            if ((R.getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.DUSK)
                && (R.getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.NIGHT))
                unInvoke();
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Room R = mob.location();
            if ((R != null) && (!R.getArea().getClimateObj().canSeeTheMoon(R, null))) {
                if ((R.getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.DUSK)
                    && (R.getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.NIGHT))
                    return Ability.QUALITY_INDIFFERENT;
                if ((R.domainType() & Room.INDOORS) > 0)
                    return Ability.QUALITY_INDIFFERENT;
                if (R.fetchEffect(ID()) != null)
                    return Ability.QUALITY_INDIFFERENT;
                return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;
        if ((target.getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.DUSK)
            && (target.getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.NIGHT)) {
            mob.tell(L("You can only start this chant at night."));
            return false;
        }
        if ((target.domainType() & Room.INDOORS) > 0) {
            mob.tell(L("This chant only works outdoors."));
            return false;
        }

        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("This place is already under the piercing moon."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to the sky.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if (!mob.location().getArea().getClimateObj().canSeeTheStars(mob.location()))
                        mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The Moon pierces through the clouds!"));
                    else
                        mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The Moon brightens!"));
                    beneficialAffect(mob, target, asLevel, 0);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to the sky, but the magic fades."));
        // return whether it worked
        return success;
    }
}
