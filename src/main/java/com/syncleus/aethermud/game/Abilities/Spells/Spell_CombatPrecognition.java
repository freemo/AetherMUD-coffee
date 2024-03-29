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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_CombatPrecognition extends Spell {

    private final static String localizedName = CMLib.lang().L("Combat Precognition");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Combat Precognition)");
    boolean lastTime = false;

    @Override
    public String ID() {
        return "Spell_CombatPrecognition";
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
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int overrideMana() {
        return 100;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (mob.location() != null)
            && (CMLib.flags().isAliveAwakeMobile(mob, true))) {
            if (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK) {
                final CMMsg msg2 = CMClass.getMsg(mob, msg.source(), null, CMMsg.MSG_QUIETMOVEMENT, L("<S-NAME> avoid(s) the attack by <T-NAME>!"));
                if ((proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 60, false))
                    && (!lastTime)
                    && (msg.source().getVictim() == mob)
                    && (msg.source().rangeToTarget() == 0)
                    && (mob.location().okMessage(mob, msg2))) {
                    lastTime = true;
                    mob.location().send(mob, msg2);
                    helpProficiency(mob, 0);
                    return false;
                }
                lastTime = false;
            } else if ((msg.value() <= 0)
                && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
                && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 50, false))) {
                String tool = null;
                if ((msg.tool() instanceof Ability))
                    tool = ((Ability) msg.tool()).name();
                CMMsg msg2 = null;
                switch (msg.targetMinor()) {
                    case CMMsg.TYP_JUSTICE:
                        if ((CMath.bset(msg.targetMajor(), CMMsg.MASK_MOVE))
                            && (tool != null))
                            msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", tool));
                        break;
                    case CMMsg.TYP_GAS:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "noxious fumes" : tool)));
                        break;
                    case CMMsg.TYP_COLD:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "cold blast" : tool)));
                        break;
                    case CMMsg.TYP_ELECTRIC:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "electrical attack" : tool)));
                        break;
                    case CMMsg.TYP_FIRE:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "blast of heat" : tool)));
                        break;
                    case CMMsg.TYP_WATER:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "weat blast" : tool)));
                        break;
                    case CMMsg.TYP_ACID:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "acid attack" : tool)));
                        break;
                    case CMMsg.TYP_SONIC:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "sonic attack" : tool)));
                        break;
                    case CMMsg.TYP_LASER:
                        msg2 = CMClass.getMsg(mob, msg.source(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> avoid(s) the @x1 from <T-NAME>.", ((tool == null) ? "laser attack" : tool)));
                        break;
                }
                if ((msg2 != null) && (mob.location() != null) && (mob.location().okMessage(mob, msg2))) {
                    mob.location().send(mob, msg2);
                    return false;
                }
            }
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

        mob.tell(L("Your combat precognition fades away."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> already <S-HAS-HAVE> the sight."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "<T-NAME> shout(s) combatively!" : "^S<S-NAME> shout(s) a combative spell!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> shout(s) combatively, but nothing more happens."));
        // return whether it worked
        return success;
    }
}
