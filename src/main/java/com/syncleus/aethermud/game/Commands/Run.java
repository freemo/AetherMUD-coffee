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

import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.CMath;

import java.util.List;


public class Run extends Go {
    private final String[] access = I(new String[]{"RUN"});

    public Run() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    public int energyExpenseFactor() {
        return 2;
    }

    @Override
    public double actionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandActionCost(ID(), CMath.div(CMProps.getIntVar(CMProps.Int.DEFCMDTIME), 400.0));
    }

    @Override
    public double combatActionsCost(MOB mob, List<String> cmds) {
        return CMProps.getCommandCombatActionCost(ID(), CMath.div(CMProps.getIntVar(CMProps.Int.DEFCOMCMDTIME), 400.0));
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (mob == null)
            return super.execute(mob, commands, metaFlags);
        final boolean wasSet = mob.isAttributeSet(MOB.Attrib.AUTORUN);
        mob.setAttribute(MOB.Attrib.AUTORUN, true);
        final boolean returnValue = super.execute(mob, commands, metaFlags);
        if (!wasSet)
            mob.setAttribute(MOB.Attrib.AUTORUN, false);
        return returnValue;
    }
}
