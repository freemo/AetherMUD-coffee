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
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_DivineLuck extends Prayer {
    private final static String localizedName = CMLib.lang().L("Divine Luck");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Divine Luck)");

    @Override
    public String ID() {
        return "Prayer_DivineLuck";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        for (final int i : CharStats.CODES.SAVING_THROWS())
            affectableStats.setStat(i,
                affectableStats.getStat(i)
                    + 1 + ((affectedMOB.phyStats().level() + (2 * getXLEVELLevel(invoker()))) / 5));
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected == null)
            return;
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        final int xlvl = super.getXLEVELLevel(invoker());
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + ((affected.phyStats().level() + (2 * xlvl)) / 5) + 1);
        affectableStats.setDamage(affectableStats.damage() + ((affected.phyStats().level() + (2 * xlvl)) / 5) + 1);

        if (mob.isInCombat()) {
            final MOB victim = mob.getVictim();
            if (CMLib.flags().isEvil(victim))
                affectableStats.setArmor(affectableStats.armor() - 5 - xlvl);
        }
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your divine luck is over."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Physical target = mob;
        if ((auto) && (givenTarget != null))
            target = givenTarget;
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(mob, target, null, L("<T-NAME> <T-IS-ARE> already affected by @x1.", name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) divinely lucky.") : L("^S<S-NAME> @x1 for divine luck.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> @x1, but as luck would have it, there's no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
