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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;

import java.util.List;
import java.util.Vector;


public class Pour extends StdCommand {
    private final String[] access = I(new String[]{"POUR"});

    public Pour() {
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
            CMLib.commands().doCommandFail(mob, origCmds, L("Pour what, into/onto what?"));
            return false;
        }
        commands.remove(0);
        PourVerb verb = PourVerb.DEFAULT;
        if (((commands.get(0))).equalsIgnoreCase("out")) {
            commands.remove(0);
            verb = PourVerb.OUT;
            if (commands.size() == 0) {
                CMLib.commands().doCommandFail(mob, origCmds, L("Pour out what?"));
                return false;
            }
        }
        Environmental fillFromThis = null;
        final String thingToFillFrom = commands.get(0);
        fillFromThis = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, thingToFillFrom);
        if ((fillFromThis == null) || (!CMLib.flags().canBeSeenBy(fillFromThis, mob))) {
            CMLib.commands().doCommandFail(mob, origCmds, L("You don't seem to have '@x1'.", thingToFillFrom));
            return false;
        }
        commands.remove(0);

        if (commands.size() == 1) {
            if (((commands.get(0))).equalsIgnoreCase("out")) {
                commands.remove(0);
                verb = PourVerb.OUT;
            }
        } else if (commands.size() > 1) {
            if (((commands.get(0))).equalsIgnoreCase("into"))
                commands.remove(0);
            else if (((commands.get(0))).equalsIgnoreCase("onto")) {
                commands.remove(0);
                verb = PourVerb.ONTO;
            }
        }

        Environmental fillThis;
        String msgStr;
        if (verb == PourVerb.OUT) {
            final Item out = CMClass.getItem("StdDrink");
            ((Drink) out).setLiquidHeld(999999);
            ((Drink) out).setLiquidRemaining(0);
            out.setDisplayText("");
            out.setName(L("out"));
            msgStr = L("<S-NAME> pour(s) <O-NAME> <T-NAME>.");
            mob.location().addItem(out, ItemPossessor.Expire.Resource);
            fillThis = out;
        } else {
            if (commands.size() < 1) {
                CMLib.commands().doCommandFail(mob, origCmds, L("@x1 what should I pour the @x2?", CMStrings.capitalizeAndLower(verb.name()), thingToFillFrom));
                return false;
            }
            final String thingToFill = CMParms.combine(commands, 0);
            fillThis = mob.location().fetchFromMOBRoomFavorsItems(mob, null, thingToFill, Wearable.FILTER_ANY);
            if ((fillThis == null) || (!CMLib.flags().canBeSeenBy(fillThis, mob))) {
                CMLib.commands().doCommandFail(mob, origCmds, L("I don't see '@x1' here.", thingToFill));
                return false;
            }
            if ((verb == PourVerb.DEFAULT) && (!(fillThis instanceof Drink)))
                verb = PourVerb.ONTO;
            else if ((verb == PourVerb.ONTO) && (fillThis instanceof Drink))
                verb = PourVerb.INTO;
            if (verb == PourVerb.ONTO)
                msgStr = L("<S-NAME> pour(s) <O-NAME> onto <T-NAME>.");
            else
                msgStr = L("<S-NAME> pour(s) <O-NAME> into <T-NAME>.");
        }

        final CMMsg fillMsg = CMClass.getMsg(mob, fillThis, fillFromThis, (verb == PourVerb.ONTO) ? CMMsg.MSG_POUR : CMMsg.MSG_FILL, msgStr);
        if (mob.location().okMessage(mob, fillMsg))
            mob.location().send(mob, fillMsg);

        if (verb == PourVerb.OUT)
            fillThis.destroy();
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

    enum PourVerb {DEFAULT, INTO, ONTO, OUT}

}
