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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_FakeSpring extends Spell {

    private final static String localizedName = CMLib.lang().L("Fake Spring");

    @Override
    public String ID() {
        return "Spell_FakeSpring";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public void unInvoke() {
        final Item spring = (Item) affected;
        super.unInvoke();
        if ((canBeUninvoked()) && (spring != null)) {
            final Room SpringLocation = CMLib.map().roomLocation(spring);
            spring.destroy();
            SpringLocation.recoverRoomStats();
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(affected)) {
            if (msg.targetMinor() == CMMsg.TYP_DRINK) {
                if (msg.othersMessage() != null)
                    msg.source().location().show(msg.source(), msg.target(), msg.tool(), CMMsg.MSG_QUIETMOVEMENT, msg.othersMessage());
                msg.source().tell(L("You have drunk all you can."));
                return false;
            }
        } else if ((msg.tool() == affected) && (msg.target() instanceof Drink)) {
            if (msg.targetMinor() == CMMsg.TYP_FILL) {
                msg.source().tell(L("@x1 is full.", ((Drink) msg.target()).name(msg.source())));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> invoke(s) a spell dramatically.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final String itemID = "Spring";

                final Item newItem = CMClass.getItem(itemID);

                if (newItem == null) {
                    mob.tell(L("There's no such thing as a '@x1'.\n\r", itemID));
                    return false;
                }

                final Drink W = (Drink) CMClass.getItem("GenWater");
                W.setName(newItem.Name());
                W.setDisplayText(newItem.displayText());
                W.setDescription(newItem.description());
                W.basePhyStats().setWeight(newItem.basePhyStats().weight());
                CMLib.flags().setGettable(((Item) W), false);
                W.setThirstQuenched(0);
                W.recoverPhyStats();
                mob.location().addItem((Item) W);
                mob.location().showHappens(CMMsg.MSG_OK_ACTION, L("Suddenly, @x1 starts flowing here.", newItem.name()));
                if (CMLib.law().doesOwnThisLand(mob, mob.location())) {
                    final Ability A = (Ability) copyOf();
                    A.setInvoker(mob);
                    W.addNonUninvokableEffect(A);
                } else
                    beneficialAffect(mob, W, asLevel, 0);
                mob.location().recoverPhyStats();
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> dramatically attempt(s) to invoke a spell, but fizzle(s) the spell."));

        // return whether it worked
        return success;
    }
}
