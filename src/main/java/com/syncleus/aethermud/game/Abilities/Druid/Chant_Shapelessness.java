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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_Shapelessness extends Chant {
    private final static String localizedName = CMLib.lang().L("Shapelessness");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Shapelessness)");

    @Override
    public String ID() {
        return "Chant_Shapelessness";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_SHAPE_SHIFTING;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
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
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> return(s) to material form."));
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setWeight(0);
        affectableStats.setHeight(-1);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null)
            && (affected instanceof MOB)
            && (msg.amISource((MOB) affected))
            && (CMLib.dice().rollPercentage() > 25)) {
            switch (msg.sourceMinor()) {
                case CMMsg.TYP_ENTER:
                case CMMsg.TYP_LEAVE:
                    if ((msg.tool() instanceof Exit)
                        && (((Exit) msg.tool()).hasADoor())
                        && (!((Exit) msg.tool()).isOpen())
                        && (msg.source().numItems() > 0)) {
                        msg.source().tell(L("Your corporeal equipment, suspended in your shapeless form, will not pass through the door."));
                        return false;
                    }
                    break;
                case CMMsg.TYP_GET:
                case CMMsg.TYP_PUT:
                case CMMsg.TYP_DROP:
                case CMMsg.TYP_HOLD:
                case CMMsg.TYP_WIELD:
                case CMMsg.TYP_WEAR:
                case CMMsg.TYP_REMOVE:
                case CMMsg.TYP_DELICATE_HANDS_ACT:
                case CMMsg.TYP_WITHDRAW:
                case CMMsg.TYP_BORROW:
                case CMMsg.TYP_LOCK:
                case CMMsg.TYP_UNLOCK:
                case CMMsg.TYP_HANDS:
                case CMMsg.TYP_INSTALL:
                case CMMsg.TYP_ENHANCE:
                case CMMsg.TYP_REPAIR:
                    msg.source().tell(L("You have trouble manipulating matter in this form."));
                    return false;
                case CMMsg.TYP_THROW:
                case CMMsg.TYP_WEAPONATTACK:
                case CMMsg.TYP_KNOCK:
                case CMMsg.TYP_PULL:
                case CMMsg.TYP_PUSH:
                case CMMsg.TYP_OPEN:
                case CMMsg.TYP_CLOSE:
                    msg.source().tell(L("You fail your attempt to affect matter in this form."));
                    return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (((MOB) target).isInCombat())
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already shapeless."));
            return false;
        }
        if ((!auto) && (!mob.location().getArea().getClimateObj().canSeeTheMoon(mob.location(), null))) {
            mob.tell(L("You must be able under the moons glow for this magic to work."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant that <T-NAME> be given a shapeless form.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> shimmer(s) and become(s) ethereal!"));
                beneficialAffect(mob, target, asLevel, 3);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) for a new shape, but nothing happens."));

        // return whether it worked
        return success;
    }
}
