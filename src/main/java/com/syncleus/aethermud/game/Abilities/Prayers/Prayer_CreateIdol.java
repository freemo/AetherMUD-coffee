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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharState;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.ItemPossessor;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_CreateIdol extends Prayer {
    private final static String localizedName = CMLib.lang().L("Create Idol");

    @Override
    public String ID() {
        return "Prayer_CreateIdol";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
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
    public boolean bubbleAffect() {
        return true;
    }

    @Override
    public void affectPhyStats(Physical aff, PhyStats affectableStats) {
        super.affectPhyStats(aff, affectableStats);
        if ((affected instanceof Item) && (((Item) affected).container() == null)) {
            final int xlvl = super.getXLEVELLevel(invoker());
            affectableStats.setArmor(affectableStats.armor() + (20 + (4 * xlvl)));
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - 10 - (2 * xlvl));
        }
    }

    @Override
    public void affectCharStats(MOB aff, CharStats affectableStats) {
        super.affectCharStats(aff, affectableStats);
        if ((affected instanceof Item) && (((Item) affected).container() == null)) {
            if (affectableStats.getStat(CharStats.STAT_STRENGTH) > 3)
                affectableStats.setStat(CharStats.STAT_STRENGTH, 3);
            if (affectableStats.getStat(CharStats.STAT_DEXTERITY) > 2)
                affectableStats.setStat(CharStats.STAT_DEXTERITY, 2);
            if (affectableStats.getStat(CharStats.STAT_CONSTITUTION) > 1)
                affectableStats.setStat(CharStats.STAT_CONSTITUTION, 1);
        }
    }

    @Override
    public void affectCharState(MOB aff, CharState affectableState) {
        super.affectCharState(aff, affectableState);
        if ((affected instanceof Item) && (((Item) affected).container() == null)) {
            if (aff.maxState().getFatigue() > Long.MIN_VALUE / 2)
                aff.curState().setFatigue(CharState.FATIGUED_MILLIS + 10);
            affectableState.setMovement(20);
        }
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((msg.targetMinor() == CMMsg.TYP_GIVE)
            && (msg.target() instanceof MOB)
            && (affected instanceof Item)
            && (msg.tool() == affected)
            && (!((MOB) msg.target()).willFollowOrdersOf(msg.source()))) {
            msg.source().tell(L("@x1 won`t accept @x2.", ((MOB) msg.target()).name(msg.source()), ((Item) msg.tool()).name(msg.source())));
            return false;
        }
        return super.okMessage(host, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((mob.getWorshipCharID().length() == 0) || (CMLib.map().getDeity(mob.getWorshipCharID()) == null)) {
            mob.tell(L("You must worship a god to use this prayer."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        int material = -1;
        final Room R = mob.location();
        if (R != null) {
            if (((R.myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_ROCK)
                || ((R.myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_MITHRIL)
                || ((R.myResource() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_METAL))
                material = R.myResource();
            else {
                final List<Integer> V = R.resourceChoices();
                if ((V != null) && (V.size() > 0))
                    for (int v = 0; v < V.size() * 10; v++) {
                        final int rsc = V.get(CMLib.dice().roll(1, V.size(), -1)).intValue();
                        if (((rsc & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_ROCK)
                            || ((rsc & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_MITHRIL)
                            || ((rsc & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_METAL)) {
                            material = rsc;
                            break;
                        }
                    }
            }
        }

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if ((success) && (material > 0)) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> @x1 for an idol.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Item newItem = CMClass.getBasicItem("GenItem");
                newItem.setBaseValue(1);
                final String name = CMLib.english().startWithAorAn(RawMaterial.CODES.NAME(material).toLowerCase() + " idol of " + mob.getWorshipCharID());
                newItem.setName(name);
                newItem.setDisplayText(L("@x1 sits here.", name));
                newItem.basePhyStats().setDisposition(PhyStats.IS_EVIL);
                newItem.basePhyStats().setWeight(10);
                newItem.setMaterial(material);
                newItem.recoverPhyStats();
                CMLib.flags().setRemovable(newItem, false);
                CMLib.flags().setDroppable(newItem, false);
                newItem.addNonUninvokableEffect((Ability) copyOf());
                mob.location().addItem(newItem, ItemPossessor.Expire.Resource);
                mob.location().showHappens(CMMsg.MSG_OK_ACTION, L("Suddenly, @x1 grows out of the ground.", newItem.name()));
                mob.location().recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> @x1 for an idol, but there is no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
