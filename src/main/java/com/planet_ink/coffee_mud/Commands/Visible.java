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
package com.planet_ink.coffee_mud.Commands;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Visible extends StdCommand {
    private final String[] access = I(new String[]{"VISIBLE", "VIS"});

    public Visible() {
    }

    public static java.util.List<Ability> returnOffensiveAffects(Physical fromMe) {
        final MOB newMOB = CMClass.getFactoryMOB();
        newMOB.setLocation(CMLib.map().roomLocation(fromMe));
        final List<Ability> offenders = new Vector<Ability>();
        for (int a = 0; a < fromMe.numEffects(); a++) // personal
        {
            final Ability A = fromMe.fetchEffect(a);
            if ((A != null) && (A.canBeUninvoked())) {
                try {
                    newMOB.recoverPhyStats();
                    A.affectPhyStats(newMOB, newMOB.phyStats());
                    if (CMLib.flags().isInvisible(newMOB) || CMLib.flags().isHidden(newMOB))
                        offenders.add(A);
                } catch (final Exception e) {
                }
            }
        }
        newMOB.destroy();
        return offenders;
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final String str = L("Prop_WizInvis");
        final Ability A = mob.fetchEffect(str);
        boolean didSomething = false;
        if (A != null) {
            final Command C = CMClass.getCommand("WizInv");
            if ((C != null) && (C.securityCheck(mob))) {
                didSomething = true;
                C.execute(mob, new XVector<String>("WIZINV", "OFF"), metaFlags);
            }
        }
        final java.util.List<Ability> V = returnOffensiveAffects(mob);
        if (V.size() == 0) {
            if (!didSomething)
                mob.tell(L("You are not invisible or hidden!"));
        } else
            for (int v = 0; v < V.size(); v++)
                V.get(v).unInvoke();
        mob.location().recoverRoomStats();
        mob.location().recoverRoomStats();
        return false;
    }

    @Override
    public double actionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandActionCost(ID());
    }

    @Override
    public double combatActionsCost(MOB mob, List<String> cmds) {
        return 0.25;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
