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
package com.planet_ink.game.Races;

import com.planet_ink.game.Races.interfaces.Race;
import com.planet_ink.game.core.CMLib;


public class Duck extends WaterFowl {
    private final static String localizedStaticName = CMLib.lang().L("Duck");
    private final String[] racialAbilityNames = {"DuckSpeak"};
    private final int[] racialAbilityLevels = {1};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {""};

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
                return "duckling";
            case Race.AGE_YOUNGADULT:
            case Race.AGE_MATURE:
            case Race.AGE_MIDDLEAGED:
            default: {
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "drake";
                    case 'F':
                    case 'f':
                        return "duck";
                    default:
                        return name().toLowerCase();
                }
            }
            case Race.AGE_OLD:
            case Race.AGE_VENERABLE:
            case Race.AGE_ANCIENT: {
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "old drake";
                    case 'F':
                    case 'f':
                        return "old duck";
                    default:
                        return "old " + name().toLowerCase();
                }
            }
        }
    }
}
