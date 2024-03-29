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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Prayer_SunCurse extends Prayer {
    private final static String localizedName = CMLib.lang().L("Sun Curse");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Sun Curse)");

    @Override
    public String ID() {
        return "Prayer_SunCurse";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
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

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (!(affected instanceof MOB))
            return;
        if (((MOB) affected).location() == null)
            return;
        if (CMLib.flags().isInDark(((MOB) affected).location()))
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
        else
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SEE);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);
        final MOB mob = (MOB) affected;
        if ((mob.location() != null)
            && (mob.location().getArea().getClimateObj().canSeeTheSun(mob.location()))
            && (CMLib.flags().isInTheGame(mob, false))) {
            mob.tell(L("\n\r\n\r\n\r\n\r**THE SUN IS BEATING ONTO YOUR SKIN**\n\r\n\r"));
            final Ability A = CMClass.getAbility("Spell_FleshStone");
            if (A != null)
                A.invoke(mob, mob, true, 0);
            unInvoke();
            return false;
        }
        return true;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if ((canBeUninvoked()) && (mob.fetchEffect("Spell_FleshStone") == null))
            mob.tell(L("Your sun curse is lifted."));
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!mob.location().getArea().getClimateObj().canSeeTheSun(mob.location()))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if ((!auto)
            && (target.location() != null)
            && (target.location().getArea().getClimateObj().canSeeTheSun(target.location()))) {
            mob.tell(L("This cannot be prayed for while the sun is shining on you."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final int adjustment = target.phyStats().level() - ((mob.phyStats().level() + super.getXLEVELLevel(mob)) / 2);
        boolean success = proficiencyCheck(mob, -adjustment, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 for an unholy sun curse upon <T-NAMESELF>.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> under a mighty sun curse!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to sun curse <T-NAMESELF>, but flub(s) it."));

        // return whether it worked
        return success;
    }
}
