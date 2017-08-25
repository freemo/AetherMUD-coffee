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
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_UndeadInvisibility extends Prayer {
    private final static String localizedName = CMLib.lang().L("Invisibility to Undead");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Invisibility/Undead)");

    @Override
    public String ID() {
        return "Prayer_UndeadInvisibility";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_DEATHLORE;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
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
        if (affected == null)
            return;
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        if (mob.isInCombat()) {
            final MOB victim = mob.getVictim();
            if (victim.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead")) {
                final int xlvl = super.getXLEVELLevel(invoker());
                affectableStats.setArmor(affectableStats.armor() - 20 - (2 * xlvl));
            }
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && ((msg.amITarget(affected)))) {
            final MOB target = (MOB) msg.target();
            if ((!target.isInCombat())
                && (CMLib.flags().isUndead(msg.source()))
                && (msg.source().location() == target.location())
                && (msg.source().getVictim() != target)) {
                msg.source().tell(L("You don't see @x1", target.name(msg.source())));
                if (target.getVictim() == msg.source()) {
                    target.makePeace(true);
                    target.setVictim(null);
                    helpProficiency((MOB) affected, 0);
                }
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your invisibility to undead fades."));
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final MOB victim = mob.getVictim();
            if ((victim != null)
                && (victim.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead")))
                return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
        }
        return super.castingQuality(mob, target);
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) invisible to the undead.") : L("^S<S-NAME> @x1 for invisibility to the undead.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> @x1 for invisibility to the undead, but there is no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
