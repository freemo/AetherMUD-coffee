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
package com.syncleus.aethermud.game.Races;

import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;


public class Goose extends WaterFowl {
    private final static String localizedStaticName = CMLib.lang().L("Duck");
    private final String[] racialAbilityNames = CMParms.combine(super.racialAbilityNames(), new String[]{"GooseSpeak"});
    private final int[] racialAbilityLevels = CMParms.combine(super.racialAbilityLevels(), new int[]{1});
    private final int[] racialAbilityProficiencies = CMParms.combine(super.racialAbilityProficiencies(), new int[]{100});
    private final boolean[] racialAbilityQuals = CMParms.combine(super.racialAbilityQuals(), new boolean[]{false});
    private final String[] racialAbilityParms = CMParms.combine(super.racialAbilityParms(), new String[]{""});

    @Override
    public String ID() {
        return "Duck";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    protected String[] racialAbilityNames() {
        return racialAbilityNames;
    }

    @Override
    protected int[] racialAbilityLevels() {
        return racialAbilityLevels;
    }

    @Override
    protected int[] racialAbilityProficiencies() {
        return racialAbilityProficiencies;
    }

    @Override
    protected boolean[] racialAbilityQuals() {
        return racialAbilityQuals;
    }

    @Override
    public String[] racialAbilityParms() {
        return racialAbilityParms;
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
            case Race.AGE_CHILD:
                return "gosling";
            case Race.AGE_YOUNGADULT:
            case Race.AGE_MATURE:
            case Race.AGE_MIDDLEAGED:
            default:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "gander";
                    case 'F':
                    case 'f':
                        return "goose";
                    default:
                        return name().toLowerCase();
                }
            case Race.AGE_OLD:
            case Race.AGE_VENERABLE:
            case Race.AGE_ANCIENT:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "old gander";
                    case 'F':
                    case 'f':
                        return "old goose";
                    default:
                        return "old " + name().toLowerCase();
                }
        }
    }
}
