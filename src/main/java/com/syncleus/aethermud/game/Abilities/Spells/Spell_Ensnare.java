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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Spell_Ensnare extends Spell {

    private final static String localizedName = CMLib.lang().L("Ensnare");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Ensnared)");
    public int amountRemaining = 0;

    @Override
    public String ID() {
        return "Spell_Ensnare";
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
    public int maxRange() {
        return adjustedMaxInvokerRange(5);
    }

    @Override
    public int minRange() {
        return 1;
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if (msg.amISource(mob)) {
            switch (msg.sourceMinor()) {
                case CMMsg.TYP_ENTER:
                case CMMsg.TYP_ADVANCE:
                case CMMsg.TYP_LEAVE:
                case CMMsg.TYP_FLEE:
                    if (mob.location().show(mob, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> struggle(s) against the ensnarement."))) {
                        amountRemaining -= mob.phyStats().level();
                        if (amountRemaining < 0)
                            unInvoke();
                    }
                    return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() & (~(PhyStats.IS_FLYING)));
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked())
            mob.location().show(mob, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> manage(s) to break <S-HIS-HER> way free of the ensnarement."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth ensnaring."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (mob.location().show(mob, null, this, somanticCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> speak(s) and wave(s) <S-HIS-HER> fingers at the ground.^?"))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), null);
                    if ((mob.location().okMessage(mob, msg)) && (target.fetchEffect(this.ID()) == null)) {
                        mob.location().send(mob, msg);
                        if (msg.value() <= 0) {
                            amountRemaining = 60;
                            if (target.location() == mob.location()) {
                                success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                                target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> become(s) ensnared, and is unable to move <S-HIS-HER> feet!"));
                            }
                        }
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> speak(s) and wave(s) <S-HIS-HER> fingers, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
