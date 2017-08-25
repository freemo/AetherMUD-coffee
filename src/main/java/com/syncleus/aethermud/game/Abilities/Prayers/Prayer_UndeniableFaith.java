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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.Deity;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.collections.DVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Prayer_UndeniableFaith extends Prayer {
    private final static String localizedName = CMLib.lang().L("Undeniable Faith");
    private static DVector convertStack = new DVector(2);
    protected String godName = "";

    @Override
    public String ID() {
        return "Prayer_UndeniableFaith";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL | Ability.FLAG_CHARMING;
    }

    @Override
    protected int overrideMana() {
        return 100;
    }

    @Override
    public void unInvoke() {
        final MOB M = (MOB) affected;
        super.unInvoke();
        if (canBeUninvoked())
            M.tell(L("Your compelled faith is finally subsided."));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return true;
        final MOB M = (MOB) affected;
        if (M.location() != null) {
            if ((!M.getWorshipCharID().equals(godName))
                && (godName.length() > 0)) {
                final Deity D = CMLib.map().getDeity(godName);
                if (M.getWorshipCharID().length() > 0) {
                    final Deity D2 = CMLib.map().getDeity(M.getWorshipCharID());
                    if (D2 != null) {
                        final CMMsg msg2 = CMClass.getMsg(M, D2, this, CMMsg.MSG_REBUKE, null);
                        if (M.location().okMessage(M, msg2))
                            M.location().send(M, msg2);
                    }
                }
                final CMMsg msg2 = CMClass.getMsg(M, D, this, CMMsg.MSG_SERVE, null);
                if (M.location().okMessage(M, msg2)) {
                    M.location().send(M, msg2);
                    M.setWorshipCharID(godName);
                }
            }
        }
        return true;
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((affected instanceof MOB)
            && (msg.amISource((MOB) affected))
            && (msg.sourceMinor() == CMMsg.TYP_REBUKE)
            && (msg.target() != null)
            && ((msg.target() == invoker()) || (msg.target().Name().equals(godName)))) {
            msg.source().tell(L("Your faith is too undeniable."));
            return false;
        }
        return super.okMessage(host, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if ((mob.getWorshipCharID().length() == 0)
            || (CMLib.map().getDeity(mob.getWorshipCharID()) == null)) {
            if (!auto)
                mob.tell(L("You must worship a god to use this prayer."));
            return false;
        }
        final Deity D = CMLib.map().getDeity(mob.getWorshipCharID());
        if ((target.getWorshipCharID().length() > 0)
            && (CMLib.map().getDeity(target.getWorshipCharID()) != null)) {
            if (!auto)
                mob.tell(L("@x1 worships @x2, and may not be converted with this prayer.", target.name(mob), target.getWorshipCharID()));
            return false;
        }
        if ((CMLib.flags().isAnimalIntelligence(target) || CMLib.flags().isGolem(target) || (D == null))) {
            if (!auto)
                mob.tell(L("@x1 can not be converted with this prayer.", target.name(mob)));
            return false;
        }
        if (!auto) {
            if (convertStack.contains(target)) {
                final Long L = (Long) convertStack.elementAt(convertStack.indexOf(target), 2);
                if ((System.currentTimeMillis() - L.longValue()) > CMProps.getMillisPerMudHour() * 5)
                    convertStack.removeElement(target);
            }
            if (convertStack.contains(target)) {
                mob.tell(L("@x1 must wait to be undeniably faithful again.", target.name(mob)));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff < 0)
            levelDiff = 0;
        final boolean success = proficiencyCheck(mob, -(levelDiff * 25), auto);
        int type = verbalCastCode(mob, target, auto);
        int mal = CMMsg.MASK_MALICIOUS;
        if (auto) {
            type = CMath.unsetb(type, CMMsg.MASK_MALICIOUS);
            mal = 0;
        }
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, type, auto ? "" : L("^S<S-NAME> @x1 for <T-NAMESELF> to BELIEVE!^?", prayWord(mob)));
            final CMMsg msg2 = CMClass.getMsg(target, D, this, CMMsg.MSG_SERVE, L("<S-NAME> BELIEVE(S) !!!"));
            final CMMsg msg3 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_VERBAL | mal | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg))
                && (mob.location().okMessage(mob, msg3))
                && (mob.location().okMessage(mob, msg2))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg3);
                if ((msg.value() <= 0) && (msg3.value() <= 0)) {
                    target.location().send(target, msg2);
                    target.setWorshipCharID(godName);
                    if (mob != target)
                        CMLib.leveler().postExperience(mob, target, null, 25, false);
                    godName = mob.getWorshipCharID();
                    beneficialAffect(mob, target, asLevel, CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH));
                    convertStack.addElement(target, Long.valueOf(System.currentTimeMillis()));
                }
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
