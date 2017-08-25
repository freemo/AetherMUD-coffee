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
package com.planet_ink.game.Abilities.Skills;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Places;

import java.util.List;
import java.util.Vector;


public class Skill_WildernessLore extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Wilderness Lore");
    private static final String[] triggerStrings = I(new String[]{"WILDERNESSLORE", "WLORE"});

    @Override
    public String ID() {
        return "Skill_WildernessLore";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (!success) {
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> take(s) a quick look at the terrain and feel(s) quite confused."));
            return false;
        }
        final Room room = mob.location();
        final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_HANDS, L("<S-NAME> take(s) a quick look at the terrain."));
        if (room.okMessage(mob, msg)) {
            room.send(mob, msg);
            switch (room.domainType()) {
                case Room.DOMAIN_INDOORS_METAL:
                    mob.tell(L("You are in a metal structure."));
                    break;
                case Room.DOMAIN_OUTDOORS_SPACEPORT:
                    mob.tell(L("You are at a space port."));
                    break;
                case Room.DOMAIN_OUTDOORS_CITY:
                    mob.tell(L("You are on a city street."));
                    break;
                case Room.DOMAIN_OUTDOORS_WOODS:
                    mob.tell(L("You are in a forest."));
                    break;
                case Room.DOMAIN_OUTDOORS_ROCKS:
                    mob.tell(L("You are on a rocky plain."));
                    break;
                case Room.DOMAIN_OUTDOORS_PLAINS:
                    mob.tell(L("You are on the plains."));
                    break;
                case Room.DOMAIN_OUTDOORS_UNDERWATER:
                    mob.tell(L("You are under the water."));
                    break;
                case Room.DOMAIN_OUTDOORS_AIR:
                    mob.tell(L("You are up in the air."));
                    break;
                case Room.DOMAIN_OUTDOORS_WATERSURFACE:
                    mob.tell(L("You are on the surface of the water."));
                    break;
                case Room.DOMAIN_OUTDOORS_JUNGLE:
                    mob.tell(L("You are in a jungle."));
                    break;
                case Room.DOMAIN_OUTDOORS_SEAPORT:
                    mob.tell(L("You are at a seaport."));
                    break;
                case Room.DOMAIN_OUTDOORS_SWAMP:
                    mob.tell(L("You are in a swamp."));
                    break;
                case Room.DOMAIN_OUTDOORS_DESERT:
                    mob.tell(L("You are in a desert."));
                    break;
                case Room.DOMAIN_OUTDOORS_HILLS:
                    mob.tell(L("You are in the hills."));
                    break;
                case Room.DOMAIN_OUTDOORS_MOUNTAINS:
                    mob.tell(L("You are on a mountain."));
                    break;
                case Room.DOMAIN_INDOORS_STONE:
                    mob.tell(L("You are in a stone structure."));
                    break;
                case Room.DOMAIN_INDOORS_WOOD:
                    mob.tell(L("You are in a wooden structure."));
                    break;
                case Room.DOMAIN_INDOORS_CAVE:
                    mob.tell(L("You are in a cave."));
                    break;
                case Room.DOMAIN_INDOORS_MAGIC:
                    mob.tell(L("You are in a magical place."));
                    break;
                case Room.DOMAIN_INDOORS_UNDERWATER:
                    mob.tell(L("You are under the water."));
                    break;
                case Room.DOMAIN_INDOORS_AIR:
                    mob.tell(L("You are up in a large indoor space."));
                    break;
                case Room.DOMAIN_INDOORS_WATERSURFACE:
                    mob.tell(L("You are inside, on the surface of the water."));
                    break;
            }
            final int derivedClimate = room.getClimateType();
            if (derivedClimate != Places.CLIMASK_NORMAL) {
                final StringBuffer str = new StringBuffer(L("It is unusually "));
                final List<String> conditions = new Vector<String>();
                if (CMath.bset(derivedClimate, Places.CLIMASK_WET))
                    conditions.add("wet");
                if (CMath.bset(derivedClimate, Places.CLIMASK_HOT))
                    conditions.add("hot");
                if (CMath.bset(derivedClimate, Places.CLIMASK_DRY))
                    conditions.add("dry");
                if (CMath.bset(derivedClimate, Places.CLIMASK_COLD))
                    conditions.add("cold");
                if (CMath.bset(derivedClimate, Places.CLIMASK_WINDY))
                    conditions.add("windy");
                str.append(CMLib.english().toEnglishStringList(conditions.toArray(new String[0])));
                str.append(L(" here."));
                mob.tell(str.toString());
            }
        } else
            mob.location().show(mob, null, this, CMMsg.MSG_HANDS, L("<S-NAME> take(s) a quick look around, but get(s) confused."));
        return success;
    }

}
