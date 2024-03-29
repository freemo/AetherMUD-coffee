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


public class Prayer_DarkeningAura extends Prayer {
    private final static String localizedName = CMLib.lang().L("Darkening Aura");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Darkening Aura)");

    @Override
    public String ID() {
        return "Prayer_DarkeningAura";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
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
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your darkening aura fades."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return true;
        if (!(myHost instanceof MOB))
            return true;
        final MOB myChar = (MOB) myHost;

        if (msg.amISource(myChar)
            && (!myChar.isMonster())
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.tool() instanceof Ability)
            && (CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_UNHOLY))
            && (!CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_HOLY))
            && (CMLib.ableMapper().getQualifyingLevel(ID(), true, msg.tool().ID()) > 0)
            && (myChar.isMine(msg.tool()))
            && (msg.value() > 0))
            msg.setValue((int) Math.round(CMath.mul(msg.value(), 1.5)));
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = getTarget(mob, commands, givenTarget);
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target == null)
            return false;

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already surrounded by a darkening aura."));
            return false;
        }

        Room R = CMLib.map().roomLocation(target);
        if (R == null)
            R = mob.location();

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> @x1 for a dark aura upon <T-NAMESELF>.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> surrounded by a dark aura!"));
                    maliciousAffect(mob, target, asLevel, 0, -1);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> @x1 for a darkening aura, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
