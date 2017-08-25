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
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Prayer_ProtUndead extends Prayer {
    private final static String localizedName = CMLib.lang().L("Protection Undead");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Protection/Undead)");

    @Override
    public String ID() {
        return "Prayer_ProtUndead";
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
        return Ability.QUALITY_OK_SELF;
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
        return Ability.CAN_MOBS;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(affected instanceof MOB))
            return false;

        if (invoker == null)
            return false;

        final MOB mob = (MOB) affected;

        if (mob.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead")) {
            final int damage = (int) Math.round(CMath.div(mob.phyStats().level() + (2 * getXLEVELLevel(invoker())), 3.0));
            final MOB invoker = (invoker() != null) ? invoker() : mob;
            CMLib.combat().postDamage(invoker, mob, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, Weapon.TYPE_BURSTING, L("^S<T-HIS-HER> protective aura <DAMAGE> <T-NAME>!^?"));
        }
        return super.tick(ticking, tickID);
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
                affectableStats.setArmor(affectableStats.armor() - 50 - (5 * xlvl));
            }
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
            mob.tell(L("Your protection from undead fades."));
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final MOB victim = mob.getVictim();
            if ((victim != null)
                && (victim.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead"))
                && (!mob.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead")))
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) protected from the undead.") : L("^S<S-NAME> @x1 for protection from the undead.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> @x1 for protection, but there is no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
