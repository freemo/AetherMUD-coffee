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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.CMath;

import java.util.List;


public class WizInv extends StdCommand {
    private final String[] access = I(new String[]{"WIZINVISIBLE", "WIZINV", "NOWIZINV"});

    public WizInv() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        String str = commands.get(0);
        if (Character.toUpperCase(str.charAt(0)) != 'W')
            commands.add(1, "OFF");
        commands.remove(0);
        int abilityCode = PhyStats.IS_NOT_SEEN | PhyStats.IS_CLOAKED;
        Ability A = mob.fetchEffect("Prop_WizInvis");
        if ((commands.size() > 0) && ("NOCLOAK".startsWith(CMParms.combine(commands, 0).trim().toUpperCase())))
            abilityCode = PhyStats.IS_NOT_SEEN;
        if (CMParms.combine(commands, 0).trim().equalsIgnoreCase("OFF")) {
            if (A != null)
                A.unInvoke();
            else
                mob.tell(L("You are not wizinvisible!"));
            return false;
        } else if (A != null) {
            if (CMath.bset(A.abilityCode(), abilityCode)) {
                mob.tell(L("You have already faded from view!"));
                return false;
            }
        }

        if (A == null)
            A = CMClass.getAbility("Prop_WizInvis");
        if (A != null) {
            if (mob.location() != null)
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> fade(s) from view!"));
            if (mob.fetchEffect(A.ID()) == null)
                mob.addPriorityEffect((Ability) A.copyOf());
            A = mob.fetchEffect(A.ID());
            if (A != null) {
                A.setAbilityCode(abilityCode);
                A.setMiscText("UNINVOKABLE");
            }
            mob.recoverPhyStats();
            mob.location().recoverRoomStats();
            mob.tell(L("You may uninvoke WIZINV with 'WIZINV OFF'."));
            return false;
        }
        mob.tell(L("Wizard invisibility is not available!"));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.WIZINV);
    }

}
