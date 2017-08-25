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

import com.planet_ink.game.Abilities.StdAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Druid_KnowPlants extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Know Plants");
    private static final String[] triggerStrings = I(new String[]{"KNOWPLANT"});

    public static boolean isPlant(Item I) {
        if ((I != null) && (I.rawSecretIdentity().length() > 0)) {
            for (final Enumeration<Ability> a = I.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null) && (A.invoker() != null) && (A instanceof Chant_SummonPlants))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String ID() {
        return "Druid_KnowPlants";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item I = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (I == null)
            return false;
        if (((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_VEGETATION)
            && ((I.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)) {
            mob.tell(L("Your plant knowledge can tell you nothing about @x1.", I.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (!success)
            mob.tell(L("Your plant senses fail you."));
        else {
            final CMMsg msg = CMClass.getMsg(mob, I, null, CMMsg.MSG_DELICATE_SMALL_HANDS_ACT | CMMsg.MASK_MAGIC, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final StringBuffer str = new StringBuffer("");
                str.append(L("@x1 is a kind of @x2.  ", I.name(mob), RawMaterial.CODES.NAME(I.material()).toLowerCase()));
                if (isPlant(I))
                    str.append(L("It was summoned by @x1.", I.rawSecretIdentity()));
                else
                    str.append(L("It is either processed by hand, or grown wild."));
                mob.tell(str.toString());
            }
        }
        return success;
    }
}

