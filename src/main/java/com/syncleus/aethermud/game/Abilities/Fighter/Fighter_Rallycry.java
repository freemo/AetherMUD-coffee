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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Fighter_Rallycry extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Rally Cry");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Rally Cry)");
    private static final String[] triggerStrings = I(new String[]{"RALLYCRY"});
    protected int timesTicking = 0;
    protected int hpUp = 0;

    @Override
    public String ID() {
        return "Fighter_Rallycry";
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
    public void affectCharState(MOB affected, CharState affectableStats) {
        super.affectCharState(affected, affectableStats);
        if (invoker == null)
            return;
        affectableStats.setHitPoints(affectableStats.getHitPoints() + hpUp);
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

        if (canBeUninvoked()) {
            mob.tell(L("You feel less rallied."));
            mob.recoverMaxState();
            if (mob.curState().getHitPoints() > mob.maxState().getHitPoints())
                mob.curState().setHitPoints(mob.maxState().getHitPoints());
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_SPEAK, auto ? "" : L("^S<S-NAME> scream(s) a mighty RALLYING CRY!!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Set<MOB> h = properTargets(mob, givenTarget, auto);
                if (h == null)
                    return false;
                for (final Object element : h) {
                    final MOB target = (MOB) element;
                    target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> seem(s) rallied!"));
                    timesTicking = 0;
                    hpUp = mob.phyStats().level() + (2 * getXLEVELLevel(mob));
                    beneficialAffect(mob, target, asLevel, 0);
                    target.recoverMaxState();
                    if (target.fetchEffect(ID()) != null)
                        mob.curState().adjHitPoints(hpUp, mob.maxState());
                }
            }
        } else
            beneficialWordsFizzle(mob, null, auto ? "" : L("<S-NAME> mumble(s) a weak rally cry."));

        // return whether it worked
        return success;
    }
}
