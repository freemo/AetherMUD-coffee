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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Libraries.interfaces.MaskingLibrary.CompiledZMask;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Vector;


public class MobileAggressive extends Mobile {
    protected int tickWait = 0;
    protected boolean mobkill = false;
    protected boolean misbehave = false;
    protected String attackMsg = null;
    protected int aggressiveTickDown = 0;
    protected boolean levelcheck = false;
    protected VeryAggressive veryA = new VeryAggressive();
    protected CompiledZMask mask = null;
    public MobileAggressive() {
        super();

        tickDown = 0;
        aggressiveTickDown = 0;
    }

    @Override
    public String ID() {
        return "MobileAggressive";
    }

    @Override
    public long flags() {
        return Behavior.FLAG_POTENTIALLYAGGRESSIVE | Behavior.FLAG_TROUBLEMAKING;
    }

    @Override
    public String accountForYourself() {
        if (getParms().trim().length() > 0)
            return "wandering aggression against " + CMLib.masking().maskDesc(getParms(), true).toLowerCase();
        else
            return "wandering aggressiveness";
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        tickWait = CMParms.getParmInt(newParms, "delay", 0);
        attackMsg = CMParms.getParmStr(newParms, "MESSAGE", null);
        tickDown = tickWait;
        aggressiveTickDown = tickWait;
        final Vector<String> V = CMParms.parse(newParms.toUpperCase());
        levelcheck = V.contains("CHECKLEVEL");
        mobkill = V.contains("MOBKILL");
        misbehave = V.contains("MISBEHAVE");
        this.mask = CMLib.masking().getPreCompiledMask(newParms);
    }

    @Override
    public boolean grantsAggressivenessTo(MOB M) {
        if (M == null)
            return true;
        return CMLib.masking().maskCheck(getParms(), M, false);
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        veryA.executeMsg(affecting, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        tickStatus = Tickable.STATUS_MISC + 0;
        super.tick(ticking, tickID);
        tickStatus = Tickable.STATUS_MISC + 1;
        if (tickID != Tickable.TICKID_MOB) {
            tickStatus = Tickable.STATUS_NOT;
            return true;
        }
        if ((--aggressiveTickDown) < 0) {
            aggressiveTickDown = tickWait;
            tickStatus = Tickable.STATUS_MISC + 2;
            veryA.tickAggressively(ticking, tickID, mobkill, misbehave, levelcheck, this.mask, attackMsg);
            tickStatus = Tickable.STATUS_MISC + 3;
            veryA.tickVeryAggressively(ticking, tickID, wander, mobkill, misbehave, levelcheck, this.mask, attackMsg);
        }
        tickStatus = Tickable.STATUS_NOT;
        return true;
    }
}
