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


public class Horse extends Equine {
    private final static String localizedStaticName = CMLib.lang().L("Horse");

    @Override
    public String ID() {
        return "Horse";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
                return "foal";
            case Race.AGE_CHILD:
            case Race.AGE_YOUNGADULT:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "colt";
                    case 'F':
                    case 'f':
                        return "filly";
                    default:
                        return "young " + name().toLowerCase();
                }
            case Race.AGE_MATURE:
            case Race.AGE_MIDDLEAGED:
            default:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "stud";
                    case 'F':
                    case 'f':
                        return "stallion";
                    default:
                        return name().toLowerCase();
                }
            case Race.AGE_OLD:
            case Race.AGE_VENERABLE:
            case Race.AGE_ANCIENT:
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "old male " + name().toLowerCase();
                    case 'F':
                    case 'f':
                        return "old female " + name().toLowerCase();
                    default:
                        return "old " + name().toLowerCase();
                }
        }
    }
}
