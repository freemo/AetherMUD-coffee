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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Prayer_DrunkenStupor extends Prayer {
    private final static String localizedName = CMLib.lang().L("Drunken Stupor");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Drunken Stupor)");
    public Ability inebriation = null;

    @Override
    public String ID() {
        return "Prayer_DrunkenStupor";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL | Ability.FLAG_INTOXICATING;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    protected Ability getInebriation() {
        if (inebriation == null) {
            inebriation = CMClass.getAbility("Inebriation");
            inebriation.makeLongLasting();
            inebriation.makeNonUninvokable();
            inebriation.setAffectedOne(affected);
        }
        return inebriation;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected instanceof MOB)
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
                - ((MOB) affected).phyStats().level()
                - (2 * getXLEVELLevel(invoker())));
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_DEXTERITY, (affectableStats.getStat(CharStats.STAT_DEXTERITY) - 3));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        final Ability A = getInebriation();
        if (A != null)
            A.tick(ticking, tickID);

        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        final Ability A = getInebriation();
        if (A != null)
            A.executeMsg(myHost, msg);
        super.executeMsg(myHost, msg);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        final Ability A = getInebriation();
        if ((A == null) || (!A.okMessage(myHost, msg)))
            return false;

        if (msg.source() != affected)
            return true;
        if (msg.source().location() == null)
            return true;
        if ((!msg.targetMajor(CMMsg.MASK_ALWAYS))
            && (msg.targetMajor() > 0)) {
            if ((msg.target() != null)
                && (msg.target() instanceof MOB))
                msg.modify(msg.source(), msg.source().location().fetchInhabitant(CMLib.dice().roll(1, msg.source().location().numInhabitants(), 0) - 1), msg.tool(), msg.sourceCode(), msg.sourceMessage(), msg.targetCode(), msg.targetMessage(), msg.othersCode(), msg.othersMessage());
        }
        return true;
    }

    @Override
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked())
            mob.tell(L("You feel sober now."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> @x1 to inflict a drunken stupor upon <T-NAMESELF>.^?", prayForWord(mob)));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg2);
                if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                    invoker = mob;
                    maliciousAffect(mob, target, asLevel, 0, -1);
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> look(s) a bit tipsy!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> @x1 to inflict a drunken stupor upon <T-NAMESELF>, but flub(s) it.", prayForWord(mob)));

        // return whether it worked
        return success;
    }
}
