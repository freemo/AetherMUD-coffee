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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Spell_Fear extends Spell {

    private final static String localizedName = CMLib.lang().L("Fear");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Afraid)");

    @Override
    public String ID() {
        return "Spell_Fear";
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
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_TRANSPORTING;
    }

    @Override
    public void unInvoke() {
        MOB M = null;
        final MOB oldI = invoker;
        if (affected instanceof MOB)
            M = (MOB) affected;
        super.unInvoke();
        if (M != null) {
            if (!M.isMonster())
                CMLib.commands().postStand(M, true);
            if ((oldI != M) && (oldI != null))
                M.tell(M, oldI, null, L("You are no longer afraid of <T-NAMESELF>."));
            else
                M.tell(L("You are no longer afraid."));
            if (M.isMonster())
                CMLib.tracking().wanderAway(M, false, true);
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected instanceof MOB)
            && (invoker != null)
            && (invoker != affected)
            && ((((MOB) affected).location() == null)
            || (!((MOB) affected).location().isInhabitant(invoker)))) {
            unInvoke();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectPhyStats(Physical E, PhyStats stats) {
        if ((affected instanceof MOB) && (invoker != null) && (invoker != affected) && (((MOB) affected).getVictim() == invoker)) {
            final int xlvl = super.getXLEVELLevel(invoker());
            final float f = (float) 0.05 * xlvl;
            stats.setArmor(stats.armor() + 30 + (3 * xlvl));
            stats.setAttackAdjustment((int) Math.round(CMath.mul(stats.attackAdjustment(), 0.90 - f)));
            stats.setDamage((int) Math.round(CMath.mul(stats.damage(), 0.90 - f)));
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            if (!auto)
                mob.tell(L("There doesn't appear to be anyone here worth scaring."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            for (final Object element : h) {
                final MOB target = (MOB) element;

                final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> scare(s) <T-NAMESELF>.^?"));
                final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                if (((text().toUpperCase().indexOf("WEAK") < 0) || ((mob.phyStats().level() / 2) > target.phyStats().level()))
                    && ((mob.location().okMessage(mob, msg)) && ((mob.location().okMessage(mob, msg2))))) {
                    mob.location().send(mob, msg);
                    if (msg.value() <= 0) {
                        mob.location().send(mob, msg2);
                        if (msg2.value() <= 0) {
                            invoker = mob;
                            CMLib.commands().postFlee(target, "");
                        }
                    }
                }
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> attempt(s) a frightening spell, but completely flub(s) it."));

        // return whether it worked
        return success;
    }
}
