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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_Poison extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Deprecated Poison");
    private static final String[] triggerStrings = I(new String[]{"DOPOISON"});

    // **
    // This is a deprecated skill provided only
    // for backwards compatibility.  It has been
    // replaced with Thief_UsePoison
    // **
    @Override
    public String ID() {
        return "Thief_Poison";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_POISONING;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        String str = null;
        if (success) {
            str = auto ? "" : L("^F^<FIGHT^><S-NAME> attempt(s) to poison <T-NAMESELF>!^</FIGHT^>^?");
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_THIEF_ACT, str, CMMsg.MSK_MALICIOUS_MOVE | CMMsg.MSG_THIEF_ACT | (auto ? CMMsg.MASK_ALWAYS : 0), str, CMMsg.MSG_NOISYMOVEMENT, str);
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | CMMsg.TYP_POISON, null, CMMsg.MASK_MALICIOUS | CMMsg.TYP_POISON | (auto ? CMMsg.MASK_ALWAYS : 0), null, CMMsg.NO_EFFECT, null);
            CMLib.color().fixSourceFightColor(msg);
            final Room R = mob.location();
            if (R.okMessage(mob, msg) && R.okMessage(mob, msg2)) {
                R.send(mob, msg);
                R.send(mob, msg2);
                if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                    final Ability A = CMClass.getAbility("Poison");
                    A.invoke(mob, target, true, asLevel);
                    if (target.fetchEffect("Poison") != null)
                        mob.tell(L("@x1 is now poisoned.", target.name()));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to poison <T-NAMESELF>, but fail(s)."));

        return success;
    }
}
