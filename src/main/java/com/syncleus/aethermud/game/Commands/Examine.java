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
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.BoardableShip;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Libraries.interfaces.CMFlagLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


public class Examine extends StdCommand {
    private final String[] access = I(new String[]{"EXAMINE", "EXAM", "EXA", "LONGLOOK", "LLOOK", "LL", "ID"});

    public Examine() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        boolean quiet = false;
        Vector<String> origCmds = new XVector<String>(commands);
        if ((commands != null) && (commands.size() > 1) && (commands.get(commands.size() - 1).equalsIgnoreCase("UNOBTRUSIVELY"))) {
            commands.remove(commands.size() - 1);
            quiet = true;
        }
        final String textMsg = "<S-NAME> examine(s) ";
        final Room R = mob.location();
        if (R == null)
            return false;
        if ((commands != null) && (commands.size() > 1)) {
            Environmental thisThang = null;

            final String ID = CMParms.combine(commands, 1);
            if (ID.length() == 0)
                thisThang = R;
            else if ((ID.toUpperCase().startsWith("EXIT") && (commands.size() == 2))) {
                final CMMsg exitMsg = CMClass.getMsg(mob, thisThang, null, CMMsg.MSG_LOOK_EXITS, null);
                if ((CMProps.getIntVar(CMProps.Int.EXVIEW) >= CMProps.Int.EXVIEW_MIXED) != mob.isAttributeSet(MOB.Attrib.BRIEF))
                    exitMsg.setValue(CMMsg.MASK_OPTIMIZE);
                if (R.okMessage(mob, exitMsg))
                    R.send(mob, exitMsg);
                return false;
            }
            if (ID.equalsIgnoreCase("SELF") || ID.equalsIgnoreCase("ME"))
                thisThang = mob;

            if (thisThang == null)
                thisThang = R.fetchFromMOBRoomFavorsItems(mob, null, ID, Wearable.FILTER_ANY);
            if (thisThang == null) {
                final CMFlagLibrary flagLib = CMLib.flags();
                for (int i = 0; i < R.numItems(); i++) {
                    final Item I = R.getItem(i);
                    if (flagLib.isOpenAccessibleContainer(I)) {
                        thisThang = R.fetchFromRoomFavorItems(I, ID);
                        if (thisThang != null)
                            break;
                    }
                }
            }
            int dirCode = -1;
            if (thisThang == null) {
                dirCode = CMLib.directions().getGoodDirectionCode(ID);
                if (dirCode >= 0) {
                    final Room room = R.getRoomInDir(dirCode);
                    final Exit exit = R.getExitInDir(dirCode);
                    if ((room != null) && (exit != null))
                        thisThang = exit;
                    else {
                        CMLib.commands().doCommandFail(mob, origCmds, L("You don't see anything that way."));
                        return false;
                    }
                }
            }
            if (thisThang != null) {
                String name = "<T-NAMESELF>";
                if ((thisThang instanceof Room) || (thisThang instanceof Exit)) {
                    if (thisThang == R)
                        name = "around";
                    else if (dirCode >= 0)
                        name = ((R instanceof BoardableShip) || (R.getArea() instanceof BoardableShip)) ?
                            CMLib.directions().getShipDirectionName(dirCode) : CMLib.directions().getDirectionName(dirCode);
                }
                final CMMsg msg = CMClass.getMsg(mob, thisThang, null, CMMsg.MSG_EXAMINE, L("@x1@x2 closely.", textMsg, name));
                if (R.okMessage(mob, msg))
                    R.send(mob, msg);
                if ((mob.isAttributeSet(MOB.Attrib.AUTOEXITS)) && (thisThang instanceof Room))
                    msg.addTrailerMsg(CMClass.getMsg(mob, thisThang, null, CMMsg.MSG_LOOK_EXITS, null));
            } else
                CMLib.commands().doCommandFail(mob, origCmds, L("You don't see that here!"));
        } else {
            final CMMsg msg = CMClass.getMsg(mob, R, null, CMMsg.MSG_EXAMINE, (quiet ? null : textMsg + "around carefully."), CMMsg.MSG_EXAMINE, (quiet ? null : textMsg + "at you."), CMMsg.MSG_EXAMINE, (quiet ? null : textMsg + "around carefully."));
            if ((mob.isAttributeSet(MOB.Attrib.AUTOEXITS)) && (CMLib.flags().canBeSeenBy(R, mob)))
                msg.addTrailerMsg(CMClass.getMsg(mob, R, null, CMMsg.MSG_LOOK_EXITS, null));
            if (R.okMessage(mob, msg))
                R.send(mob, msg);
        }
        return false;
    }

    @Override
    public double combatActionsCost(final MOB mob, final List<String> cmds) {
        return CMProps.getCommandCombatActionCost(ID());
    }

    @Override
    public double actionsCost(MOB mob, List<String> cmds) {
        return 1.0;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }
}
