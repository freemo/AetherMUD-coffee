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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.*;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class Thief_UsePoison extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Use Poison");
    private static final String[] triggerStrings = I(new String[]{"POISON", "USEPOISON"});

    @Override
    public String ID() {
        return "Thief_UsePoison";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_POISONING;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    public List<Ability> returnOffensiveAffects(Physical fromMe) {
        final Vector<Ability> offenders = new Vector<Ability>();

        for (final Enumeration<Ability> a = fromMe.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null) && ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
                offenders.addElement(A);
        }
        return offenders;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("What would you like to poison, and which poison would you use?"));
            return false;
        }
        final Item target = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, commands.get(0));
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", (commands.get(0))));
            return false;
        }
        if ((!(target instanceof Food))
            && (!(target instanceof Drink))
            && (!(target instanceof Weapon))) {
            mob.tell(L("You don't know how to poison @x1.", target.name(mob)));
            return false;
        }
        final Item poison = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, CMParms.combine(commands, 1));
        if ((poison == null) || (!CMLib.flags().canBeSeenBy(poison, mob))) {
            mob.tell(L("You don't see '@x1' here.", CMParms.combine(commands, 1)));
            return false;
        }
        final List<Ability> V = returnOffensiveAffects(poison);
        if ((V.size() == 0) || (!(poison instanceof Drink))) {
            mob.tell(L("@x1 is not a poison!", poison.name()));
            return false;
        }
        final Drink dPoison = (Drink) poison;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_THIEF_ACT, L("<S-NAME> attempt(s) to poison <T-NAMESELF>."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            if (success) {
                final Ability A = V.get(0);
                if (A != null) {
                    if (target instanceof Weapon)
                        A.invoke(mob, target, true, adjustedLevel(mob, asLevel));
                    else
                        target.addNonUninvokableEffect(A);

                    int amountToTake = dPoison.thirstQuenched() / 5;
                    if (amountToTake < 1)
                        amountToTake = 1;
                    dPoison.setLiquidRemaining(dPoison.liquidRemaining() - amountToTake);
                    if (dPoison.disappearsAfterDrinking()
                        || ((dPoison instanceof RawMaterial) && (dPoison.liquidRemaining() <= 0)))
                        dPoison.destroy();
                }

            }
        }
        return success;
    }

}
