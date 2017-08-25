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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.interfaces.Places;


public class MagicFreeRoom extends StdRoom {
    public MagicFreeRoom() {
        super();
        basePhyStats.setWeight(1);
        recoverPhyStats();
        addEffect(CMClass.getAbility("Prop_MagicFreedom"));
        climask = Places.CLIMASK_NORMAL;
    }

    @Override
    public String ID() {
        return "MagicFreeRoom";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_STONE;
    }
}
