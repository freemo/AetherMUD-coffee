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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_BloodMoon extends Prayer {
    private final static String localizedName = CMLib.lang().L("Blood Curse");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Blood Curse)");

    @Override
    public String ID() {
        return "Prayer_BloodMoon";
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
    public void unInvoke() {
        // undo the affects of this spell
        if (affected == null)
            return;
        if (canBeUninvoked()) {
            final Room R = CMLib.map().roomLocation(affected);
            if ((R != null) && (CMLib.flags().isInTheGame(affected, true)))
                R.showHappens(CMMsg.MSG_OK_VISUAL, L("The blood curse fades."));
        }
        super.unInvoke();
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
            int recovery = (int) Math.round(CMath.div((msg.value()), 10.0));
            if (recovery <= 0)
                recovery = 1;
            msg.setValue(msg.value() + recovery);
        }
        return true;
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 for <T-NAME> to feel pain.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("The Blood Curse inflicts <S-NAME>."));
                    maliciousAffect(mob, target, asLevel, 0, -1);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> @x1 for a Blood Curse, but <S-HIS-HER> plea is not answered.", prayWord(mob)));
        // return whether it worked
        return success;
    }
}
