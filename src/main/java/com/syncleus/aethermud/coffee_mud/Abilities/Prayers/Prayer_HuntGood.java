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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;


public class Prayer_HuntGood extends Prayer_HuntEvil {
    private final static String localizedName = CMLib.lang().L("Hunt Good");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Hunting Good)");

    @Override
    public String ID() {
        return "Prayer_HuntGood";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY | Ability.FLAG_TRACKING;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected String word() {
        return "good";
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected MOB gameHere(Room room) {
        if (room == null)
            return null;
        for (int i = 0; i < room.numInhabitants(); i++) {
            final MOB mob = room.fetchInhabitant(i);
            if (CMLib.flags().isGood(mob))
                return mob;
        }
        return null;
    }

}
