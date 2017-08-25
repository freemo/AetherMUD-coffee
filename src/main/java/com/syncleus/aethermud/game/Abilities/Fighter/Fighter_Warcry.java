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
package com.planet_ink.game.Abilities.Fighter;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Fighter_Warcry extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("War Cry");
    private final static String localizedStaticDisplay = CMLib.lang().L("(War Cry)");
    private static final String[] triggerStrings = I(new String[]{"WARCRY"});
    protected int timesTicking = 0;

    @Override
    public String ID() {
        return "Fighter_Warcry";
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
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_SINGING;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (invoker == null)
            return;
        final float f = (float) CMath.mul(0.1, (float) getXLEVELLevel(invoker()));
        affectableStats.setDamage(affectableStats.damage() + 1 + (int) Math.round(CMath.div(affectableStats.damage(), 4.0 - f)));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected == null) || (invoker == null) || (!(affected instanceof MOB)))
            return false;
        if ((!((MOB) affected).isInCombat()) && (++timesTicking > 5))
            unInvoke();
        return true;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("You calm down a bit."));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_SPEAK, auto ? "" : L("^S<S-NAME> scream(s) a mighty WAR CRY!!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Set<MOB> h = properTargets(mob, givenTarget, auto);
                if (h == null)
                    return false;
                for (final Object element : h) {
                    final MOB target = (MOB) element;
                    target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> get(s) enraged!"));
                    timesTicking = 0;
                    beneficialAffect(mob, target, asLevel, 0);
                }
            }
        } else
            beneficialWordsFizzle(mob, null, auto ? "" : L("<S-NAME> mumble(s) a weak war cry."));

        // return whether it worked
        return success;
    }
}
