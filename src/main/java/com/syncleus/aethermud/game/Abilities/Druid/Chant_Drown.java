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
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Arrays;
import java.util.List;


public class Chant_Drown extends Chant {
    private final static String localizedName = CMLib.lang().L("Drown");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Drowning)");
    protected int[] lastSet = null;
    protected int[] newSet = null;

    @Override
    public String ID() {
        return "Chant_Drown";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_WATERCONTROL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        final int[] breatheables = affectableStats.getBreathables();
        if (breatheables.length == 0)
            return;
        if ((lastSet != breatheables) || (newSet == null)) {
            int remove = 0;
            if (CMParms.contains(breatheables, RawMaterial.RESOURCE_SALTWATER))
                remove++;
            if (CMParms.contains(breatheables, RawMaterial.RESOURCE_FRESHWATER))
                remove++;
            boolean addAir = (remove == breatheables.length);
            if (remove > 0) {
                newSet = Arrays.copyOf(breatheables, breatheables.length - remove + (addAir ? 1 : 0));
                for (int i = 0, ni = 0; i < breatheables.length; i++) {
                    if ((breatheables[i] != RawMaterial.RESOURCE_SALTWATER)
                        && (breatheables[i] != RawMaterial.RESOURCE_FRESHWATER))
                        newSet[ni++] = breatheables[i];
                }
                if (addAir)
                    newSet[newSet.length - 1] = RawMaterial.RESOURCE_AIR;
                Arrays.sort(newSet);
            } else
                newSet = breatheables;
            lastSet = breatheables;
        }
        affectableStats.setBreathables(newSet);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("You no longer have a drowning sensation."));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        final MOB myChar = (MOB) affected;
        final Room R = myChar.location();
        if (R == null)
            return true;
        if (!CMLib.flags().isWateryRoom(R))
            unInvoke();
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if ((msg.source() == affected)
            && (msg.sourceMajor(CMMsg.MASK_MOVE))
            && (msg.tool() instanceof Ability)
            && (((Ability) msg.tool()).ID().equalsIgnoreCase("Skill_Swim"))) {
            return false;
        }
        return true;
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if ((lastSet == newSet) && (affected instanceof MOB)) {
            if (CMath.bset(affectableStats.disposition(), PhyStats.IS_SWIMMING))
                affectableStats.setDisposition(CMath.unsetb(affectableStats.disposition(), PhyStats.IS_SWIMMING));
            else
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_BREATHE);
        }
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> chant(s) at <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> seem(s) to be drowning!"));
                    maliciousAffect(mob, target, asLevel, 6, -1);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades."));

        // return whether it worked
        return success;
    }
}
