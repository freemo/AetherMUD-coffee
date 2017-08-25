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
package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.HashSet;


public class Disease_Migraines extends Disease {
    private final static String localizedName = CMLib.lang().L("Migraine Headaches");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Migraine Headaches)");
    public HashSet<Ability> forgotten = new HashSet<Ability>();
    public HashSet<Ability> remember = new HashSet<Ability>();

    @Override
    public String ID() {
        return "Disease_Migraines";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 99999;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 50;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your headaches stop.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> get(s) terrible headaches.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return "";
    }

    @Override
    public int abilityCode() {
        return 0;
    }

    @Override
    public int difficultyLevel() {
        return 4;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amISource(mob))
            && (msg.tool() instanceof Ability)) {
            if (remember.contains(msg.tool()))
                return true;
            if (forgotten.contains(msg.tool())) {
                mob.tell(L("Your headaches make you forget @x1!", msg.tool().name()));
                return false;
            }
            if (mob.fetchAbility(msg.tool().ID()) == msg.tool()) {
                if (CMLib.dice().rollPercentage() > (mob.charStats().getSave(CharStats.STAT_SAVE_MIND) + 35)) {
                    forgotten.add((Ability) msg.tool());
                    mob.tell(L("Your headaches make you forget @x1!", msg.tool().name()));
                    return false;
                } else
                    remember.add((Ability) msg.tool());
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected == null)
            return false;
        return true;
    }
}
