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
package com.planet_ink.coffee_mud.Abilities.Skills;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Skill_TurnUndead extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Turn Undead");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Turned)");
    private static final String[] triggerStrings = I(new String[]{"TURN"});

    @Override
    public String ID() {
        return "Skill_TurnUndead";
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
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_DEATHLORE;
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
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null)) {
            if (!(target instanceof MOB))
                return Ability.QUALITY_INDIFFERENT;
            final MOB targetM = (MOB) target;
            if ((targetM.baseCharStats().getMyRace() == null)
                || (!targetM.baseCharStats().getMyRace().racialCategory().equals("Undead")))
                return Ability.QUALITY_INDIFFERENT;
            if (CMLib.flags().isEvil(mob))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if ((target.baseCharStats().getMyRace() == null)
            || (!target.baseCharStats().getMyRace().racialCategory().equals("Undead"))) {
            mob.tell(auto ? L("Only the undead can be turned.") : L("You can only turn the undead."));
            return false;
        }

        if (CMLib.flags().isEvil(mob)) {
            mob.tell(L("Only the riteous may turn the undead."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelAdj = 0;
        final Room R = mob.location();
        if ((R != null) && (R.getArea() != null)) {
            String value = R.getArea().getBlurbFlag(ID());
            if ((value != null) && (value.length() > 0)) {
                for (String s : CMParms.parse(value)) {
                    if (s.startsWith("+") && (CMath.isNumber(value.substring(1))))
                        levelAdj = CMath.s_int(value.substring(1));
                    else if (CMath.isNumber(s))
                        levelAdj = CMath.s_int(value.trim());
                }
            }
        }

        final boolean success = proficiencyCheck(mob, ((mob.phyStats().level() + (4 * levelAdj) + (4 * getXLEVELLevel(mob))) - target.phyStats().level()) * 30, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_CAST_ATTACK_SOMANTIC_SPELL | (auto ? CMMsg.MASK_ALWAYS : 0), auto ? L("<T-NAME> turn(s) away.") : L("^S<S-NAME> turn(s) <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if ((mob.phyStats().level() + levelAdj + (getXLEVELLevel(mob)) - target.phyStats().level()) > 6) {
                        mob.location().show(mob, target, CMMsg.MSG_OK_ACTION, L("<T-NAME> wither(s)" + (auto ? "." : " under <S-HIS-HER> holy power!")));
                        CMLib.combat().postDamage(mob, target, this, target.curState().getHitPoints(), CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, -1, null);
                    } else {
                        mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> shake(s) in fear!"));
                        CMLib.commands().postFlee(target, "");
                    }
                    invoker = mob;
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to turn <T-NAMESELF>, but fail(s)."));

        // return whether it worked
        return success;
    }
}
