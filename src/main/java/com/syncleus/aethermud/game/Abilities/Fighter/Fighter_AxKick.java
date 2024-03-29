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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_AxKick extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Ax Kick");
    private static final String[] triggerStrings = I(new String[]{"AXKICK"});

    @Override
    public String ID() {
        return "Fighter_AxKick";
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
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_KICKING;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null)) {
            if (mob.isInCombat() && (mob.rangeToTarget() > 0))
                return Ability.QUALITY_INDIFFERENT;
            if (mob.charStats().getBodyPart(Race.BODY_LEG) <= 0)
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.isInCombat() && (mob.rangeToTarget() > 0)) {
            mob.tell(L("You are too far away to kick!"));
            return false;
        }
        if (mob.charStats().getBodyPart(Race.BODY_LEG) <= 0) {
            mob.tell(L("You need legs to do this."));
            return false;
        }

        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, (mob.charStats().getStat(CharStats.STAT_DEXTERITY) - target.charStats().getStat(CharStats.STAT_DEXTERITY)) * 2, auto);
        if (success) {
            invoker = mob;
            final int topDamage = adjustedLevel(mob, asLevel) + 10;
            int damage = CMLib.dice().roll(1, topDamage, 0);
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() > 0)
                    damage = (int) Math.round(CMath.div(damage, 2.0));
                CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, Weapon.TYPE_BASHING, L("^F^<FIGHT^><S-NAME> <DAMAGE> <T-NAME> with a ferocious AX KICK!^</FIGHT^>^?"));
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> fail(s) to ax kick <T-NAMESELF>."));

        // return whether it worked
        return success;
    }
}
