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
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Prayer_CurseMinds extends Prayer {
    private final static String localizedName = CMLib.lang().L("Curse Minds");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Cursed Mind)");
    boolean notAgain = false;

    @Override
    public String ID() {
        return "Prayer_CurseMinds";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);

        if (!super.tick(ticking, tickID))
            return false;
        final MOB mob = (MOB) affected;
        if (mob.isInCombat()) {
            final MOB newvictim = mob.location().fetchRandomInhabitant();
            if (newvictim != mob)
                mob.setVictim(newvictim);
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked())
            mob.tell(L("Your mind feels less cursed."));
        CMLib.commands().postStand(mob, true);
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, affectableStats.getStat(CharStats.STAT_SAVE_MIND) - 50);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null)
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        boolean nothingDone = true;
        if (success) {
            for (final Object element : h) {
                final MOB target = (MOB) element;
                final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> @x1 an unholy curse upon <T-NAMESELF>.^?", prayWord(mob)));
                final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                if ((target != mob) && (mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                    mob.location().send(mob, msg);
                    mob.location().send(mob, msg2);
                    if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                        success = maliciousAffect(mob, target, asLevel, 15, -1) != null;
                        mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> look(s) confused!"));
                    }
                    nothingDone = false;
                }
            }
        }

        if (nothingDone)
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to curse everyone, but flub(s) it."));

        // return whether it worked
        return success;
    }
}
