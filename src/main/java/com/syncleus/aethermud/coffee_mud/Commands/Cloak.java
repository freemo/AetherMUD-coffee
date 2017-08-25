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
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.CMath;

import java.util.List;


public class Cloak extends StdCommand {
    private final String[] access = I(new String[]{"CLOAK"});

    public Cloak() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        String str = commands.get(0);
        if (Character.toUpperCase(str.charAt(0)) != 'C')
            commands.add(1, "OFF");
        commands.remove(0);
        final int abilityCode = PhyStats.IS_CLOAKED;
        str = L("Prop_WizInvis");
        Ability A = mob.fetchEffect(str);
        if (CMParms.combine(commands, 0).trim().equalsIgnoreCase("OFF")) {
            if (A != null)
                A.unInvoke();
            else
                mob.tell(L("You are not cloaked!"));
            return false;
        } else if (A != null) {
            if (CMath.bset(A.abilityCode(), abilityCode) && (!CMath.bset(A.abilityCode(), PhyStats.IS_NOT_SEEN))) {
                mob.tell(L("You are already cloaked!"));
                return false;
            }
        }

        if (A == null)
            A = CMClass.getAbility(str);
        if (A != null) {
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> become(s) cloaked!"));
            if (mob.fetchEffect(A.ID()) == null)
                mob.addPriorityEffect((Ability) A.copyOf());
            A = mob.fetchEffect(A.ID());
            if (A != null)
                A.setAbilityCode(abilityCode);

            mob.recoverPhyStats();
            mob.location().recoverRoomStats();
            mob.tell(L("You may uninvoke CLOAK with 'CLOAK OFF' or 'WIZINV OFF'."));
            return false;
        }
        mob.tell(L("Cloaking is not available!"));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.CLOAK);
    }

}
