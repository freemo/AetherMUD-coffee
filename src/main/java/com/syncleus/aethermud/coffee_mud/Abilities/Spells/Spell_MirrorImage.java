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
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Random;


public class Spell_MirrorImage extends Spell {

    private final static String localizedName = CMLib.lang().L("Mirror Image");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mirror Image spell)");
    private final Random randomizer = new Random(System.currentTimeMillis());
    protected int numberOfImages = 0;
    protected boolean notAgain = false;

    @Override
    public String ID() {
        return "Spell_MirrorImage";
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
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if ((msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (mob != msg.source())) {
            if (numberOfImages <= 0) {
                unInvoke();
                return true;
            }
            int intAdjustment = (msg.source().charStats().getMaxStat(CharStats.STAT_INTELLIGENCE)
                - mob.charStats().getStat(CharStats.STAT_INTELLIGENCE)) / 2;
            if (intAdjustment < 1)
                intAdjustment = 1;

            final int numberOfTargets = numberOfImages + intAdjustment;
            if (randomizer.nextInt() % numberOfTargets >= intAdjustment) {
                if (mob.location().show(msg.source(), mob, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> attack(s) a mirrored image!")))
                    numberOfImages--;
                return false;
            }
        }
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if (!(affected instanceof MOB))
            return;

        if (notAgain)
            return;

        final MOB mob = (MOB) affected;
        if (msg.amISource(mob)) {
            if ((
                (CMath.bset(msg.othersMajor(), CMMsg.MASK_EYES))
                    || (CMath.bset(msg.othersMajor(), CMMsg.MASK_MOVE))
                    || (CMath.bset(msg.othersMajor(), CMMsg.MASK_MOUTH))
                    || (CMath.bset(msg.othersMajor(), CMMsg.MASK_HANDS)))
                && (msg.othersMessage() != null)
                && (msg.targetMinor() != CMMsg.TYP_DAMAGE)
                && (msg.othersMessage().length() > 0)) {
                notAgain = true;
                if (numberOfImages <= 0)
                    unInvoke();
                else
                    for (int x = 0; x < numberOfImages; x++)
                        msg.addTrailerMsg(CMClass.getMsg(mob, msg.target(), msg.tool(), CMMsg.MSG_OK_VISUAL, msg.othersMessage()));
            }
        } else if ((msg.amITarget(mob.location())
            && (!msg.amISource(mob))
            && ((msg.targetMinor() == CMMsg.TYP_LOOK) || (msg.targetMinor() == CMMsg.TYP_EXAMINE))
            && (CMLib.flags().canBeSeenBy(mob, msg.source()))
            && (mob.displayText(msg.source()).length() > 0))) {
            final StringBuffer Say = new StringBuffer("");
            final boolean compress = msg.source().isAttributeSet(MOB.Attrib.COMPRESS);
            for (int i = 0; i < numberOfImages; i++) {
                Say.append("^M");
                if (compress)
                    Say.append(CMLib.flags().getDispositionBlurbs(mob, mob) + "^M ");
                if (mob.displayText(msg.source()).length() > 0)
                    Say.append(CMStrings.endWithAPeriod(CMStrings.capitalizeFirstLetter(mob.displayText(msg.source()))));
                else
                    Say.append(CMStrings.endWithAPeriod(CMStrings.capitalizeFirstLetter(mob.name())));
                if (!compress)
                    Say.append(CMLib.flags().getDispositionBlurbs(mob, msg.source()) + "^N\n\r");
                else
                    Say.append("^N");
            }
            if (Say.toString().length() > 0) {
                final CMMsg msg2 = CMClass.getMsg(msg.source(), null, this, CMMsg.MSG_OK_VISUAL, Say.toString(), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
                msg.addTrailerMsg(msg2);
            }
        }
        notAgain = false;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (numberOfImages > 0)
            affectableStats.setArmor(affectableStats.armor() - 1 - getXLEVELLevel(invoker()));
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            numberOfImages = 0;
        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your mirror images fade away."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> already <S-HAS-HAVE> mirror images."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            numberOfImages = CMLib.dice().roll(1, (int) (Math.round(CMath.div(adjustedLevel(mob, asLevel), 3.0))), 2);
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L((auto ? "A spell forms around" : "^S<S-NAME> incant(s) the reflective spell of") + " <T-NAME>, and suddenly @x1 copies appear.^?", "" + numberOfImages));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else {
            numberOfImages = 0;
            return beneficialWordsFizzle(mob, target, L("<S-NAME> speak(s) reflectively, but nothing more happens."));
        }
        // return whether it worked
        return success;
    }
}
