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
import com.syncleus.aethermud.game.Common.interfaces.Climate;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_CalmWind extends Chant {
    private final static String localizedName = CMLib.lang().L("Calm Wind");

    public static void xpWorthyChange(MOB mob, Climate oldC, Climate newC) {
        if ((oldC.nextWeatherType(null) != Climate.WEATHER_CLEAR)
            && (oldC.nextWeatherType(null) != Climate.WEATHER_CLOUDY)
            && ((newC.nextWeatherType(null) == Climate.WEATHER_CLEAR)
            || (newC.nextWeatherType(null) == Climate.WEATHER_CLOUDY))
            && ((newC.weatherType(null) == Climate.WEATHER_CLEAR)
            || (newC.weatherType(null) == Climate.WEATHER_CLOUDY))) {
            mob.tell(CMLib.lang().L("^YYou have restored balance to the weather!^N"));
            CMLib.leveler().postExperience(mob, null, null, 25, false);
        }
    }

    @Override
    public String ID() {
        return "Chant_CalmWind";
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
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public long flags() {
        return Ability.FLAG_WEATHERAFFECTING;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Room R = mob.location();
            if (R != null) {
                if (CMath.bset(weatherQue(R), WEATHERQUE_CALM))
                    return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
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
        switch (mob.location().getArea().getClimateObj().weatherType(mob.location())) {
            case Climate.WEATHER_WINDY:
            case Climate.WEATHER_THUNDERSTORM:
            case Climate.WEATHER_BLIZZARD:
            case Climate.WEATHER_DUSTSTORM:
                break;
            case Climate.WEATHER_HAIL:
            case Climate.WEATHER_SLEET:
            case Climate.WEATHER_SNOW:
            case Climate.WEATHER_RAIN:
                mob.tell(L("The weather is nasty, but not especially windy any more."));
                return false;
            default:
                mob.tell(L("If doesn't seem especially windy right now."));
                return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int size = mob.location().getArea().numberOfProperIDedRooms();
        size = size / (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (size < 0)
            size = 0;
        final boolean success = proficiencyCheck(mob, -size, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("^JThe swirling sky changes color!^?") : L("^S<S-NAME> chant(s) into the swirling sky!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Climate C = mob.location().getArea().getClimateObj();
                final Climate oldC = (Climate) C.copyOf();
                switch (C.weatherType(mob.location())) {
                    case Climate.WEATHER_WINDY:
                        C.setNextWeatherType(Climate.WEATHER_CLEAR);
                        break;
                    case Climate.WEATHER_THUNDERSTORM:
                        C.setNextWeatherType(Climate.WEATHER_RAIN);
                        break;
                    case Climate.WEATHER_BLIZZARD:
                        C.setNextWeatherType(Climate.WEATHER_SNOW);
                        break;
                    case Climate.WEATHER_DUSTSTORM:
                        C.setNextWeatherType(Climate.WEATHER_CLEAR);
                        break;
                    default:
                        break;
                }
                C.forceWeatherTick(mob.location().getArea());
                Chant_CalmWeather.xpWorthyChange(mob, mob.location().getArea(), oldC, C);
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> chant(s) into the sky, but the magic fizzles."));

        return success;
    }
}
