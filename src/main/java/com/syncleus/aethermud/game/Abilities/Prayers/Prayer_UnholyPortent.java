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
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_UnholyPortent extends Prayer {
    private final static String localizedName = CMLib.lang().L("Unholy Portent");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Unholy Portent)");

    @Override
    public String ID() {
        return "Prayer_UnholyPortent";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
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
    public void unInvoke() {
        // undo the affects of this spell
        final MOB mob = (MOB) affected;
        if (canBeUninvoked() && (mob != null))
            mob.tell(L("The unholy portent curse is lifted."));
        super.unInvoke();
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.source() == affected)
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (msg.target() instanceof MOB)
            && (CMLib.flags().isEvil((MOB) msg.target()))) {
            final MOB M = (MOB) msg.target();
            final MOB invoker = (invoker() != null) ? invoker() : M;
            final int damage = CMLib.dice().roll(1, 3 + (invoker.phyStats().level() / 10), 0);
            CMLib.combat().postDamage(invoker, M, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD, Weapon.TYPE_STRIKING, L("The unholy portent curse <DAMAGE> <T-NAME>!"));
            CMLib.combat().postRevengeAttack(M, invoker);
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? L("<T-NAME> gain(s) an unholy portent!") : L("^S<S-NAME> curse(s) <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                    target.recoverPhyStats();
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to curse <T-NAMESELF>, but nothing happens."));
        // return whether it worked
        return success;
    }
}
