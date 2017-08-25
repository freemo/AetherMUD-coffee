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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Play_Battlehymn extends Play {
    private final static String localizedName = CMLib.lang().L("Battlehymn");
    protected int timesTicking = 0;

    @Override
    public String ID() {
        return "Play_Battlehymn";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected String songOf() {
        return CMLib.english().startWithAorAn(name());
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (invoker == null)
            return;
        affectableStats.setDamage(affectableStats.damage() + 1 + (int) Math.round(CMath.mul(affectableStats.damage(), CMath.div(adjustedLevel(invoker(), 0), 100))));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected == null) || (invoker == null) || (!(affected instanceof MOB)))
            return false;
        if ((!((MOB) affected).isInCombat()) && (++timesTicking > (5 + super.getXTIMELevel(invoker()))))
            unInvoke();
        return true;
    }
}
