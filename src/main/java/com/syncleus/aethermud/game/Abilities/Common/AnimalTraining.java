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
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.CagedAnimal;
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class AnimalTraining extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Animal Training");
    private static final String[] triggerStrings = I(new String[]{"ANIMALTRAINING"});
    protected Physical taming = null;
    protected String skillto = "";
    protected Object skill = null;
    protected boolean messedUp = false;

    public AnimalTraining() {
        super();
        displayText = L("You are taming...");
        verb = L("taming");
    }

    @Override
    public String ID() {
        return "AnimalTraining";
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
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null)
            && (affected instanceof MOB)
            && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if ((taming == null) || (mob.location() == null)) {
                messedUp = true;
                unInvoke();
            }
            if ((taming instanceof MOB) && (!mob.location().isInhabitant((MOB) taming))) {
                messedUp = true;
                unInvoke();
            }
            if ((taming instanceof Item) && (!mob.location().isContent((Item) taming))) {
                messedUp = true;
                unInvoke();
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((taming != null) && (!aborted)) {
                    MOB animal = null;
                    if (taming instanceof MOB)
                        animal = (MOB) taming;
                    else if ((taming != null) && (taming instanceof CagedAnimal))
                        animal = ((CagedAnimal) taming).unCageMe();
                    if ((messedUp) || (animal == null) || (skill == null))
                        commonTell(mob, L("You've failed to train @x1!", taming.name()));
                    else {
                        if (animal.numBehaviors() == 0)
                            commonTell(mob, L("@x1 is already tame.", taming.name()));
                        else {
                            final String s = " to " + skillto;
                            mob.location().show(mob, null, getActivityMessageType(), L("<S-NAME> manage(s) to train @x1 @x2.", animal.name(), s));
                            if (skill instanceof Behavior)
                                animal.addBehavior((Behavior) skill);
                            else if (skill instanceof Ability) {
                                ((Ability) skill).setProficiency(100);
                                animal.addAbility((Ability) skill);
                            }
                            animal.recoverCharStats();
                            animal.recoverPhyStats();
                            animal.recoverMaxState();
                            if (taming instanceof CagedAnimal) {
                                animal.text();
                                ((CagedAnimal) taming).cageMe(animal);
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

        verb = L("training");
        taming = null;
        Item cage = null;
        final String[] skills = {"WANDER", //0
            "HUNT", //1
            "KILL", //2
            "DOORGUARD" //3
        };
        String valid = "Skills include:";
        for (final String skill2 : skills)
            valid += " " + skill2;
        valid += ".";
        if (commands.size() < 2) {
            commonTell(mob, L("Train whom to do what? @x1", valid));
            return false;
        }
        skill = null;
        final String what = commands.get(commands.size() - 1);
        commands.remove(commands.size() - 1);
        for (int i = 0; i < skills.length; i++) {
            if (skills[i].startsWith(what.toUpperCase())) {
                switch (i) {
                    case 0:
                        skill = CMClass.getBehavior("Mobile");
                        break;
                    case 1:
                        skill = CMClass.getAbility("Hunt");
                        break;
                    case 2:
                        skill = CMClass.getBehavior("Aggressive");
                        break;
                    case 3:
                        skill = CMClass.getBehavior("DoorwayGuardian");
                        break;
                }
                if (skill != null)
                    skillto = skills[i].toLowerCase();
            }
        }
        if (skill == null) {
            commonTell(mob, L("Train whom to do what? @x1", valid));
            return false;
        }

        final String str = CMParms.combine(commands, 0);
        MOB M = mob.location().fetchInhabitant(str);
        taming = null;
        if (M != null) {
            if (!CMLib.flags().canBeSeenBy(M, mob)) {
                commonTell(mob, L("You don't see anyone called '@x1' here.", str));
                return false;
            }
            if ((!M.isMonster()) || (!CMLib.flags().isAnimalIntelligence(M))) {
                commonTell(mob, L("You can't train @x1.", M.name(mob)));
                return false;
            }
            if ((CMLib.flags().canMove(M)) && (!CMLib.flags().isBoundOrHeld(M))) {
                commonTell(mob, L("@x1 doesn't seem willing to cooperate.", M.name(mob)));
                return false;
            }
            taming = M;
        } else if (mob.location() != null) {
            for (int i = 0; i < mob.location().numItems(); i++) {
                final Item I = mob.location().getItem(i);
                if ((I != null)
                    && (I instanceof Container)
                    && ((((Container) I).containTypes() & Container.CONTAIN_CAGED) == Container.CONTAIN_CAGED)) {
                    cage = I;
                    break;
                }
            }
            if (commands.size() > 0) {
                final String last = commands.get(commands.size() - 1);
                final Item I = mob.location().findItem(null, last);
                if ((I != null)
                    && (I instanceof Container)
                    && ((((Container) I).containTypes() & Container.CONTAIN_CAGED) == Container.CONTAIN_CAGED)) {
                    cage = I;
                    commands.remove(last);
                }
            }
            if (cage == null) {
                commonTell(mob, L("You don't see anyone called '@x1' here.", str));
                return false;
            }
            taming = mob.location().findItem(cage, CMParms.combine(commands, 0));
            if ((taming == null) || (!CMLib.flags().canBeSeenBy(taming, mob)) || (!(taming instanceof CagedAnimal))) {
                commonTell(mob, L("You don't see any creatures in @x1 called '@x2'.", cage.name(), CMParms.combine(commands, 0)));
                return false;
            }
            M = ((CagedAnimal) taming).unCageMe();
        } else
            return false;

        if (((skill instanceof Behavior) && (M.fetchBehavior(((Behavior) skill).ID()) != null))
            || (skill instanceof Ability) && (M.fetchAbility(((Ability) skill).ID()) != null)) {
            commonTell(mob, L("@x1 already knows how to do that.", M.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        messedUp = !proficiencyCheck(mob, -taming.phyStats().level() + (2 * getXLEVELLevel(mob)), auto);
        final int duration = getDuration(35, mob, taming.phyStats().level(), 10);
        verb = L("training @x1", M.name());
        final CMMsg msg = CMClass.getMsg(mob, null, this, getActivityMessageType(), L("<S-NAME> start(s) training @x1.", M.name()));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
