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
package com.planet_ink.game.Commands;

import com.planet_ink.game.CharClasses.interfaces.CharClass;
import com.planet_ink.game.Commands.interfaces.Command;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.Races.interfaces.Race;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.CMath;

import java.util.List;


public class OutFit extends StdCommand {
    private final String[] access = I(new String[]{"OUTFIT"});

    public OutFit() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean preExecute(MOB mob, List<String> commands, int metaFlags, int secondsElapsed, double actionsRemaining)
        throws java.io.IOException {
        if (secondsElapsed > 8.0)
            mob.tell(L("You feel your outfit plea is almost answered."));
        else if (secondsElapsed > 4.0)
            mob.tell(L("Your plea swirls around you."));
        else if (actionsRemaining > 0.0)
            mob.tell(L("You invoke a plea for mystical outfitting and await the answer."));
        return true;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (mob == null)
            return false;
        if (mob.charStats() == null)
            return false;
        final CharClass C = mob.charStats().getCurrentClass();
        final Race R = mob.charStats().getMyRace();
        if (C != null)
            CMLib.utensils().outfit(mob, C.outfit(mob));
        if (R != null)
            CMLib.utensils().outfit(mob, R.outfit(mob));
        mob.tell(L("\n\r"));
        final Command C2 = CMClass.getCommand("Equipment");
        if (C2 != null)
            C2.executeInternal(mob, metaFlags);
        mob.tell(L("\n\rUseful equipment appears mysteriously out of the Java Plane."));
        mob.recoverCharStats();
        mob.recoverMaxState();
        mob.recoverPhyStats();
        return false;
    }

    @Override
    public double combatActionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandCombatActionCost(ID(), CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMCMDTIME), 25.0));
    }

    @Override
    public double actionsCost(MOB mob, List<String> cmds) {
        return CMProps.getCommandActionCost(ID(), CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME), 25.0));
    }

    @Override
    public boolean canBeOrdered() {
        return false;
    }

}
