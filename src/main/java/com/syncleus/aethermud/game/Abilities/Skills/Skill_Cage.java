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
package com.planet_ink.game.Abilities.Skills;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.CagedAnimal;
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Skill_Cage extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Cage");
    private static final String[] triggerStrings = I(new String[]{"CAGE"});

    @Override
    public String ID() {
        return "Skill_Cage";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ANIMALAFFINITY;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null) && (mob.isInCombat()))
            return Ability.QUALITY_INDIFFERENT;
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Item cage = null;
        if (mob.location() != null) {
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
        }

        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if ((!auto) && (!(CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.ORDER) || target.willFollowOrdersOf(mob)))) {
            boolean ok = false;
            if ((target.isMonster())
                && (CMLib.flags().isAnimalIntelligence(target))) {
                if (CMLib.flags().isSleeping(target)
                    || (!CMLib.flags().canMove(target))
                    || ((target.amFollowing() == mob))
                    || (CMLib.flags().isBoundOrHeld(target)))
                    ok = true;
            }
            if (!ok) {
                mob.tell(L("@x1 won't seem to let you.", target.name(mob)));
                return false;
            }

            if (cage == null) {
                mob.tell(L("Cage @x1 where?", target.name(mob)));
                return false;
            }

            if ((mob.isInCombat()) && (mob.getVictim() != target)) {
                mob.tell(L("Not while you are fighting!"));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        final CagedAnimal caged = (CagedAnimal) CMClass.getItem("GenCaged");
        if ((success) && (caged.cageMe(target))) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_JUSTICE, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if ((cage != null) && (cage.owner() != null)) {
                    if (cage.owner() instanceof MOB)
                        ((MOB) cage.owner()).addItem(caged);
                    else if (cage.owner() instanceof Room)
                        ((Room) cage.owner()).addItem(caged);
                } else
                    mob.addItem(caged);
                final CMMsg putMsg = CMClass.getMsg(mob, cage, caged, CMMsg.MSG_PUT, L("<S-NAME> cage(s) <O-NAME> in <T-NAME>."));
                if (mob.location().okMessage(mob, putMsg)) {
                    mob.location().send(mob, putMsg);
                    target.killMeDead(false);
                } else
                    ((Item) caged).destroy();
                mob.location().recoverRoomStats();
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to cage <T-NAME> and fail(s)."));

        // return whether it worked
        return success;
    }
}
