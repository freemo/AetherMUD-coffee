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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_CurseFlames extends Prayer {
    private final static String localizedName = CMLib.lang().L("Curse Flames");

    @Override
    public String ID() {
        return "Prayer_CurseFlames";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
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
    public long flags() {
        return Ability.FLAG_UNHOLY | Ability.FLAG_FIREBASED;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(5);
    }

    @Override
    public int minRange() {
        return 0;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (getFireSource((MOB) target) == null)
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    private Item getFireSource(MOB target) {
        Item fireSource = null;
        for (int i = 0; i < target.numItems(); i++) {
            final Item I = target.getItem(i);
            if ((CMLib.flags().isOnFire(I)) && (I.container() == null)) {
                fireSource = I;
                break;
            }
        }

        if (fireSource == null)
            for (int i = 0; i < target.location().numItems(); i++) {
                final Item I = target.location().getItem(i);
                if ((CMLib.flags().isOnFire(I)) && (I.container() == null)) {
                    fireSource = I;
                    break;
                }
            }
        return fireSource;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        final Item fireSource = getFireSource(target);

        if ((success) && (fireSource != null)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "Suddenly " + fireSource.name() + " flares up and attacks <T-HIM-HER>!^?" : "^S<S-NAME> point(s) at <T-NAMESELF> and " + prayWord(mob) + ".  Suddenly " + fireSource.name() + " flares up and attacks <T-HIM-HER>!^?") + CMLib.protocol().msp("fireball.wav", 40));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_FIRE | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && ((mob.location().okMessage(mob, msg2)))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg2);
                int damage = CMLib.dice().roll(1, 2 * adjustedLevel(mob, asLevel), (2 * super.getX1Level(mob)) + 1);
                if ((msg.value() > 0) || (msg2.value() > 0))
                    damage = (int) Math.round(CMath.div(damage, 2.0));

                if (target.location() == mob.location())
                    CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("The flames <DAMAGE> <T-NAME>!"));
            }
            fireSource.destroy();
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
