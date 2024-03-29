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

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Food;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


public class Eat extends StdCommand {
    private final String[] access = I(new String[]{"EAT"});

    public Eat() {
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
            CMLib.commands().doCommandFail(mob, origCmds, L("Eat what?"));
            return false;
        }
        commands.remove(0);

        Environmental thisThang = null;
        thisThang = mob.location().fetchFromMOBRoomFavorsItems(mob, null, CMParms.combine(commands, 0), Wearable.FILTER_ANY);
        if ((thisThang == null)
            || (!CMLib.flags().canBeSeenBy(thisThang, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't see '@x1' here.", CMParms.combine(commands, 0)));
            return false;
        }
        final boolean hasHands = mob.charStats().getBodyPart(Race.BODY_HAND) > 0;
        if ((thisThang instanceof Food) && (!mob.isMine(thisThang)) && (hasHands)) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to have '@x1'.", CMParms.combine(commands, 0)));
            return false;
        }
        final String eatSound = CMLib.protocol().msp("gulp.wav", 10);
        final String eatMsg = "<S-NAME> eat(s) <T-NAMESELF>." + eatSound;
        final CMMsg newMsg = CMClass.getMsg(mob, thisThang, null, hasHands ? CMMsg.MSG_EAT : CMMsg.MSG_EAT_GROUND, eatMsg);
        if (mob.location().okMessage(mob, newMsg)) {
            if ((thisThang instanceof Food)
                && (newMsg.value() > 0)
                && (newMsg.value() < ((Food) thisThang).nourishment())
                && (newMsg.othersMessage() != null)
                && (newMsg.othersMessage().startsWith(eatMsg))
                && (newMsg.sourceMessage().equalsIgnoreCase(newMsg.othersMessage()))
                && (newMsg.targetMessage().equalsIgnoreCase(newMsg.othersMessage()))) {
                final String biteMsg = "<S-NAME> take(s) a bite of <T-NAMESELF>." + eatSound;
                newMsg.setSourceMessage(biteMsg);
                newMsg.setTargetMessage(biteMsg);
                newMsg.setOthersMessage(biteMsg);
            }
            mob.location().send(mob, newMsg);
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
