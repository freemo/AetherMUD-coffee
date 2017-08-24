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
package com.planet_ink.coffee_mud.Abilities.Misc;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class DiseaseCure extends StdAbility {
    private final static String localizedName = CMLib.lang().L("A Cure");
    protected boolean processing = false;

    @Override
    public String ID() {
        return "DiseaseCure";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL;
    }

    public List<Ability> returnOffensiveAffects(Physical fromMe) {
        final Vector<Ability> offenders = new Vector<Ability>();

        for (int a = 0; a < fromMe.numEffects(); a++) // personal
        {
            final Ability A = fromMe.fetchEffect(a);
            if ((A != null)
                && (A instanceof DiseaseAffect)
                && ((text().length() == 0) || (A.name().toUpperCase().indexOf(text().toUpperCase()) >= 0) || (A.ID().toUpperCase().indexOf(text().toUpperCase()) >= 0)))
                offenders.addElement(A);
        }
        return offenders;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (affected == null)
            return;
        if (affected instanceof Item) {
            if (!processing) {
                final Item myItem = (Item) affected;
                if (myItem.owner() == null)
                    return;
                processing = true;
                if (msg.amITarget(myItem)) {
                    switch (msg.sourceMinor()) {
                        case CMMsg.TYP_DRINK:
                            if (myItem instanceof Drink) {
                                invoke(msg.source(), null, msg.source(), true, 0);
                                myItem.destroy();
                            }
                            break;
                        case CMMsg.TYP_EAT:
                            if (myItem instanceof Food) {
                                invoke(msg.source(), null, msg.source(), true, 0);
                                myItem.destroy();
                            }
                            break;
                        case CMMsg.TYP_WEAR:
                            if (myItem.rawProperLocationBitmap() != Wearable.WORN_HELD) {
                                invoke(msg.source(), null, msg.source(), true, 0);
                                myItem.destroy();
                            }
                            break;
                    }
                }
            }
            processing = false;
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        final List<Ability> offensiveAffects = returnOffensiveAffects(target);

        if ((success) && (offensiveAffects.size() > 0)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_OK_VISUAL, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (int a = offensiveAffects.size() - 1; a >= 0; a--)
                    offensiveAffects.get(a).unInvoke();
                if ((!CMLib.flags().isStillAffectedBy(target, offensiveAffects, false)) && (target.location() != null))
                    target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> feel(s) much better."));
            }
        }

        // return whether it worked
        return success;
    }
}
