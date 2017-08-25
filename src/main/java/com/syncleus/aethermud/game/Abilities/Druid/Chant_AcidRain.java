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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.Climate;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Chant_AcidRain extends Chant {
    private final static String localizedName = CMLib.lang().L("Acid Rain");

    @Override
    public String ID() {
        return "Chant_AcidRain";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
    }

    @Override
    public long flags() {
        return Ability.FLAG_EARTHBASED;
    }

    public boolean isRaining(Room R) {
        if ((R.getArea().getClimateObj().weatherType(R) == Climate.WEATHER_RAIN)
            || (R.getArea().getClimateObj().weatherType(R) == Climate.WEATHER_SLEET)
            || (R.getArea().getClimateObj().weatherType(R) == Climate.WEATHER_THUNDERSTORM))
            return true;
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected != null) && (affected instanceof Room)) {
            final Room R = (Room) affected;
            if (isRaining(R))
                for (int i = 0; i < R.numInhabitants(); i++) {
                    final MOB M = R.fetchInhabitant(i);
                    if (M != null) {
                        final MOB invoker = (invoker() != null) ? invoker() : M;
                        if (CMLib.dice().rollPercentage() > M.charStats().getSave(CharStats.STAT_SAVE_ACID))
                            CMLib.combat().postDamage(invoker, M, this, CMLib.dice().roll(1, M.phyStats().level() + (2 * getXLEVELLevel(invoker())), 1), CMMsg.MASK_ALWAYS | CMMsg.TYP_ACID, Weapon.TYPE_MELTING, L("The acid rain <DAMAGE> <T-NAME>!"));
                        CMLib.combat().postRevengeAttack(M, invoker);
                    }
                }
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!isRaining(mob.location()))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;
        if (!isRaining(target)) {
            mob.tell(L("This chant requires some rain."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to the rain.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    for (int i = 0; i < target.numInhabitants(); i++) {
                        final MOB M = target.fetchInhabitant(i);
                        if ((M != null) && (mob != M))
                            mob.location().show(mob, M, CMMsg.MASK_MALICIOUS | CMMsg.TYP_OK_VISUAL, null);
                    }
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Acid rain starts pouring from the sky!"));
                    maliciousAffect(mob, target, asLevel, 0, -1);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to the rain, but the magic fades."));
        // return whether it worked
        return success;
    }
}
