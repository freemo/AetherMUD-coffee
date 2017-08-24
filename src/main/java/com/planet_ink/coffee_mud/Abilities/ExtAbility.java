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
package com.planet_ink.coffee_mud.Abilities;

import com.planet_ink.coffee_mud.Abilities.interfaces.ExtendableAbility;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.*;


public class ExtAbility extends StdAbility implements ExtendableAbility {
    private String ID = "ExtAbility";
    private StatsAffecting statsAffector = null;
    private MsgListener msgListener = null;
    private Tickable tickable = null;

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public ExtendableAbility setAbilityID(String ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public ExtendableAbility setStatsAffector(StatsAffecting code) {
        this.statsAffector = code;
        return this;
    }

    @Override
    public ExtendableAbility setMsgListener(MsgListener code) {
        this.msgListener = code;
        return this;
    }

    @Override
    public ExtendableAbility setTickable(Tickable code) {
        this.tickable = code;
        return this;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (this.statsAffector != null)
            statsAffector.affectPhyStats(affected, affectableStats);
    }

    @Override
    public void affectCharStats(MOB affectedMob, CharStats affectableStats) {
        super.affectCharStats(affectedMob, affectableStats);
        if (this.statsAffector != null)
            statsAffector.affectCharStats(affectedMob, affectableStats);
    }

    @Override
    public void affectCharState(MOB affectedMob, CharState affectableMaxState) {
        super.affectCharState(affectedMob, affectableMaxState);
        if (this.statsAffector != null)
            statsAffector.affectCharState(affectedMob, affectableMaxState);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (msgListener != null)
            msgListener.executeMsg(myHost, msg);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if (msgListener != null)
            return msgListener.okMessage(myHost, msg);
        return true;
    }

    @Override
    public String name() {
        return (tickable != null) ? tickable.name() : super.name();
    }

    @Override
    public int getTickStatus() {
        return (tickable != null) ? tickable.getTickStatus() : super.getTickStatus();
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (tickable != null)
            return tickable.tick(ticking, tickID);
        return true;
    }
}
