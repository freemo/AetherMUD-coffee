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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Thief_Search extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Search");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Searching)");
    private static final String[] triggerStrings = I(new String[]{"SEARCH"});
    private Room lastRoom = null;
    private int bonusThisRoom = 0;

    @Override
    public String ID() {
        return "Thief_Search";
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
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_ALERT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING, bonusThisRoom + proficiency() + affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (affected instanceof MOB) {
            if (!CMLib.flags().isAliveAwakeMobile((MOB) affected, true)) {
                unInvoke();
                return false;
            }
            if (((MOB) affected).location() != lastRoom) {
                lastRoom = ((MOB) affected).location();
                bonusThisRoom = getXLEVELLevel((MOB) affected) * 2;
                ((MOB) affected).recoverCharStats();
            } else if (bonusThisRoom < affected.phyStats().level()) {
                bonusThisRoom += 5;
                ((MOB) affected).recoverCharStats();
            }
        }
        return true;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_HIDDEN);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.fetchEffect(this.ID()) != null)
                return Ability.QUALITY_INDIFFERENT;

            final Room R = mob.location();
            if (R != null) {
                for (int r = 0; r < R.numInhabitants(); r++) {
                    final MOB M = R.fetchInhabitant(r);
                    if ((M != null) && (M != mob) && (CMLib.flags().isHidden(M)))
                        return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
                }
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already aware of hidden things."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        final CMMsg msg = CMClass.getMsg(mob, target, this, auto ? CMMsg.MASK_ALWAYS : CMMsg.MSG_DELICATE_HANDS_ACT, CMMsg.MSG_OK_VISUAL, CMMsg.MSG_OK_VISUAL, auto ? L("<T-NAME> become(s) very observant.") : L("<S-NAME> examine(s) <S-HIS-HER> surroundings carefully."));
        if (!success)
            return beneficialVisualFizzle(mob, null, auto ? "" : L("<S-NAME> look(s) around carefully, but become(s) distracted."));
        else if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, target, asLevel, 0);
            target.phyStats().setSensesMask(mob.phyStats().sensesMask() | PhyStats.CAN_SEE_HIDDEN);
            target.phyStats().setSensesMask(mob.phyStats().sensesMask() | PhyStats.CAN_SEE_SNEAKERS);
            CMLib.commands().postLook(target, false);
            target.recoverPhyStats();
        }
        return success;
    }
}
