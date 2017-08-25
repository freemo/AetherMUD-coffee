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
package com.syncleus.aethermud.game.Abilities.Paladin;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Rideable;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Paladin_PaladinsMount extends PaladinSkill {
    private final static String localizedName = CMLib.lang().L("Paladin`s Mount");
    protected boolean pass = false;

    public Paladin_PaladinsMount() {
        super();
    }

    @Override
    public String ID() {
        return "Paladin_PaladinsMount";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        final Physical affected = super.affected;
        final MOB invoker = super.invoker();
        if ((affected instanceof Rideable)
            && (affected instanceof MOB)
            && (invoker != null)
            && (affected != invoker)) {
            final MOB affectedMob = (MOB) affected;
            if ((!((Rideable) affected).amRiding(invoker))
                || (affectedMob.amDead())
                || (CMLib.flags().isEvil(affectedMob))
                || (!CMLib.flags().isGood(invoker))) {
                affected.delEffect(this);
                affected.recoverPhyStats();
                affectedMob.recoverCharStats();
                affectedMob.recoverMaxState();
            }
        } else if ((affected == invoker)
            && (invoker != null)
            && (CMLib.flags().isGood(invoker))
            && (invoker.riding() != null)
            && (invoker.riding() instanceof MOB)
            && (((MOB) invoker.riding()).charStats().getMyRace().racialCategory().equals("Equine"))
            && (!CMLib.flags().isEvil(invoker.riding()))
            && (invoker.riding().fetchEffect(ID()) == null)) {
            invoker.riding().addEffect((Paladin_PaladinsMount) copyOf());
            invoker.riding().recoverPhyStats();
            ((MOB) invoker.riding()).recoverCharStats();
            ((MOB) invoker.riding()).recoverMaxState();
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if ((invoker != null)
            && (affected != invoker)) {
            affectableStats.setStat(CharStats.STAT_SAVE_DISEASE, affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 50 + proficiency() + (5 * getXLEVELLevel(invoker())));
            affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 50 + proficiency() + (5 * getXLEVELLevel(invoker())));
            final int amount = (int) Math.round(CMath.mul(CMath.div(proficiency(), 100.0), affected.phyStats().level() + (2 * getXLEVELLevel(invoker))));
            for (final int i : CharStats.CODES.SAVING_THROWS())
                affectableStats.setStat(i, affectableStats.getStat(i) + amount);
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((invoker == null)
            || (!(CMLib.flags().isGood(invoker)))
            || (affected == null)
            || (affected == invoker)
            || (!(affected instanceof MOB)))
            return true;

        if (msg.target() == affected) {
            if (msg.tool() instanceof Ability) {
                if ((CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
                    && (msg.targetMinor() == CMMsg.TYP_CAST_SPELL)
                    && (!CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_HOLY))
                    && (CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_UNHOLY))
                    && (proficiencyCheck(invoker, 0, false))) {
                    msg.source().location().show((MOB) msg.target(), null, CMMsg.MSG_OK_VISUAL, L("The holy field around <S-NAME> protect(s) <S-HIM-HER> from the evil magic attack of @x1.", msg.source().name()));
                    return false;
                }
                final String str1 = msg.tool().ID().toUpperCase();
                if (((str1.indexOf("SPOOK") >= 0) || (str1.indexOf("NIGHTMARE") >= 0) || (str1.indexOf("FEAR") >= 0))
                    && (proficiencyCheck(invoker, 0, false))) {
                    final MOB mob = (MOB) msg.target();
                    mob.location().show(invoker, mob, CMMsg.MSG_OK_VISUAL, L("<T-YOUPOSS> courage protects you from the @x1 attack.", msg.tool().name()));
                    return false;
                }
            }
            if (((msg.targetMinor() == CMMsg.TYP_POISON) || (msg.targetMinor() == CMMsg.TYP_DISEASE)) && (proficiencyCheck(invoker, 0, false)))
                return false;
        }
        return true;
    }
}
