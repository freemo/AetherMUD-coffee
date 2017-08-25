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
import com.syncleus.aethermud.game.core.interfaces.Rideable;


public class Prop_RideResister extends Prop_HaveResister {
    @Override
    public String ID() {
        return "Prop_RideResister";
    }

    @Override
    public String name() {
        return "Resistance due to riding";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "Those mounted gain resistances: " + describeResistance(text());
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_MOUNT;
    }

    @Override
    public boolean canResist(Environmental E) {
        if ((affected instanceof Rideable)
            && (E instanceof MOB)
            && (((Rideable) affected).amRiding((MOB) E))
            && (((MOB) E).location() != null))
            return true;
        return false;
    }
}
