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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.game.MOBS.interfaces.MOB;


public class Prop_RideAdjuster extends Prop_HaveAdjuster {
    @Override
    public String ID() {
        return "Prop_RideAdjuster";
    }

    @Override
    public String name() {
        return "Adjustments to stats when ridden";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return super.fixAccoutingsWithMask("Affects on the mounted: " + parameters[0], parameters[1]);
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_MOUNT;
    }

    @Override
    public boolean canApply(MOB mob) {
        if (!super.canApply(mob))
            return false;
        if (mob.riding() == affected)
            return true;
        return false;
    }
}
