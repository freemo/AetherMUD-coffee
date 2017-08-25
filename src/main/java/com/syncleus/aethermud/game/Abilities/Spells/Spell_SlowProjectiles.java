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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_SlowProjectiles extends Spell {

    private final static String localizedName = CMLib.lang().L("Slow Projectiles");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Slow Projectiles)");

    @Override
    public String ID() {
        return "Spell_SlowProjectiles";
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
        return CAN_ROOMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.tool() instanceof Weapon)
            && (msg.source().getVictim() == msg.target())
            && (msg.source().rangeToTarget() > 0)
            && (((((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_RANGED) && (msg.tool() instanceof AmmunitionWeapon) && (((AmmunitionWeapon) msg.tool()).requiresAmmunition())
            || (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_THROWN)))
            && (msg.source().location() != null)
            && (msg.source().location() == affected)
            && (!msg.source().amDead())) {
            if (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_THROWN)
                msg.source().location().show(msg.source(), null, msg.tool(), CMMsg.MSG_OK_VISUAL, L("<O-NAME> flies slowly by."));
            else
                msg.source().location().show(msg.source(), null, CMMsg.MSG_OK_VISUAL, L("The shot from @x1 flies slowly by.", msg.tool().name()));
            final int damage = (msg.value()) / 2;
            msg.setValue(damage);
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(mob, null, null, L("Projectiles are already slow here!"));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> incant(s) slowly.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke a field of slowness, but fail(s)."));

        return success;
    }
}
