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
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Domesticating extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Domesticating");
    private static final String[] triggerStrings = I(new String[]{"DOMESTICATE", "DOMESTICATING"});
    protected MOB taming = null;
    protected boolean messedUp = false;

    public Domesticating() {
        super();
        displayText = L("You are domesticating...");
        verb = L("domesticating");
    }

    @Override
    public String ID() {
        return "Domesticating";
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
            if ((taming == null) || (!mob.location().isInhabitant(taming))) {
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
                    if (messedUp)
                        commonTell(mob, L("You've failed to domesticate @x1!", taming.name()));
                    else {
                        if (taming.amFollowing() == mob)
                            commonTell(mob, L("@x1 is already domesticated.", taming.name()));
                        else {
                            CMLib.commands().postFollow(taming, mob, true);
                            if (taming.amFollowing() == mob)
                                mob.location().show(mob, null, getActivityMessageType(), L("<S-NAME> manage(s) to domesticate @x1.", taming.name()));
                            else
                                mob.location().show(mob, null, getActivityMessageType(), L("<S-NAME> domesticate(s) @x1, but @x1 won't follow <S-HIM-HER>.", taming.name()));
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
        taming = null;
        String str = CMParms.combine(commands, 0);
        String newName = null;
        if ((commands.size() > 2) && ((commands.get(0)).equalsIgnoreCase("name"))) {
            str = commands.get(1);
            newName = CMParms.combine(commands, 2);
        }
        final MOB M = mob.location().fetchInhabitant(str);
        if ((M == null) || (!CMLib.flags().canBeSeenBy(M, mob))) {
            commonTell(mob, L("You don't see anyone called '@x1' here.", str));
            return false;
        }
        if (!M.isMonster()) {
            if (newName != null)
                commonTell(mob, M, null, L("You can't name <T-NAME>."));
            else
                commonTell(mob, M, null, L("You can't domesticate <T-NAME>."));
            return false;
        }
        if (!CMLib.flags().isAnimalIntelligence(M)) {
            if (newName != null)
                commonTell(mob, M, null, L("You can't name <T-NAME>."));
            else
                commonTell(mob, M, null, L("You don't know how to domesticate <T-NAME>."));
            return false;
        }
        final String theName = newName;
        if ((newName != null) && (M.amFollowing() == null)) {
            commonTell(mob, L("You can only name someones pet."));
            return false;
        } else if (newName != null) {
            if (newName.trim().length() == 0) {
                mob.tell(L("You must specify a name."));
                return false;
            }
            if (newName.indexOf(' ') >= 0) {
                mob.tell(L("The name may not contain a space."));
                return false;
            }
            String oldName = M.name();
            final Vector<String> oldV = CMParms.parse(oldName);
            if (oldV.size() > 1) {
                if (oldName.endsWith(", " + (oldV.lastElement())))
                    oldName = oldName.substring(0, oldName.length() - (2 + oldV.lastElement().length()));
                else if (oldName.endsWith("named " + (oldV.lastElement())))
                    oldName = oldName.substring(0, oldName.length() - (6 + oldV.lastElement().length()));
            }

            if ((oldName.toUpperCase().startsWith("A "))
                || (oldName.toUpperCase().startsWith("AN "))
                || (oldName.toUpperCase().startsWith("SOME ")))
                newName = oldName + " named " + newName;
            else if (oldName.indexOf(' ') >= 0)
                newName = newName + ", " + oldName;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        taming = M;
        verb = L("domesticating @x1", M.name());
        int levelDiff = taming.phyStats().level() - xlevel(mob);
        if (levelDiff < 0)
            levelDiff = 0;
        messedUp = !proficiencyCheck(mob, -(levelDiff * 5), auto);
        int duration = 35 + levelDiff;
        if (duration < 10)
            duration = 10;
        final CMMsg msg = CMClass.getMsg(mob, null, this, getActivityMessageType(),
            (newName != null) ? L("<S-NAME> name(s) @x1 '@x2'.", M.name(), theName) : L("<S-NAME> start(s) domesticating @x1.", M.name()));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            if (newName != null) {
                final Ability A = CMClass.getAbility("Chant_BestowName");
                if (A != null) {
                    A.setMiscText(newName);
                    M.addNonUninvokableEffect(A);
                    mob.location().recoverRoomStats();
                }
            } else
                beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
