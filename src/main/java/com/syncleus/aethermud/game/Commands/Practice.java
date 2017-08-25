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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;

import java.util.List;
import java.util.Vector;


public class Practice extends StdCommand {
    private final String[] access = I(new String[]{"PRACTICE", "PRAC"});

    public Practice() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands.size() < 2) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You have @x1 practice points.  Enter HELP PRACTICE for more information.", "" + mob.getPractices()));
            return false;
        }
        commands.remove(0);

        MOB teacher = null;
        boolean triedTeacher = false;
        if (commands.size() > 1) {
            teacher = mob.location().fetchInhabitant(commands.get(commands.size() - 1));
            if (teacher != null) {
                triedTeacher = true;
                commands.remove(commands.size() - 1);
            }
        }

        final String abilityName = CMParms.combine(commands, 0);

        if (teacher == null)
            for (int i = 0; i < mob.location().numInhabitants(); i++) {
                final MOB possTeach = mob.location().fetchInhabitant(i);
                if ((possTeach != null) && (possTeach.findAbility(abilityName) != null) && (possTeach != mob)) {
                    teacher = possTeach;
                    break;
                }
            }

        final Ability myAbility = mob.findAbility(abilityName);
        if (myAbility == null) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to know @x1.", abilityName));
            return false;
        }

        if ((teacher == null) || (!CMLib.flags().canBeSeenBy(teacher, mob))) {
            if (triedTeacher)
                CMLib.commands().doCommandFail(mob, origCmds, L("That person doesn't seem to be here."));
            else
                CMLib.commands().doCommandFail(mob, origCmds, L("There doesn't seem to be a teacher to practice with here."));
            return false;
        }

        if (!myAbility.isSavable()) {
            CMLib.commands().doCommandFail(mob, origCmds, L("@x1 cannot be practiced, as it is a native skill.", myAbility.name()));
            return false;
        }

        final Ability teacherAbility = mob.findAbility(abilityName);
        if (teacherAbility == null) {
            CMLib.commands().doCommandFail(mob, origCmds, L("@x1 doesn't seem to know @x2.", teacher.name(), abilityName));
            return false;
        }

        if (!teacherAbility.canBeTaughtBy(teacher, mob))
            return false;
        if (!teacherAbility.canBePracticedBy(teacher, mob))
            return false;
        CMMsg msg = CMClass.getMsg(teacher, mob, null, CMMsg.MSG_SPEAK, null);
        if (!mob.location().okMessage(mob, msg))
            return false;
        msg = CMClass.getMsg(teacher, mob, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> practice(s) '@x1' with <T-NAMESELF>.", myAbility.name()));
        if (!mob.location().okMessage(mob, msg))
            return false;
        teacherAbility.practice(teacher, mob);
        mob.location().send(mob, msg);
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
        return false;
    }

}
