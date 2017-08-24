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
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Chant_AstralProjection extends Chant {
    private final static String localizedName = CMLib.lang().L("Astral Projection");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Astral Projection)");

    @Override
    public String ID() {
        return "Chant_AstralProjection";
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
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_SHAPE_SHIFTING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if ((invoker != null) && (invoker.soulMate() == mob)) {
            final Session s = invoker.session();
            s.setMob(invoker.soulMate());
            mob.setSession(s);
            invoker.setSession(null);
            mob.tell(L("^HYour astral spirit has returned to your body...\n\r\n\r^N"));
            invoker.setSoulMate(null);
            invoker.destroy();
        }
        super.unInvoke();
        if (mob != null) {
            mob.recoverCharStats();
            mob.recoverMaxState();
            mob.recoverPhyStats();
            CMLib.commands().postStand(mob, true);
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((unInvoked) && (canBeUninvoked()))
            return super.tick(ticking, tickID);

        if ((tickID == Tickable.TICKID_MOB)
            && (tickDown != Integer.MAX_VALUE)
            && (canBeUninvoked())
            && (tickDown == 1))
            CMLib.combat().postDeath(null, (MOB) affected, null);
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null)
            && (affected instanceof MOB)
            && (msg.amISource((MOB) affected))
            && ((msg.sourceMinor() == CMMsg.TYP_DEATH) || (msg.sourceMinor() == CMMsg.TYP_QUIT)))
            unInvoke();
        return super.okMessage(myHost, msg);
    }

    public void peaceAt(MOB mob) {
        final Room room = mob.location();
        if (room == null)
            return;
        for (int m = 0; m < room.numInhabitants(); m++) {
            final MOB inhab = room.fetchInhabitant(m);
            if ((inhab != null) && (inhab.getVictim() == mob))
                inhab.setVictim(null);
        }
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SLEEPING);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_HEAR);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SEE);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SMELL);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_TASTE);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.soulMate() != null) {
            final Ability AS = target.soulMate().fetchEffect(ID());
            if (AS != null) {
                AS.unInvoke();
                return false;
            }
        }
        if (CMLib.flags().isGolem(target)
            && ((target.phyStats().height() <= 0) || (target.phyStats().weight() <= 0))) {
            mob.tell(L("You are already as astral spirit."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (!success) {
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) softly, but nothing happens"));
        }

        final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) softly.^?"));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            target.makePeace(true);
            peaceAt(target);
            final MOB spirit = CMClass.getFactoryMOB();
            spirit.setName(L("The Spirit of @x1", target.Name()));
            spirit.baseCharStats().setMyRace(CMClass.getRace("Spirit"));
            spirit.setPlayerStats(target.playerStats());
            spirit.setLocation(target.location());
            spirit.setAttributesBitmap(target.getAttributesBitmap());
            mob.location().show(target, null, CMMsg.MSG_OK_ACTION, L("^Z<S-NAME> go(es) limp!^.^?\n\r"));
            CMLib.threads().startTickDown(spirit, Tickable.TICKID_MOB, 1);
            beneficialAffect(spirit, target, asLevel, 0);
            final Ability A = CMClass.getAbility("Prop_AstralSpirit");
            spirit.addNonUninvokableEffect(A);
            final Session s = target.session();
            s.setMob(spirit);
            spirit.setSession(s);
            spirit.setSoulMate(target);
            target.setSession(null);
            spirit.recoverCharStats();
            spirit.recoverPhyStats();
            spirit.recoverMaxState();
            mob.location().recoverRoomStats();
        }

        return success;
    }
}
