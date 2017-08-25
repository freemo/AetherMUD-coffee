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
import com.syncleus.aethermud.game.Common.interfaces.Social;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_Fertility extends Chant {
    private final static String localizedName = CMLib.lang().L("Fertility");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Fertility)");

    @Override
    public String ID() {
        return "Chant_Fertility";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your extreme fertility subsides."));
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        // the sex rules
        if (!(affected instanceof MOB))
            return;

        final MOB myChar = (MOB) affected;
        if (msg.target() instanceof MOB) {
            final MOB mate = (MOB) msg.target();
            if ((msg.amISource(myChar))
                && (msg.tool() instanceof Social)
                && (msg.tool().Name().equals("MATE <T-NAME>")
                || msg.tool().Name().equals("SEX <T-NAME>"))
                && (msg.sourceMinor() != CMMsg.TYP_CHANNEL)
                && (myChar.charStats().getStat(CharStats.STAT_GENDER) != mate.charStats().getStat(CharStats.STAT_GENDER))
                && ((mate.charStats().getStat(CharStats.STAT_GENDER) == ('M'))
                || (mate.charStats().getStat(CharStats.STAT_GENDER) == ('F')))
                && ((myChar.charStats().getStat(CharStats.STAT_GENDER) == ('M'))
                || (myChar.charStats().getStat(CharStats.STAT_GENDER) == ('F')))
                && (mate.charStats().getMyRace().canBreedWith(myChar.charStats().getMyRace()))
                && (myChar.location() == mate.location())
                && (myChar.fetchWornItems(Wearable.WORN_LEGS | Wearable.WORN_WAIST, (short) -2048, (short) 0).size() == 0)
                && (mate.fetchWornItems(Wearable.WORN_LEGS | Wearable.WORN_WAIST, (short) -2048, (short) 0).size() == 0)) {
                MOB female = myChar;
                MOB male = mate;
                if ((mate.charStats().getStat(CharStats.STAT_GENDER) == ('F'))) {
                    female = mate;
                    male = myChar;
                }
                final Ability A = CMClass.getAbility("Pregnancy");
                if ((A != null)
                    && (female.fetchAbility(A.ID()) == null)
                    && (female.fetchEffect(A.ID()) == null)) {
                    A.invoke(male, female, true, 0);
                    unInvoke();
                }
            }
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Ability A = target.fetchEffect("Chant_StrikeBarren");
                if (A != null) {
                    if (A.invoker() == null)
                        A.unInvoke();
                    else if (A.invoker().phyStats().level() < adjustedLevel(mob, asLevel))
                        A.unInvoke();
                    else {
                        mob.tell(L("The magical barrenness upon @x1 is too powerful.", target.name(mob)));
                        return false;
                    }
                }
                mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> seem(s) extremely fertile!"));
                beneficialAffect(mob, target, asLevel, Ability.TICKS_ALMOST_FOREVER);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades."));

        // return whether it worked
        return success;
    }
}
