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
package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Spell_DetectMagic extends Spell {

    private final static String localizedName = CMLib.lang().L("Detect Magic");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Detecting Magic)");

    @Override
    public String ID() {
        return "Spell_DetectMagic";
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
    public int enchantQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
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
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> eyes cease to sparkle."));
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((affected != null)
            && (affected instanceof MOB)
            && (msg.target() instanceof Physical)
            && (msg.amISource((MOB) affected))
            && ((msg.sourceMinor() == CMMsg.TYP_LOOK) || (msg.sourceMinor() == CMMsg.TYP_EXAMINE))
            && (CMLib.flags().canBeSeenBy(msg.target(), (MOB) affected))) {
            String msg2 = null;
            final Physical targetP = (Physical) msg.target();
            for (final Enumeration<Ability> a = targetP.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null)
                    && (!A.isAutoInvoked())
                    && (A.displayText().length() > 0)
                    && (((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
                    || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
                    || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SONG)
                    || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_CHANT))) {
                    if (msg2 == null)
                        msg2 = targetP.name() + " is affected by: " + A.name();
                    else
                        msg2 += " " + A.name();
                }
            }
            if ((msg2 == null) && (CMLib.flags().isABonusItems(targetP)))
                msg2 = msg.target().name() + " is enchanted";
            if (msg2 != null) {
                final CMMsg msg3 = CMClass.getMsg(msg.source(), targetP, this,
                    CMMsg.MSG_OK_VISUAL, msg2 + ".",
                    CMMsg.NO_EFFECT, null,
                    CMMsg.NO_EFFECT, null);

                msg.addTrailerMsg(msg3);
            }
        }
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_BONUS);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (((MOB) target).isInCombat())
                    return Ability.QUALITY_INDIFFERENT;
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
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already detecting magic."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> gain(s) sparkling eyes!") : L("^S<S-NAME> incant(s) softly, and gain(s) sparkling eyes!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> incant(s) and open(s) <S-HIS-HER> eyes sparkling, but the spell fizzles."));

        return success;
    }
}
