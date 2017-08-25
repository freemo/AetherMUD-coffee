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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Chant_Reincarnation extends Chant {
    private final static String localizedName = CMLib.lang().L("Reincarnation");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Reincarnation Geas)");
    Race newRace = null;

    @Override
    public String ID() {
        return "Chant_Reincarnation";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    protected int overrideMana() {
        return 200;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (newRace != null) {
            if (affected.name().indexOf(' ') > 0)
                affectableStats.setName(L("@x1 called @x2", CMLib.english().startWithAorAn(newRace.name()), affected.name()));
            else
                affectableStats.setName(L("@x1 the @x2", affected.name(), newRace.name()));
            final int oldAdd = affectableStats.weight() - affected.basePhyStats().weight();
            newRace.setHeightWeight(affectableStats, 'M');
            if (oldAdd > 0)
                affectableStats.setWeight(affectableStats.weight() + oldAdd);
        }
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (newRace != null)
            affectableStats.setMyRace(newRace);
    }

    @Override
    public void unInvoke() {
        super.unInvoke();
        if ((!this.canBeUninvoked) && (affected != null) && (affected.fetchEffect(ID()) == this))
            this.unInvoked = false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((tickID == Tickable.TICKID_MOB)
            && (tickDown != Integer.MAX_VALUE)) {
            if ((tickDown <= 1) && (!unInvoked)) {
                tickDown = -1;
                // undo the affects of this spell
                if (!(affected instanceof MOB))
                    return super.tick(ticking, tickID);
                final MOB mob = (MOB) affected;
                mob.tell(L("Your reincarnation geas is lifted as your form solidifies."));
                if (newRace != null) {
                    mob.baseCharStats().setMyRace(newRace);
                    newRace.setHeightWeight(mob.basePhyStats(), (char) mob.charStats().getStat(CharStats.STAT_GENDER));
                    mob.recoverPhyStats();
                    mob.recoverCharStats();
                    mob.recoverMaxState();
                }
                unInvoke();
                if (mob.location() != null)
                    mob.location().recoverRoomStats();
            }
            if (!super.canBeUninvoked) // called during bring-to-life, which is why its down here
            {
                if (CMLib.flags().isInTheGame(affected, true))
                    super.canBeUninvoked = true;
                else
                    tickDown--;
            }
        }
        return true;
    }

    public boolean isGolem(Race R) {
        final MOB M = CMClass.getFactoryMOB();
        R.affectPhyStats(M, M.phyStats());
        final boolean golem = CMLib.flags().isGolem(M);
        M.destroy();
        return golem;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return true;
        final MOB mob = (MOB) affected;
        if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
            && (msg.amISource(mob))) {
            newRace = null;
            while ((newRace == null)
                || (isGolem(newRace))
                || (!newRace.canBreedWith(newRace))
                || (!CMath.bset(newRace.availabilityCode(), Area.THEME_FANTASY))
                || (newRace == mob.charStats().getMyRace())
                || (newRace.ID().equals("StdRace")))
                newRace = CMClass.randomRace();
            if (newRace != null)
                mob.tell(L("You are being reincarnated as a @x1!!", newRace.name()));
            msg.source().recoverCharStats();
            msg.source().recoverPhyStats();
            super.canBeUninvoked = false; // without this, bring to life removes it
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget, false, true);
        if (target == null)
            return false;
        if (target.fetchEffect(ID()) != null) {
            if (mob.location().show(mob, target, null, CMMsg.MSG_CAST, L("<S-NAME> lift(s) the reincarnation geas on <T-NAMESELF>.")))
                target.delEffect(target.fetchEffect(ID()));
            else
                mob.location().show(mob, target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> fail(s) to lift the reincarnation geas on <T-NAMESELF>."));
            return false;
        }
        if (target.isMonster()) {
            mob.tell(L("Your chant would have no effect on such a creature."));
            return false;
        }

        boolean success = proficiencyCheck(mob, 0, auto);
        final Set<MOB> groupMembers = mob.getGroupMembers(new HashSet<MOB>());
        if (success && (!auto) && (mob != target) && (!mob.mayIFight(target)) && (!groupMembers.contains(target))) {
            mob.tell(L("@x1 is a player, so you must be group members, and your playerkill flags must be on for this to work.", target.name(mob)));
            success = false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if (success) {
            int modifier = 0;
            if ((target != mob) && (!groupMembers.contains(target)))
                modifier = CMMsg.MASK_MALICIOUS;
            final CMMsg msg = CMClass.getMsg(mob, target, this, modifier | verbalCastCode(mob, target, auto), L(auto ? "^S<S-NAME> get(s) put under a reincarnation geas!^?" : "^S<S-NAME> chant(s) a reincarnation geas upon <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 1800);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) for a reincarnation geas, but nothing happens."));

        return success;
    }
}
