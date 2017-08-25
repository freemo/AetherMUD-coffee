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
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_AirWall extends Chant {
    private final static String localizedName = CMLib.lang().L("Air Wall");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Air Wall)");

    @Override
    public String ID() {
        return "Chant_AirWall";
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
    public int maxRange() {
        return adjustedMaxInvokerRange(10);
    }

    @Override
    public int minRange() {
        return 1;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public int enchantQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((!mob.isInCombat()) || (mob.rangeToTarget() < 1)) {
            mob.tell(L("You really should be in ranged combat to do this."));
            return false;
        }
        for (int i = 0; i < mob.location().numItems(); i++) {
            final Item I = mob.location().getItem(i);
            if ((I != null) && (I.fetchEffect(ID()) != null)) {
                mob.tell(L("There is already an air wall here."));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Physical target = mob.location();

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? null : L("^S<S-NAME> chant(s) for an air wall!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                Ability A = CMClass.getAbility("Spell_WallOfAir");
                if (A != null) {
                    A.setMiscText("SHOOTBACK=FALSE");
                    A.invoke(mob, target, true, asLevel);
                }
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s), but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
