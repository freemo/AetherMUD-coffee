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
package com.planet_ink.coffee_mud.Abilities.Common;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Shearing extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Shearing");
    private static final String[] triggerStrings = I(new String[]{"SHEAR", "SHEARING"});
    protected boolean failed = false;
    private MOB sheep = null;

    public Shearing() {
        super();
        displayText = L("You are shearing something...");
        verb = L("shearing");
    }

    @Override
    public String ID() {
        return "Shearing";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    protected int getDuration(MOB mob, int weight) {
        int duration = ((weight / (10 + getXLEVELLevel(mob))));
        duration = super.getDuration(duration, mob, 1, 10);
        if (duration > 40)
            duration = 40;
        return duration;
    }

    @Override
    protected int baseYield() {
        return 1;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((sheep != null)
            && (affected instanceof MOB)
            && (((MOB) affected).location() != null)
            && ((!((MOB) affected).location().isInhabitant(sheep))))
            unInvoke();
        return super.tick(ticking, tickID);
    }

    public Vector<RawMaterial> getMyWool(MOB M) {
        final Vector<RawMaterial> wool = new Vector<RawMaterial>();
        if ((M != null)
            && (M.charStats().getMyRace() != null)
            && (M.charStats().getMyRace().myResources() != null)
            && (M.charStats().getMyRace().myResources().size() > 0)) {
            final List<RawMaterial> V = M.charStats().getMyRace().myResources();
            for (int v = 0; v < V.size(); v++) {
                if ((V.get(v) != null)
                    && (V.get(v).material() == RawMaterial.RESOURCE_WOOL))
                    wool.addElement(V.get(v));
            }
        }
        return wool;
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((sheep != null) && (!aborted) && (mob.location() != null)) {
                    if ((failed) || (!mob.location().isInhabitant(sheep)))
                        commonTell(mob, L("You messed up your shearing completely."));
                    else {
                        final int yield = (baseYield() + abilityCode()) <= 0 ? 1 : (baseYield() + abilityCode());
                        final CMMsg msg = CMClass.getMsg(mob, sheep, this, getCompletedActivityMessageType(), null);
                        msg.setValue(yield);
                        if (mob.location().okMessage(mob, msg)) {
                            spreadImmunity(sheep);
                            msg.modify(L("<S-NAME> manage(s) to shear <T-NAME>."));
                            mob.location().send(mob, msg);
                            for (int i = 0; i < msg.value(); i++) {
                                final Vector<RawMaterial> V = getMyWool(sheep);
                                for (int v = 0; v < V.size(); v++) {
                                    RawMaterial I = V.elementAt(v);
                                    I = (RawMaterial) I.copyOf();
                                    if (!dropAWinner(mob, I))
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        MOB target = null;
        final Room R = mob.location();
        if (R == null)
            return false;
        sheep = null;
        if ((mob.isMonster()
            && (!CMLib.flags().isAnimalIntelligence(mob)))
            && (commands.size() == 0)) {
            for (int i = 0; i < R.numInhabitants(); i++) {
                final MOB M = R.fetchInhabitant(i);
                if ((M != mob) && (CMLib.flags().canBeSeenBy(M, mob)) && (getMyWool(M).size() > 0)) {
                    target = M;
                    break;
                }
            }
        } else if (commands.size() == 0)
            mob.tell(L("Shear what?"));
        else
            target = super.getTarget(mob, commands, givenTarget);

        if (target == null)
            return false;
        if ((getMyWool(target).size() <= 0)
            || (!target.okMessage(target, CMClass.getMsg(target, target, this, CMMsg.MSG_OK_ACTION, null)))) {
            commonTell(mob, target, null, L("You can't shear <T-NAME>, there's no wool left on <T-HIM-HER>."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        failed = !proficiencyCheck(mob, 0, auto);
        final CMMsg msg = CMClass.getMsg(mob, target, this, getActivityMessageType(), getActivityMessageType(), getActivityMessageType(), L("<S-NAME> start(s) shearing <T-NAME>."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            sheep = target;
            verb = L("shearing @x1", target.name());
            playSound = "scissor.wav";
            final int duration = getDuration(mob, target.phyStats().weight());
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
