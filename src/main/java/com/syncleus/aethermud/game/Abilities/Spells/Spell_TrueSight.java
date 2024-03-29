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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_TrueSight extends Spell {

    private final static String localizedName = CMLib.lang().L("True Sight");
    private final static String localizedStaticDisplay = CMLib.lang().L("(True Sight)");

    @Override
    public String ID() {
        return "Spell_TrueSight";
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
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked())
            mob.tell(L("You no longer have true sight."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if (!(affected instanceof MOB))
            return true;
        if ((msg.amISource((MOB) affected))
            && ((msg.targetMinor() == CMMsg.TYP_LOOK) || (msg.targetMinor() == CMMsg.TYP_EXAMINE))
            && (msg.target() != null)
            && (!msg.target().name().equals(msg.target().Name())))
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), null, this, CMMsg.MSG_OK_VISUAL, L("^H@x1 is truly @x2.^N", msg.target().name(), msg.target().Name()), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null));
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.source() != affected)
            && (affected instanceof MOB)
            && (msg.source().fetchEffect("Spell_ObscureSelf") != null)) {
            if ((msg.target() == affected)
                && ((msg.targetMessage() != null) && (msg.targetMessage().toLowerCase().indexOf("someone") >= 0))) {
                msg.addTrailerRunnable(new Runnable() {
                    final MOB M = (MOB) affected;

                    @Override
                    public void run() {
                        if (M != null)
                            M.tell(L("^HThat someone was @x1.^N", msg.source().Name()));
                    }
                });
            } else if ((msg.othersMessage() != null) && (msg.othersMessage().toLowerCase().indexOf("someone") >= 0)) {
                msg.addTrailerRunnable(new Runnable() {
                    final MOB M = (MOB) affected;

                    @Override
                    public void run() {
                        if (M != null)
                            M.tell(L("^HThat someone was @x1.^N", msg.source().Name()));
                    }
                });
            }

        }
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING, affected.phyStats().level() + (2 * getXLEVELLevel(invoker())) + 200 + affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING));
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_HIDDEN);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_INVISIBLE);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_SNEAKERS);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat() && (!CMLib.flags().canBeSeenBy(mob.getVictim(), mob)))
                return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> already <S-HAS-HAVE> true sight."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> gain(s) true sight!") : L("^S<S-NAME> incant(s) softly, and gain(s) true sight!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> incant(s) and open(s) <S-HIS-HER> eyes, but the spell fizzles."));

        return success;
    }
}
