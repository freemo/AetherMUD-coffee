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
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.Common.interfaces.Session.InputCallback;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;

import java.util.List;


public class Quit extends StdCommand {
    private final String[] access = I(new String[]{"QUIT", "QUI", "Q"});

    public Quit() {
    }

    public static void dispossess(MOB mob, boolean force) {
        if (mob.soulMate() == null) {
            mob.tell(CMLib.lang().L("Huh?"));
            return;
        }
        final CMMsg msg = CMClass.getMsg(mob, CMMsg.MSG_DISPOSSESS, CMLib.lang().L("^H<S-YOUPOSS> spirit has returned to <S-YOUPOSS> body...\n\r\n\r^N"));
        final Room room = mob.location();
        if ((room == null) || (room.okMessage(mob, msg)) || force) {
            if (room != null)
                room.send(mob, msg);
            mob.dispossess(true);
        }
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(final MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (mob.soulMate() != null)
            dispossess(mob, CMParms.combine(commands).endsWith("!"));
        else if (!mob.isMonster()) {
            final Session session = mob.session();
            if (session != null) {
                if ((session.getLastPKFight() > 0)
                    && ((System.currentTimeMillis() - session.getLastPKFight()) < (5 * 60 * 1000))
                    && (!CMSecurity.isASysOp(mob))) {
                    mob.tell(L("You must wait a few more minutes before you are allowed to quit."));
                    return false;
                }
                session.prompt(new InputCallback(InputCallback.Type.CONFIRM, "N", 30000) {
                    @Override
                    public void showPrompt() {
                        session.promptPrint(L("\n\rQuit -- are you sure (y/N)?"));
                    }

                    @Override
                    public void timedOut() {
                    }

                    @Override
                    public void callBack() {
                        if (this.confirmed) {
                            final CMMsg msg = CMClass.getMsg(mob, null, CMMsg.MSG_QUIT, null);
                            final Room R = mob.location();
                            if ((R != null) && (R.okMessage(mob, msg))) {
                                CMLib.map().sendGlobalMessage(mob, CMMsg.TYP_QUIT, msg);
                                session.stopSession(false, false, false); // this should call prelogout and later loginlogoutthread to cause msg SEND
                                CMLib.commands().monitorGlobalMessage(R, msg);
                            }
                        }
                    }
                });
            }
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return false;
    }

}
