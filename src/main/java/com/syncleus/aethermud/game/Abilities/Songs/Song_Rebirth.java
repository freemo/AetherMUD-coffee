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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Song_Rebirth extends Song {
    private final static String localizedName = CMLib.lang().L("Rebirth");

    @Override
    public String ID() {
        return "Song_Rebirth";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    protected boolean skipStandardSongInvoke() {
        return true;
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        timeOut = 0;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if ((!auto) && (!CMLib.flags().canSpeak(mob))) {
            mob.tell(L("You can't sing!"));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);
        unsingAllByThis(mob, mob);
        if (success) {
            invoker = mob;
            originRoom = mob.location();
            commonRoomSet = getInvokerScopeRoomSet(null);
            String str = auto ? L("The @x1 begins to play!", songOf()) : L("^S<S-NAME> begin(s) to sing the @x1.^?", songOf());
            if ((!auto) && (mob.fetchEffect(this.ID()) != null))
                str = L("^S<S-NAME> start(s) the @x1 over again.^?", songOf());

            for (int v = 0; v < commonRoomSet.size(); v++) {
                final Room R = commonRoomSet.get(v);
                final String msgStr = getCorrectMsgString(R, str, v);
                final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), msgStr);
                if (R.okMessage(mob, msg)) {
                    if (R == originRoom)
                        R.send(mob, msg);
                    else
                        R.sendOthers(mob, msg);
                    final boolean foundOne = false;
                    int i = 0;
                    while (i < R.numItems()) {
                        final Item body = R.getItem(i);
                        if ((body instanceof DeadBody)
                            && (((DeadBody) body).isPlayerCorpse())
                            && (((DeadBody) body).getMobName().length() > 0)
                            && (CMLib.players().playerExists(((DeadBody) body).getMobName()))) {
                            if (!CMLib.utensils().resurrect(mob, R, (DeadBody) body, -1))
                                i++;
                        } else
                            i++;
                    }
                    if (!foundOne)
                        mob.tell(L("Nothing seems to happen."));
                }
            }
        } else
            mob.location().show(mob, null, CMMsg.MSG_NOISE, L("<S-NAME> hit(s) a foul note."));

        return success;
    }
}
