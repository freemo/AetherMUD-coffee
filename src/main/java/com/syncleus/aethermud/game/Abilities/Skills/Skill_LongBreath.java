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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;


public class Skill_LongBreath extends Skill_DeepBreath {
    private final static String localizedName = CMLib.lang().L("Long Breath");

    @Override
    public String ID() {
        return "Skill_LongBreath";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int getMaxBreathTicks(final MOB mob) {
        return 5 + (super.adjustedLevel((MOB) affected, 0) / 3);
    }

}
