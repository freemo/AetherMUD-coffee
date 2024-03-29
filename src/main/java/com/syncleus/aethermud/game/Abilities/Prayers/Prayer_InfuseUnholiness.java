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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.Deity;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Prayer_InfuseUnholiness extends Prayer {
    private final static String localizedName = CMLib.lang().L("Infuse Unholiness");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Infused Unholiness)");
    protected int serviceRunning = 0;

    @Override
    public String ID() {
        return "Prayer_InfuseUnholiness";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_EVANGELISM;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_ROOMS | Ability.CAN_EXITS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_ROOMS | Ability.CAN_EXITS;
    }

    @Override
    public int abilityCode() {
        return serviceRunning;
    }

    @Override
    public void setAbilityCode(int newCode) {
        serviceRunning = newCode;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_EVIL);
        if (CMath.bset(affectableStats.disposition(), PhyStats.IS_GOOD))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_GOOD);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if ((affected == null))
            return;
        if (canBeUninvoked()) {
            if (affected instanceof MOB)
                ((MOB) affected).tell(L("Your infused unholiness fades."));
        }

        super.unInvoke();

    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (serviceRunning == 0)
            return super.okMessage(myHost, msg);
        if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) == CMMsg.MASK_MALICIOUS)
            && (msg.target() instanceof MOB)) {
            if (msg.source().getWorshipCharID().equalsIgnoreCase(((MOB) msg.target()).getWorshipCharID())) {
                msg.source().tell(L("Not right now -- you're in a service."));
                msg.source().makePeace(true);
                ((MOB) msg.target()).makePeace(true);
                return false;
            }
        }
        if ((msg.sourceMinor() == CMMsg.TYP_LEAVE) && (msg.source().isMonster())) {
            msg.source().tell(L("Not right now -- you're in a service."));
            return false;
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Physical target;
        if ((givenTarget == null)
            && (CMParms.combine(commands, 0).equalsIgnoreCase("room") || CMParms.combine(commands, 0).equalsIgnoreCase("here")))
            target = mob.location();
        else
            target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        Deity D = null;
        if (CMLib.law().getClericInfusion(target) != null) {

            if (target instanceof Room)
                D = CMLib.law().getClericInfused((Room) target);
            if (D != null)
                mob.tell(L("There is already an infused aura of @x1 around @x2.", D.Name(), target.name(mob)));
            else
                mob.tell(L("There is already an infused aura around @x1.", target.name(mob)));
            return false;
        }

        D = mob.getMyDeity();
        if (target instanceof Room) {
            if (D == null) {
                mob.tell(L("The faithless may not infuse unholiness in a room."));
                return false;
            }
            final Area A = mob.location().getArea();
            Room R = null;
            for (final Enumeration<Room> e = A.getMetroMap(); e.hasMoreElements(); ) {
                R = e.nextElement();
                if (CMLib.law().getClericInfused((Room) target) == D) {
                    mob.tell(L("There is already an unholy place of @x1 in this area at @x2.", D.Name(), R.displayText(mob)));
                    return false;
                }
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("An unholy aura appears around <T-NAME>.") : L("^S<S-NAME> @x1 to infuse an unholy aura around <T-NAMESELF>.^?", prayForWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (D != null)
                    setMiscText(D.Name());
                if ((target instanceof Room)
                    && (CMLib.law().doesOwnThisLand(mob, ((Room) target)))) {
                    target.addNonUninvokableEffect((Ability) this.copyOf());
                    CMLib.database().DBUpdateRoom((Room) target);
                } else
                    beneficialAffect(mob, target, asLevel, 0);
                target.recoverPhyStats();
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 to infuse an unholy aura in <T-NAMESELF>, but fail(s).", prayForWord(mob)));

        return success;
    }
}
