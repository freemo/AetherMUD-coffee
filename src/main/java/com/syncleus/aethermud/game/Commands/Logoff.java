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
import com.syncleus.aethermud.game.core.*;

import java.util.List;


public class Logoff extends StdCommand {
    private final String[] access = I(new String[]{"LOGOFF", "LOGOUT"});

    public Logoff() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(final MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (mob.soulMate() != null)
            Quit.dispossess(mob, CMParms.combine(commands).endsWith("!"));
        else if (!mob.isMonster()) {
            final Session session = mob.session();
            if ((session != null)
                && (session.getLastPKFight() > 0)
                && ((System.currentTimeMillis() - session.getLastPKFight()) < (5 * 60 * 1000))
                && (!CMSecurity.isASysOp(mob))) {
                mob.tell(L("You must wait a few more minutes before you are allowed to logout."));
                return false;
            }
            try {
                if (session != null) {
                    session.prompt(new InputCallback(InputCallback.Type.CONFIRM, "N", 30000) {
                        @Override
                        public void showPrompt() {
                            session.promptPrint(L("\n\rLogout -- are you sure (y/N)?"));
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
                                    session.logout(true); // this should call prelogout and later loginlogoutthread to cause msg SEND
                                    CMLib.commands().monitorGlobalMessage(R, msg);
                                }
                            }
                        }
                    });
                }
            } catch (final Exception e) {
                Log.errOut("Logoff", e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return false;
    }

}
