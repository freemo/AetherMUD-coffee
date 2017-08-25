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
import com.syncleus.aethermud.game.Items.interfaces.AmmunitionWeapon;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_ResistArrows extends Spell {

    private final static String localizedName = CMLib.lang().L("Resist Arrows");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Resist Arrows)");

    @Override
    public String ID() {
        return "Spell_ResistArrows";
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
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("Your arrow protection dissipates."));

        super.unInvoke();

    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return super.okMessage(myHost, msg);

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (msg.tool() instanceof AmmunitionWeapon)
            && (msg.source().getVictim() == mob)
            && (msg.source().rangeToTarget() > 0)
            && (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_RANGED)
            && (((AmmunitionWeapon) msg.tool()).requiresAmmunition())
            && (!mob.amDead())
            && (CMLib.dice().rollPercentage() < 35)) {
            mob.location().show(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The barrier around <S-NAME> absorbs the @x1 from <T-NAME>!", ((AmmunitionWeapon) msg.tool()).ammunitionType()));
            return false;
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> feel(s) absorbantly protected.") : L("^S<S-NAME> invoke(s) a non-porous barrier of protection around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke a non-porous barrier, but fail(s)."));

        return success;
    }
}
