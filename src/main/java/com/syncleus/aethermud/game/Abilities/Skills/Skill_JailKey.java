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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.LegalBehavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


@SuppressWarnings({"unchecked", "rawtypes"})
public class Skill_JailKey extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Jail Key");
    private static final String[] triggerStrings = I(new String[]{"JAILKEY", "JKEY"});
    public int code = 0;

    @Override
    public String ID() {
        return "Skill_JailKey";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_EXITS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_LEGAL;
    }

    @Override
    public int abilityCode() {
        return code;
    }

    @Override
    public void setAbilityCode(int newCode) {
        code = newCode;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final String whatTounlock = CMParms.combine(commands, 0);
        Exit unlockThis = null;
        final int dirCode = CMLib.directions().getGoodDirectionCode(whatTounlock);
        if ((dirCode >= 0) && (mob.location() != null)) {
            unlockThis = mob.location().getExitInDir(dirCode);
            final Room unlockThat = mob.location().getRoomInDir(dirCode);
            if (unlockThat == null)
                unlockThis = null;
            if (unlockThis != null) {
                LegalBehavior B = null;

                final Area legalA = CMLib.law().getLegalObject(mob.location());
                if (legalA != null)
                    B = CMLib.law().getLegalBehavior(legalA);
                if (B == null)
                    unlockThis = null;
                else if (!B.isJailRoom(legalA, new XVector(mob.location())))
                    unlockThis = null;
            }
        }

        if (unlockThis == null) {
            if (dirCode < 0)
                mob.tell(L("You should specify a direction."));
            else {
                final Exit E = mob.location().getExitInDir(dirCode);
                if (E == null)
                    mob.tell(L("You must specify a jail door direction."));
                else if (!E.hasADoor())
                    mob.tell(L("You must specify a jail **DOOR** direction."));
                else if (!E.hasALock())
                    mob.tell(L("You must specify a **JAIL** door direction."));
                else if (E.isOpen())
                    mob.tell(L("@x1 is open already.", E.name()));
                else
                    mob.tell(L("That's not a jail door."));
            }
            return false;
        }

        if (!unlockThis.hasALock()) {
            mob.tell(L("There is no lock on @x1!", unlockThis.name()));
            return false;
        }

        if (unlockThis.isOpen()) {
            mob.tell(L("@x1 is open!", unlockThis.name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (!success)
            beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) <S-HIS-HER> jailkey on @x1 and fail(s).", unlockThis.name()));
        else {
            CMMsg msg = CMClass.getMsg(mob, unlockThis, this, auto ? CMMsg.MSG_OK_VISUAL : (CMMsg.MSG_THIEF_ACT), CMMsg.MSG_OK_VISUAL, CMMsg.MSG_OK_VISUAL, null);
            if (mob.location().okMessage(mob, msg)) {
                if (!unlockThis.isLocked())
                    msg = CMClass.getMsg(mob, unlockThis, null, CMMsg.MSG_OK_VISUAL, CMMsg.MSG_LOCK, CMMsg.MSG_OK_VISUAL, auto ? L("@x1 vibrate(s) and click(s).", unlockThis.name()) : L("<S-NAME> use(s) <S-HIS-HER> jailkey and relock(s) @x1.", unlockThis.name()));
                else
                    msg = CMClass.getMsg(mob, unlockThis, null, CMMsg.MSG_OK_VISUAL, CMMsg.MSG_UNLOCK, CMMsg.MSG_OK_VISUAL, auto ? L("@x1 vibrate(s) and click(s).", unlockThis.name()) : L("<S-NAME> use(s) <S-HIS-HER> jailkey and unlock(s) @x1.", unlockThis.name()));
                CMLib.utensils().roomAffectFully(msg, mob.location(), dirCode);
            }
        }

        return success;
    }
}
