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
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.List;


public class Chant_FodderSignal extends Chant {
    private final static String localizedName = CMLib.lang().L("Fodder Signal");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Fodder Signal)");

    @Override
    public String ID() {
        return "Chant_FodderSignal";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("The fodder signal stops flashing."));

        super.unInvoke();

    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);

        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_GLOWING);
        affectableStats.setArmor(affectableStats.armor() + 10 + super.getXLEVELLevel(invoker()));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (tickID != Tickable.TICKID_MOB)
            return false;
        if ((affecting() != null) && (affecting() instanceof MOB)) {
            final MOB dummy = (MOB) affecting();
            final Room room = dummy.location();
            if (room != null) {
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB M = room.fetchInhabitant(i);
                    if ((M != null)
                        && (M != dummy)
                        && (M.isMonster())
                        && (!M.isInCombat())
                        && (!dummy.getGroupMembers(new HashSet<MOB>()).contains(M))
                        && (CMLib.flags().canBeSeenBy(dummy, M))) {
                        if (room.show(M, dummy, CMMsg.MASK_MOVE | CMMsg.MSG_NOISE, L("<S-NAME> howl(s) in anger at <T-NAMESELF>!")))
                            CMLib.combat().postAttack(M, dummy, M.fetchWieldedItem());
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) at <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("A horrible angering flag is emitting from <S-NAME>!"));
                    maliciousAffect(mob, target, asLevel, 0, -1);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) at <T-NAMESELF>, but the magic fades."));
        // return whether it worked
        return success;
    }
}
