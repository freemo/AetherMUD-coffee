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
package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;

import java.util.Vector;

public class Puppy extends Dog {
    private final static String localizedStaticName = CMLib.lang().L("Puppy");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Canine");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "Puppy";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 6;
    }

    @Override
    public int shortestFemale() {
        return 6;
    }

    @Override
    public int heightVariance() {
        return 3;
    }

    @Override
    public int lightestWeight() {
        return 7;
    }

    @Override
    public int weightVariance() {
        return 20;
    }

    @Override
    public long forbiddenWornBits() {
        return ~(Wearable.WORN_HEAD | Wearable.WORN_FEET | Wearable.WORN_NECK | Wearable.WORN_EARS | Wearable.WORN_EYES);
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
    }

    @Override
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 6);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 11);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
    }

    @Override
    public void affectCharState(MOB affectedMob, CharState affectableMaxState) {
        affectableMaxState.setMovement(affectableMaxState.getMovement() + 50);
    }
}
