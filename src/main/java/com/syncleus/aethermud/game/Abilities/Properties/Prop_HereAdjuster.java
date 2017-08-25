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
import com.syncleus.aethermud.game.core.CMLib;


public class Prop_HereAdjuster extends Prop_HaveAdjuster {
    @Override
    public String ID() {
        return "Prop_HereAdjuster";
    }

    @Override
    public String name() {
        return "Adjustments to stats when here";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    public String accountForYourself() {
        return super.fixAccoutingsWithMask("Affects on those here: " + parameters[0], parameters[1]);
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_ENTER;
    }

    @Override
    public boolean canApply(MOB mob) {
        if (affected == null)
            return true;
        if ((mob.location() != affected)
            || ((mask != null) && (!CMLib.masking().maskCheck(mask, mob, false))))
            return false;
        return true;
    }
}
