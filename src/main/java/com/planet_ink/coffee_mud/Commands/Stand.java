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

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.collections.StringXVector;

import java.util.List;


public class Stand extends StdCommand {
    private final String[] access = I(new String[]{"STAND", "ST", "STA", "STAN"});

    public Stand() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final boolean ifnecessary = ((commands.size() > 1) && (commands.get(commands.size() - 1).equalsIgnoreCase("IFNECESSARY")));
        final Room room = CMLib.map().roomLocation(mob);
        if (CMLib.flags().isStanding(mob)) {
            if (!ifnecessary)
                CMLib.commands().doCommandFail(mob, new StringXVector(commands), L("You are already standing!"));
        } else if ((mob.session() != null) && (mob.session().isStopped()))
            CMLib.commands().doCommandFail(mob, new StringXVector(commands), L("You may not stand up."));
        else if (room != null) {
            final CMMsg msg = CMClass.getMsg(mob, null, null, CMMsg.MSG_STAND, mob.amDead() ? null : L("<S-NAME> stand(s) up."));
            if (room.okMessage(mob, msg))
                room.send(mob, msg);
        }
        return false;
    }

    @Override
    public double combatActionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandCombatActionCost(ID());
    }

    @Override
    public double actionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandActionCost(ID());
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
