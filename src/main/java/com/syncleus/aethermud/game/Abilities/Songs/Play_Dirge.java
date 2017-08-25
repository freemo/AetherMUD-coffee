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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.DeadBody;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Play_Dirge extends Play {
    private final static String localizedName = CMLib.lang().L("Dirge");

    @Override
    public String ID() {
        return "Play_Dirge";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected boolean persistantSong() {
        return false;
    }

    @Override
    protected boolean skipStandardSongTick() {
        return true;
    }

    @Override
    protected String songOf() {
        return CMLib.english().startWithAorAn(name());
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
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
        final Item target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if ((!(target instanceof DeadBody)) || (target.rawSecretIdentity().toUpperCase().indexOf("FAKE") >= 0)) {
            mob.tell(L("You may only play this for the dead."));
            return false;
        }
        if ((((DeadBody) target).isPlayerCorpse()) && (((DeadBody) target).hasContent())) {
            mob.tell(L("You may not play for that body"));
            return false;
        }

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
                    final Set<MOB> h = super.sendMsgAndGetTargets(mob, R, msg, givenTarget, auto);
                    if (h == null)
                        continue;

                    for (final Object element : h) {
                        final MOB follower = (MOB) element;

                        double exp = 10.0;
                        final int levelLimit = CMProps.getIntVar(CMProps.Int.EXPRATE);
                        final int levelDiff = follower.phyStats().level() - target.phyStats().level();
                        if (levelDiff > levelLimit)
                            exp = 0.0;
                        final int expGained = (int) Math.round(exp);

                        // malicious songs must not affect the invoker!
                        if (CMLib.flags().canBeHeardSpeakingBy(invoker, follower) && (expGained > 0))
                            CMLib.leveler().postExperience(follower, null, null, expGained, false);
                    }
                    R.recoverRoomStats();
                    R.showHappens(CMMsg.MSG_OK_VISUAL, L("@x1 fades away.", target.name()));
                    target.destroy();
                }
            }
        } else
            mob.location().show(mob, null, CMMsg.MSG_NOISE, L("<S-NAME> hit(s) a foul note."));

        return success;
    }
}
