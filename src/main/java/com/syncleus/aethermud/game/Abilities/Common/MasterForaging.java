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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;

public class MasterForaging extends Foraging {
    private final static String localizedName = CMLib.lang().L("Master Foraging");
    private static final String[] triggerStrings = I(new String[]{"MFORAGE", "MFORAGING", "MASTERFORAGE", "MASTERFORAGING"});

    @Override
    public String ID() {
        return "MasterForaging";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int getDuration(MOB mob, int level) {
        return getDuration(113, mob, level, 25);
    }

    @Override
    protected int baseYield() {
        return 3;
    }
}

