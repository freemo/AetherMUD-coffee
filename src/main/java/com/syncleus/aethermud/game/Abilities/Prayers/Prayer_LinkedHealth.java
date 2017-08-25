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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;


public class Prayer_LinkedHealth extends Prayer {
    private final static String localizedName = CMLib.lang().L("Linked Health");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Linked Health)");
    MOB buddy = null;

    @Override
    public String ID() {
        return "Prayer_LinkedHealth";
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
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HEALING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked()) {
            if (buddy != null) {
                mob.tell(L("Your health is no longer linked with @x1.", buddy.name()));
                final Ability A = buddy.fetchEffect(ID());
                if (A != null)
                    A.unInvoke();
            }
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;
        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)) {
            if ((msg.tool() == null) || (!msg.tool().ID().equals(ID()))) {
                final int recovery = (int) Math.round(CMath.div((msg.value()), 2.0));
                msg.setValue(recovery);
                CMLib.combat().postDamage(msg.source(), buddy, this, recovery, CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, Weapon.TYPE_BURSTING, L("<T-NAME> absorb(s) damage from the harm to @x1.", msg.target().name()));
            }
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if (mob.fetchEffect(ID()) != null) {
            mob.tell(L("Your health is already linked with someones!"));
            return false;
        }
        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("@x1's health is already linked with someones!", target.name(mob)));
            return false;
        }

        if (!mob.getGroupMembers(new HashSet<MOB>()).contains(target)) {
            mob.tell(L("@x1 is not in your group.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 that <S-HIS-HER> health be linked with <T-NAME>.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<S-NAME> and <T-NAME> are linked in health."));
                buddy = mob;
                beneficialAffect(mob, target, asLevel, 0);
                buddy = target;
                beneficialAffect(mob, mob, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for a link with <T-NAME>, but <S-HIS-HER> plea is not answered.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
