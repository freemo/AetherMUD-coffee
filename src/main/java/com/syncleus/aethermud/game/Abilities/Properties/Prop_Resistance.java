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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.TriggeredAffect;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Prop_Resistance extends Prop_HaveResister {
    @Override
    public String ID() {
        return "Prop_Resistance";
    }

    @Override
    public String name() {
        return "Resistance to Stuff";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public boolean bubbleAffect() {
        return false;
    }

    @Override
    public long flags() {
        return Ability.FLAG_RESISTER;
    }

    @Override
    public String accountForYourself() {
        return "Have resistances: " + describeResistance(text());
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_ALWAYS;
    }

    @Override
    public boolean canResist(Environmental E) {
        return ((E instanceof MOB)
            && (E == affected));
    }
}
