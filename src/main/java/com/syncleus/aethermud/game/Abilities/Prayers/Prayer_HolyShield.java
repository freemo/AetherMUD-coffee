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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_HolyShield extends Prayer {
    private final static String localizedName = CMLib.lang().L("Holy Shield");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Holy Shield)");

    @Override
    public String ID() {
        return "Prayer_HolyShield";
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
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;

        affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD, 100);
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((msg.target() == affected)
            && ((msg.sourceMinor() == CMMsg.TYP_UNDEAD) || (msg.targetMinor() == CMMsg.TYP_UNDEAD))
            && (msg.source().location() != null)) {
            msg.source().location().show((MOB) msg.target(), msg.source(), this, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> holy shield block(s) the unholy magic from <T-NAMESELF>."));
            return false;
        }
        return super.okMessage(host, msg);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> holy shield fades."));
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "<T-NAME> become(s) protected by the holy shield." : "^S<S-NAME> " + prayWord(mob) + " for <T-NAMESELF> to be protected by the holy shield.^?") + CMLib.protocol().msp("bless.wav", 10));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
                target.recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for a holy shield, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
