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
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_MassMobility extends Prayer {
    private final static String localizedName = CMLib.lang().L("Mass Mobility");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mass Mobility)");

    @Override
    public String ID() {
        return "Prayer_MassMobility";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_RESTORATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob))
            && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (msg.targetMinor() == CMMsg.TYP_CAST_SPELL)
            && (msg.tool() instanceof Ability)
            && (!mob.amDead())) {
            final Ability A = (Ability) msg.tool();
            final MOB newMOB = CMClass.getFactoryMOB();
            final CMMsg msg2 = CMClass.getMsg(newMOB, null, null, CMMsg.MSG_SIT, null);
            newMOB.recoverPhyStats();
            try {
                A.affectPhyStats(newMOB, newMOB.phyStats());
                if ((!CMLib.flags().isAliveAwakeMobileUnbound(newMOB, true))
                    || (CMath.bset(A.flags(), Ability.FLAG_PARALYZING))
                    || (!A.okMessage(newMOB, msg2))) {
                    mob.location().show(mob, msg.source(), null, CMMsg.MSG_OK_VISUAL, L("The aura around <S-NAME> repels the @x1 from <T-NAME>.", A.name()));
                    newMOB.destroy();
                    return false;
                }
            } catch (final Exception e) {
            }
            newMOB.destroy();
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
        super.affectCharStats(affectedMOB, affectedStats);
        if (affectedStats.getStat(CharStats.STAT_SAVE_PARALYSIS) < (Short.MAX_VALUE / 2))
            affectedStats.setStat(CharStats.STAT_SAVE_PARALYSIS, affectedStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("The aura of mobility around you fades."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        final Room room = mob.location();
        int affectType = CMMsg.MSG_CAST_VERBAL_SPELL;
        if (auto)
            affectType = affectType | CMMsg.MASK_ALWAYS;
        if ((success) && (room != null)) {
            CMMsg msg = CMClass.getMsg(mob, null, this, affectType, auto ? "" : L("^S<S-NAME> @x1 for an aura of mobility!^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB target = room.fetchInhabitant(i);
                    if (target == null)
                        break;

                    msg = CMClass.getMsg(mob, target, this, affectType, L("Mobility is invoked upon <T-NAME>."));
                    if (mob.location().okMessage(mob, msg)) {
                        mob.location().send(mob, msg);
                        beneficialAffect(mob, target, asLevel, 0);
                    }
                }
            }
        } else {
            beneficialWordsFizzle(mob, null, L("<S-NAME> @x1, but nothing happens.", prayWord(mob)));
            return false;
        }
        return success;
    }
}
