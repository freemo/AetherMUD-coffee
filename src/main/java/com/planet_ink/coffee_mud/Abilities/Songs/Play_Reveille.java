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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.Directions;
import com.planet_ink.coffee_mud.core.interfaces.MUDCmdProcessor;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;


public class Play_Reveille extends Play {
    private final static String localizedName = CMLib.lang().L("Reveille");

    @Override
    public String ID() {
        return "Play_Reveille";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected boolean skipStandardSongTick() {
        return true;
    }

    @Override
    protected boolean skipStandardSongInvoke() {
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        timeOut = 0;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);
        unplayAll(mob, mob);
        if (success) {
            invoker = mob;
            originRoom = mob.location();
            commonRoomSet = getInvokerScopeRoomSet(null);
            String str = auto ? L("^S@x1 begins to play!^?", songOf()) : L("^S<S-NAME> begin(s) to play @x1 on @x2.^?", songOf(), instrumentName());
            if ((!auto) && (mob.fetchEffect(this.ID()) != null))
                str = L("^S<S-NAME> start(s) playing @x1 on @x2 again.^?", songOf(), instrumentName());

            for (int v = 0; v < commonRoomSet.size(); v++) {
                final Room R = commonRoomSet.elementAt(v);
                final String msgStr = getCorrectMsgString(R, str, v);
                final CMMsg msg = CMClass.getMsg(mob, null, this, somanticCastCode(mob, null, auto), msgStr);
                if (R.okMessage(mob, msg)) {
                    if (originRoom == R)
                        R.send(mob, msg);
                    else
                        R.sendOthers(mob, msg);
                    invoker = mob;
                    final HashSet<MOB> h = new HashSet<MOB>();
                    for (int i = 0; i < R.numInhabitants(); i++) {
                        final MOB M = R.fetchInhabitant(i);
                        h.add(M);
                    }

                    for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                        final Room R3 = R.getRoomInDir(d);
                        if ((R3 != null) && (!commonRoomSet.contains(R3))) {
                            for (int i = 0; i < R3.numInhabitants(); i++) {
                                final MOB M = R3.fetchInhabitant(i);
                                h.add(M);
                            }
                        }
                    }

                    for (final MOB follower : h) {
                        final Room R2 = follower.location();

                        // malicious songs must not affect the invoker!
                        int affectType = CMMsg.MSG_CAST_SOMANTIC_SPELL;
                        if (auto)
                            affectType = affectType | CMMsg.MASK_ALWAYS;
                        if ((CMLib.flags().canBeHeardSpeakingBy(invoker, follower) && (follower.fetchEffect(this.ID()) == null))) {
                            final CMMsg msg2 = CMClass.getMsg(mob, follower, this, affectType, null);
                            if (R2.okMessage(mob, msg2)) {
                                R2.send(follower, msg2);
                                if (CMLib.flags().isSleeping(follower)) {
                                    follower.doCommand(CMParms.parse("WAKE"), MUDCmdProcessor.METAFLAG_FORCED);
                                    if (!CMLib.flags().isSleeping(follower)) {
                                        final Ability A = CMClass.getAbility("Searching");
                                        if (A != null)
                                            A.invoke(follower, null, true, asLevel);
                                    }
                                }
                            }
                        }
                    }
                    R.recoverRoomStats();
                }
            }
        } else
            mob.location().show(mob, null, CMMsg.MSG_NOISE, L("<S-NAME> hit(s) a foul note."));

        return success;
    }
}
