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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Chant_PlantChoke extends Chant {
    private final static String localizedName = CMLib.lang().L("Plant Choke");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Plant Choke)");

    @Override
    public String ID() {
        return "Chant_PlantChoke";
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
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean bubbleAffect() {
        return true;
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
    public void unInvoke() {
        Item I = null;
        if (affected instanceof Item)
            I = (Item) affected;
        super.unInvoke();
        if ((canBeUninvoked()) && (I != null) && (I.owner() instanceof MOB)
            && (!I.amWearingAt(Wearable.IN_INVENTORY))) {
            final MOB mob = (MOB) I.owner();
            if ((!mob.amDead())
                && (CMLib.flags().isInTheGame(mob, false))) {
                mob.tell(L("@x1 loosens its grip on your neck and falls off.", I.name(mob)));
                I.setRawWornCode(0);
                mob.location().moveItemTo(I, ItemPossessor.Expire.Player_Drop);
            }
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        Item I = null;
        if (affected instanceof Item)
            I = (Item) affected;
        if ((canBeUninvoked()) && (I != null) && (I.owner() instanceof MOB)
            && (I.amWearingAt(Wearable.WORN_NECK))) {
            final MOB mob = (MOB) I.owner();
            if ((!mob.amDead())
                && (mob.isMonster())
                && (CMLib.flags().isInTheGame(mob, false)))
                CMLib.commands().postRemove(mob, I, false);
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if (!super.okMessage(host, msg))
            return false;
        if ((msg.targetMinor() == CMMsg.TYP_REMOVE)
            && (msg.target() == affected)
            && (affected instanceof Item)
            && (((Item) affected).amWearingAt(Wearable.WORN_NECK))) {
            if (CMLib.dice().rollPercentage() > (msg.source().charStats().getStat(CharStats.STAT_STRENGTH) * 3)) {
                msg.source().location().show(msg.source(), affected, CMMsg.MSG_OK_VISUAL, L("<S-NAME> struggle(s) to remove <T-NAME> and fail(s)."));
                return false;
            }
        }
        return true;
    }

    @Override
    public void affectPhyStats(Physical aff, PhyStats affectableStats) {
        if ((aff instanceof MOB) && (affected instanceof Item)
            && (((Item) affected).amWearingAt(Wearable.WORN_NECK))
            && (((MOB) aff).isMine(affected)))
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_BREATHE);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Item myPlant = Druid_MyPlants.myPlant(mob.location(), mob, 0);
            if (myPlant == null)
                return Ability.QUALITY_INDIFFERENT;
            if (target instanceof MOB) {
                if (((MOB) target).getWearPositions(Wearable.WORN_NECK) == 0)
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        Item myPlant = Druid_MyPlants.myPlant(mob.location(), mob, 0);
        if (myPlant == null) {
            if (auto)
                myPlant = new Chant_SummonPlants().buildPlant(mob, mob.location());
            else {
                mob.tell(L("There doesn't appear to be any of your plants here to choke with."));
                return false;
            }
        }

        if (target.getWearPositions(Wearable.WORN_NECK) == 0) {
            if (!auto)
                mob.tell(L("Ummm, @x1 doesn't HAVE a neck...", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) at <T-NAME> while pointing at @x1!^?", myPlant.name()));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                target.moveItemTo(myPlant);
                myPlant.setRawWornCode(Wearable.WORN_NECK);
                mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("@x1 jumps up and wraps itself around <S-YOUPOSS> neck!", myPlant.name()));
                beneficialAffect(mob, myPlant, asLevel, 5);
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) at <T-NAME>, but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
