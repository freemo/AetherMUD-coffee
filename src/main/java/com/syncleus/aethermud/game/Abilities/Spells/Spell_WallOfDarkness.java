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
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Spell_WallOfDarkness extends Spell {

    private final static String localizedName = CMLib.lang().L("Wall of Darkness");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Wall of Darkness)");
    protected Item theWall = null;

    @Override
    public String ID() {
        return "Spell_WallOfDarkness";
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
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected == null) || (!(affected instanceof Item)))
            return true;

        final MOB mob = msg.source();

        if ((invoker != null)
            && (mob.isInCombat())
            && (mob.getVictim() == invoker)
            && (mob.rangeToTarget() >= 1)
            && (msg.amITarget(invoker))
            && ((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)) {
            if ((msg.tool() instanceof Ability)) {
                mob.tell(L("You cannot see through the wall of darkness to target @x1.", mob.getVictim().name()));
                return false;
            }
            if ((msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
                && (msg.tool() instanceof Weapon)
                && (!((Weapon) msg.tool()).amWearingAt(Wearable.IN_INVENTORY))
                && (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_RANGED)) {
                mob.tell(L("You cannot see through the wall of darkness to target @x1.", mob.getVictim().name()));
                if (mob.isMonster())
                    CMLib.commands().postRemove(mob, (Item) msg.tool(), true);
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void unInvoke() {
        super.unInvoke();
        if (canBeUninvoked()) {
            if ((theWall != null)
                && (invoker != null)
                && (theWall.owner() != null)
                && (theWall.owner() instanceof Room)
                && (((Room) theWall.owner()).isContent(theWall))) {
                ((Room) theWall.owner()).showHappens(CMMsg.MSG_OK_VISUAL, L("The wall of darkness fades."));
                final Item wall = theWall;
                theWall = null;
                wall.destroy();
            }
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB) {
            if ((invoker != null)
                && (theWall != null)
                && (invoker.location() != null)
                && (!invoker.location().isContent(theWall)))
                unInvoke();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((!mob.isInCombat()) || (mob.rangeToTarget() < 1)) {
            mob.tell(L("You really should be in ranged combat to cast this."));
            return false;
        }
        for (int i = 0; i < mob.location().numItems(); i++) {
            final Item I = mob.location().getItem(i);
            if ((I != null) && (I.fetchEffect(ID()) != null)) {
                mob.tell(L("There is already a wall of darkness here."));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Physical target = mob.location();

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("An eerie wall of darkness appears!") : L("^S<S-NAME> conjur(s) up a eerie wall of darkness!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Item I = CMClass.getItem("GenItem");
                I.setName(L("a wall of darkness"));
                I.setDisplayText(L("an eerie wall of darkness lingers here"));
                I.setDescription(L("It`s black."));
                I.setMaterial(RawMaterial.RESOURCE_NOTHING);
                CMLib.flags().setGettable(I, false);
                I.recoverPhyStats();
                mob.location().addItem(I);
                theWall = I;
                beneficialAffect(mob, I, asLevel, 10);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> incant(s), but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
