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
package com.planet_ink.coffee_mud.Exits;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class ClimbableExit extends StdExit {
    protected Ability climbA;

    public ClimbableExit() {
        super();
        climbA = CMClass.getAbility("Prop_Climbable");
        if (climbA != null) {
            climbA.setAffectedOne(this);
            climbA.makeNonUninvokable();
        }
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "ClimbableExit";
    }

    @Override
    public String Name() {
        return "a sheer surface";
    }

    @Override
    public String displayText() {
        return "a sheer surface";
    }

    @Override
    public String description() {
        return "Looks like you'll have to climb it.";
    }

    @Override
    public CMObject copyOf() {
        final ClimbableExit R = (ClimbableExit) super.copyOf();
        R.climbA = CMClass.getAbility("Prop_Climbable");
        R.climbA.setAffectedOne(R);
        R.climbA.makeNonUninvokable();
        return R;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((climbA != null) && (!climbA.okMessage(myHost, msg)))
            return false;
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (climbA != null)
            climbA.executeMsg(myHost, msg);
        super.executeMsg(myHost, msg);
    }

    @Override
    public void recoverPhyStats() {
        super.recoverPhyStats();
        if (climbA != null)
            climbA.affectPhyStats(this, phyStats());
    }
}
