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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.MendingSkill;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Bandaging extends CommonSkill implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Bandaging");
    private static final String[] triggerStrings = I(new String[]{"BANDAGE", "BANDAGING"});
    protected Physical bandaging = null;
    protected boolean messedUp = false;

    public Bandaging() {
        super();
        displayText = L("You are bandaging...");
        verb = L("bandaging");
    }

    @Override
    public String ID() {
        return "Bandaging";
    }

    @Override
    public String name() {
        return localizedName;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_ANATOMY;
    }

    @Override
    public boolean supportsMending(Physical item) {
        if (!(item instanceof MOB))
            return false;
        return (item.fetchEffect("Bleeding") != null)
            || (item.fetchEffect("Injury") != null)
            || (item.fetchEffect("BrokenLimbs") != null);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null)
            && (affected instanceof MOB)
            && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if ((bandaging == null) || (mob.location() == null)) {
                messedUp = true;
                unInvoke();
            }
            if ((bandaging instanceof MOB) && (!mob.location().isInhabitant((MOB) bandaging))) {
                messedUp = true;
                unInvoke();
            }
            if (mob.curState().adjHitPoints(super.getXLEVELLevel(invoker()) + (int) Math.round(CMath.div(mob.phyStats().level(), 2.0)), mob.maxState()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> mend(s) and heal(s)."));
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((bandaging != null) && (!aborted)) {
                    if ((messedUp) || (bandaging == null))
                        commonTell(mob, L("You've failed to bandage @x1!", bandaging.name()));
                    else {
                        Ability A = bandaging.fetchEffect("Bleeding");
                        if (A != null)
                            A.unInvoke();
                        A = bandaging.fetchEffect("Injury");
                        if (A != null)
                            A.unInvoke();
                        A = bandaging.fetchEffect("BrokenLimbs");
                        if (A != null) {
                            mob.tell(mob, bandaging, null, L("You finish setting <T-YOUPOSS> bones, so that they can now heal properly."));
                            if ((bandaging instanceof MOB) && (bandaging != mob))
                                ((MOB) bandaging).tell(mob, bandaging, null, L("<S-NAME> finishes setting your broken bones, so that they can now heal properly."));
                            A.setMiscText("+SETBONES");
                        }
                    }
                }
            }
        }
        super.unInvoke();
    }

    public double healthPct(MOB mob) {
        return CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints());
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        verb = L("bandaging");
        bandaging = null;
        final MOB target = super.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if ((target.fetchEffect("Bleeding") == null)
            && (target.fetchEffect("BrokenLimbs") == null)
            && (target.fetchEffect("Injury") == null)) {
            super.commonTell(mob, target, null, L("<T-NAME> <T-IS-ARE> not bleeding, broken or injured!"));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        messedUp = !proficiencyCheck(mob, 0, auto);
        int duration = 3 + (int) Math.round(10 * (1.0 - healthPct(target))) - getXLEVELLevel(mob);
        if (duration < 3)
            duration = 3;
        verb = L("bandaging @x1", target.name());
        bandaging = target;
        final String msgStr = (target.fetchEffect("BrokenLimbs") != null) ?
            L("<S-NAME> begin(s) bandaging up <T-YOUPOSS> and setting <T-HIS-HER> bones.") :
            L("<S-NAME> begin(s) bandaging up <T-YOUPOSS> wounds.");
        final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_DELICATE_HANDS_ACT, msgStr);
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
