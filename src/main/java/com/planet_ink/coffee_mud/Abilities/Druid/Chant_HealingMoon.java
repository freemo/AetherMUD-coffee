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
package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;


public class Chant_HealingMoon extends Chant {
    private final static String localizedName = CMLib.lang().L("Healing Moon");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Healing Moon)");

    @Override
    public String ID() {
        return "Chant_HealingMoon";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONALTERING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HEALINGMAGIC;
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof Room)
                ((Room) affected).showHappens(CMMsg.MSG_OK_VISUAL, L("The healing moon sets."));
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
            final Room room = (Room) affected;
            if (!room.getArea().getClimateObj().canSeeTheMoon(room, this))
                unInvoke();
            else
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB M = room.fetchInhabitant(i);
                    final int healing = M.charStats().getStat(CharStats.STAT_CONSTITUTION);
                    CMLib.combat().postHealing(M, M, this, healing, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
                }
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Room R = mob.location();
            if (R != null) {
                if (!R.getArea().getClimateObj().canSeeTheMoon(R, null))
                    return Ability.QUALITY_INDIFFERENT;
                if (R.fetchEffect(ID()) != null)
                    return Ability.QUALITY_INDIFFERENT;
                for (final Enumeration<Ability> a = R.effects(); a.hasMoreElements(); ) {
                    final Ability A = a.nextElement();
                    if ((A != null)
                        && ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_MOONALTERING))
                        return Ability.QUALITY_INDIFFERENT;
                }

            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;
        if (!target.getArea().getClimateObj().canSeeTheMoon(target, null)) {
            mob.tell(L("You must be able to see the moon for this magic to work."));
            return false;
        }

        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("This place is already under the healing moon."));
            return false;
        }

        for (final Enumeration<Ability> a = target.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null)
                && ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_MOONALTERING)) {
                mob.tell(L("The moon is already under @x1, and can not be changed until this magic is gone.", A.name()));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to the moon.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The Healing Moon Rises!"));
                    beneficialAffect(mob, target, asLevel, 0);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to the moon, but the magic fades."));
        // return whether it worked
        return success;
    }
}
