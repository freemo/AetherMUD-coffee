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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.Directions;
import com.planet_ink.game.core.collections.SLinkedList;
import com.planet_ink.game.core.interfaces.Environmental;

import java.util.List;


public class Prop_OpenCommand extends Property {
    private final List<String[]> commandPhrases = new SLinkedList<String[]>();

    @Override
    public String ID() {
        return "Prop_OpenCommand";
    }

    @Override
    public String name() {
        return "Opening Command";
    }

    @Override
    public void setMiscText(String newMiscText) {
        commandPhrases.clear();
        for (String p : newMiscText.split(";")) {
            if (p.trim().length() > 0) {
                final String[] cmd = CMParms.parse(p).toArray(new String[0]);
                if (cmd.length > 0) {
                    cmd[0] = cmd[0].toUpperCase();
                    commandPhrases.add(cmd);
                }
            }
        }
        super.setMiscText(newMiscText);
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_EXITS;
    }

    @Override
    public String accountForYourself() {
        return "";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_HUH)
            && (msg.targetMessage() != null)) {
            final MOB mob = msg.source();
            final List<String> cmds = CMParms.parse(msg.targetMessage());
            if (cmds.size() == 0)
                return true;
            final String word = cmds.get(0).toUpperCase();
            boolean match = false;
            for (String[] cmd : commandPhrases) {
                if (cmd[0].equals(word)) {
                    for (int i = 1; i < cmd.length; i++) {
                        final String cmdI = cmd[i].toLowerCase();
                        final String cmdsI = cmds.get(i).toLowerCase();
                        if (cmdI.endsWith("**") && cmdsI.startsWith(cmdI.substring(0, cmdI.length() - 2))) {
                            match = true;
                            break;
                        } else if (cmdI.endsWith("*") && cmdsI.startsWith(cmdI.substring(0, cmdI.length() - 1))) {
                        } else if (!cmdsI.equals(cmdI)) {
                            match = false;
                            break;
                        }
                        if (i == cmd.length - 1) {
                            match = (i == cmds.size() - 1);
                        }
                    }
                }
                if (match) {
                    final Room R = mob.location();
                    if (affected instanceof Exit) {
                        final Exit E = (Exit) affected;
                        if (!E.isOpen()) {
                            int dirCode = -1;
                            for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                                if (R.getExitInDir(d) == E) {
                                    dirCode = d;
                                    break;
                                }
                            }
                            if (dirCode >= 0) {
                                CMMsg msg2 = CMClass.getMsg(mob, E, null, CMMsg.MSG_UNLOCK, null);
                                CMLib.utensils().roomAffectFully(msg2, R, dirCode);
                                msg2 = CMClass.getMsg(mob, E, null, CMMsg.MSG_OPEN, L("<T-NAME> opens."));
                                CMLib.utensils().roomAffectFully(msg2, R, dirCode);
                            }
                        }
                    } else if (affected instanceof Container) {
                        CMMsg msg2 = CMClass.getMsg(mob, affected, null, CMMsg.MSG_UNLOCK, null);
                        affected.executeMsg(mob, msg2);
                        msg2 = CMClass.getMsg(mob, affected, null, CMMsg.MSG_OPEN, L("<T-NAME> opens."));
                        affected.executeMsg(mob, msg2);
                    }
                    return false;
                }
            }
        }
        return super.okMessage(myHost, msg);
    }
}
