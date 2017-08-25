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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_FaerieFire extends Spell {

    private final static String localizedName = CMLib.lang().L("Faerie Fire");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Faerie Fire)");

    @Override
    public String ID() {
        return "Spell_FaerieFire";
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
        return Ability.QUALITY_MALICIOUS;
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
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("The faerie fire around <S-NAME> fades."));
        super.unInvoke();
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);

        if ((affectableStats.disposition() & PhyStats.IS_INVISIBLE) == PhyStats.IS_INVISIBLE)
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_INVISIBLE);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_GLOWING);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_BONUS);
        affectableStats.setArmor(affectableStats.armor() + 10);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (mob.isMonster())) {
            if (target instanceof MOB) {
                if (!CMLib.flags().isInvisible(target))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        Room R = CMLib.map().roomLocation(target);
        if (R == null)
            R = mob.location();

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), L((auto ? "A " : "^S<S-NAME> speak(s) and gesture(s) and a ") + "twinkling fire envelopes <T-NAME>.^?"));
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                maliciousAffect(mob, target, asLevel, 0, -1);
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> mutter(s) about a faerie fire, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
