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
package com.planet_ink.game.Abilities.Ranger;

import com.planet_ink.game.Abilities.StdAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Ranger_Hide extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Woodland Hide");
    private static final String[] triggerStrings = I(new String[]{"WHIDE"});
    protected int bonus = 0;

    public static int getMOBLevel(MOB meMOB) {
        if (meMOB == null)
            return 0;
        return meMOB.phyStats().level();
    }

    public static MOB getHighestLevelMOB(MOB meMOB, Collection<MOB> not) {
        if (meMOB == null)
            return null;
        final Room R = meMOB.location();
        if (R == null)
            return null;
        int highestLevel = 0;
        MOB highestMOB = null;
        final Set<MOB> H = meMOB.getGroupMembers(new HashSet<MOB>());
        if (not != null)
            H.addAll(not);
        for (int i = 0; i < R.numInhabitants(); i++) {
            final MOB M = R.fetchInhabitant(i);
            if ((M != null)
                && (M != meMOB)
                && (!CMLib.flags().isSleeping(M))
                && (!H.contains(M))
                && (highestLevel < M.phyStats().level())
                && (!CMSecurity.isASysOp(M))) {
                highestLevel = M.phyStats().level();
                highestMOB = M;
            }
        }
        return highestMOB;
    }

    @Override
    public String ID() {
        return "Ranger_Hide";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_STEALTHY;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return;

        final MOB mob = (MOB) affected;

        if (msg.amISource(mob)) {

            if (((msg.sourceMajor(CMMsg.MASK_SOUND)
                || (msg.sourceMinor() == CMMsg.TYP_SPEAK)
                || (msg.sourceMinor() == CMMsg.TYP_ENTER)
                || (msg.sourceMinor() == CMMsg.TYP_LEAVE)
                || (msg.sourceMinor() == CMMsg.TYP_RECALL)))
                && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
                && (msg.sourceMinor() != CMMsg.TYP_LOOK)
                && (msg.sourceMinor() != CMMsg.TYP_EXAMINE)
                && (msg.sourceMajor() > 0)) {
                unInvoke();
                mob.recoverPhyStats();
            }
        }
        return;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
        if (CMLib.flags().isSneaking(affected))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_SNEAKING);
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_DETECTION, proficiency() + bonus + affectableStats.getStat(CharStats.STAT_SAVE_DETECTION));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if (mob.fetchEffect(this.ID()) != null) {
            mob.tell(L("You are already hiding."));
            return false;
        }

        if (mob.isInCombat()) {
            mob.tell(L("Not while in combat!"));
            return false;
        }

        if ((!CMLib.flags().isInWilderness(mob)) && (!auto)) {
            mob.tell(L("You only know how to hide outdoors."));
            return false;
        }
        if (((mob.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_SPACEPORT))
            && (!auto)) {
            mob.tell(L("You don't know how to hide in a place like this."));
            return false;
        }

        final MOB highestMOB = getHighestLevelMOB(mob, null);
        final int levelDiff = (mob.phyStats().level() + (2 * getXLEVELLevel(mob))) - getMOBLevel(highestMOB);

        String str = L("You creep into some foliage and remain completely still.");
        if ((mob.location().domainType() == Room.DOMAIN_OUTDOORS_ROCKS)
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_MOUNTAINS)
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_DESERT))
            str = L("You creep behind some rocks and remain completely still.");

        boolean success = (highestMOB == null) || proficiencyCheck(mob, levelDiff * 10, auto);

        if (!success) {
            if (highestMOB != null)
                beneficialVisualFizzle(mob, highestMOB, L("<S-NAME> attempt(s) to hide from <T-NAMESELF> and fail(s)."));
            else
                beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to hide and fail(s)."));
        } else {
            final CMMsg msg = CMClass.getMsg(mob, null, this, auto ? CMMsg.MSG_OK_ACTION : (CMMsg.MSG_DELICATE_HANDS_ACT | CMMsg.MASK_MOVE), str, CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                invoker = mob;
                final Ability newOne = (Ability) this.copyOf();
                ((Ranger_Hide) newOne).bonus = getXLEVELLevel(mob) * 2;
                if (mob.fetchEffect(newOne.ID()) == null)
                    mob.addEffect(newOne);
                mob.recoverPhyStats();
            } else
                success = false;
        }
        return success;
    }
}
