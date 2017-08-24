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
package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Chant_Treemorph extends Chant {
    private final static String localizedName = CMLib.lang().L("Treemorph");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Treemorph)");
    Item tree = null;
    Race treeForm = null;

    @Override
    public String ID() {
        return "Chant_Treemorph";
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
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_MOB)
            && (tree != null)
            && (affected instanceof MOB)) {
            final MOB mob = (MOB) affected;
            if ((tree.owner() != null) && (tree.owner() != mob.location())) {
                Room room = null;
                if (tree.owner() instanceof MOB)
                    room = ((MOB) tree.owner()).location();
                else if (tree.owner() instanceof Room)
                    room = (Room) tree.owner();
                if ((room != null) && (room != mob.location()))
                    room.bringMobHere(mob, false);
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (treeForm != null) {
            final int oldCat = affected.baseCharStats().ageCategory();
            affectableStats.setMyRace(treeForm);
            if (affected.baseCharStats().getStat(CharStats.STAT_AGE) > 0)
                affectableStats.setStat(CharStats.STAT_AGE, treeForm.getAgingChart()[oldCat]);
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (msg.source().getVictim() == mob)
                msg.source().setVictim(null);
            if (mob.isInCombat()) {
                final MOB victim = mob.getVictim();
                if (victim != null)
                    victim.makePeace(true);
                mob.makePeace(true);
            }
            mob.recoverMaxState();
            mob.resetToMaxState();
            mob.curState().setHunger(1000);
            mob.curState().setThirst(1000);
            mob.recoverCharStats();
            mob.recoverPhyStats();

            // when this spell is on a MOBs Affected list,
            // it should consistantly prevent the mob
            // from trying to do ANYTHING except sleep
            if (msg.amISource(mob)) {
                if ((!msg.sourceMajor(CMMsg.MASK_ALWAYS))
                    && (msg.sourceMajor() > 0)) {
                    mob.tell(L("Trees can't do that."));
                    return false;
                }
            }
        }
        if (!super.okMessage(myHost, msg))
            return false;

        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (msg.source().getVictim() == affected)
                msg.source().setVictim(null);
            if ((msg.target() == mob) && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))) {
                msg.source().tell(msg.source(), mob, null, L("You can't do that to <T-NAME>."));
                return false;
            }
            if (mob.isInCombat()) {
                final MOB victim = mob.getVictim();
                if (victim != null)
                    victim.makePeace(true);
                mob.makePeace(true);
            }
        }
        return true;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        // when this spell is on a MOBs Affected list,
        // it should consistantly put the mob into
        // a sleeping state, so that nothing they do
        // can get them out of it.
        if ((treeForm != null) && (affected instanceof MOB)) {
            if (affected.name().indexOf(' ') > 0)
                affectableStats.setName(L("a @x1 called @x2", treeForm.name(), affected.name()));
            else
                affectableStats.setName(L("@x1 the @x2", affected.name(), treeForm.name()));
            final int oldAdd = affectableStats.weight() - affected.basePhyStats().weight();
            treeForm.setHeightWeight(affectableStats, 'M');
            if (oldAdd > 0)
                affectableStats.setWeight(affectableStats.weight() + oldAdd);

            //affectableStats.setReplacementName("a tree of "+affected.name());
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_HEAR);
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SMELL);
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK);
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_TASTE);
        }
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked()) {
            if (tree != null)
                tree.destroy();
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> no longer a tree."));
            mob.curState().setHitPoints(1);
            mob.curState().setMana(0);
            mob.curState().setMovement(0);
            mob.curState().setHunger(0);
            mob.curState().setThirst(0);
            CMLib.commands().postStand(mob, true);
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff < 0)
            levelDiff = 0;
        boolean success = proficiencyCheck(mob, -(levelDiff * 10), auto);
        treeForm = CMClass.getRace("TreeGolem");
        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) at <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    int a = 0;
                    while (a < target.numEffects()) // personal effects
                    {
                        final Ability A = target.fetchEffect(a);
                        final int s = target.numEffects();
                        if (A != null)
                            A.unInvoke();
                        if (target.numEffects() == s)
                            a++;
                    }
                    target.makePeace(true);
                    CMLib.commands().postStand(target, true);
                    tree = CMClass.getItem("GenItem");
                    tree.setName(L("a oak tree"));
                    tree.setDisplayText(L("an oak tree that reminds you of @x1 is growing here.", target.name()));
                    tree.setDescription(L("It`s a tall oak tree, which seems to remind you of @x1.", target.name()));
                    tree.setMaterial(RawMaterial.RESOURCE_OAK);
                    tree.basePhyStats().setWeight(5000);
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> turn(s) into a tree!!"));
                    success = maliciousAffect(mob, target, asLevel, (mob.phyStats().level() + (2 * getXLEVELLevel(mob))) * 25, -1) != null;
                    final Ability A = target.fetchEffect(ID());
                    if (success && (A != null)) {
                        mob.location().addItem(tree);
                        tree.addEffect(A);
                        A.setAffectedOne(target);
                        tree.recoverPhyStats();
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) at <T-NAMESELF>, but the magic fades."));

        // return whether it worked
        return success;
    }
}
