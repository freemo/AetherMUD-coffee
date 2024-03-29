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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Skill_AdLib extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Ad Lib");
    private static final String[] triggerStrings = I(new String[]{"ADLIB"});
    protected MOB adLibbingM = null;

    @Override
    public String ID() {
        return "Skill_AdLib";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return L("(Waiting to Ad Lib @x1)", adLibbingM == null ? "no one" : adLibbingM.name(invoker()));
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_THEATRE;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            final Room R = mob.location();
            if (R == null) {
                unInvoke();
                return false;
            }
            if ((!R.isInhabitant(adLibbingM)) || (!CMLib.flags().canBeSeenBy(adLibbingM, mob))) {
                unInvoke();
                return false;
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if ((msg.source() == adLibbingM)
            && (msg.sourceMessage() != null)
            && (msg.sourceMessage().length() > 0)
            && (msg.tool() instanceof Ability)
            && (mob.location() != null)
            && (mob.location().isInhabitant(msg.source()))
            && (CMLib.flags().canBeSeenBy(msg.source(), mob))
            && (msg.source().fetchAbility(msg.tool().ID()) != null)) {
            final boolean hasAble = (mob.fetchAbility(msg.tool().ID()) != null);
            final int lowestLevel = CMLib.ableMapper().lowestQualifyingLevel(msg.tool().ID());
            int myLevel = 0;
            if (hasAble)
                myLevel = adjustedLevel(mob, 0) - lowestLevel + 1;
            final int lvl = (mob.phyStats().level() / 3) + getXLEVELLevel(mob);
            if (myLevel < lvl)
                myLevel = lvl;
            if ((!hasAble) && (lowestLevel <= myLevel)) {
                final Ability A = (Ability) msg.tool().copyOf();
                if (msg.target() instanceof Physical) {
                    mob.location().show(mob, msg.target(), A, CMMsg.MSG_OK_VISUAL, L("<S-NAME> attempt(s) to ad-lib <O-NAME> at <T-NAME>!"));
                    A.invoke(mob, new XVector<String>(msg.target().Name()), (Physical) msg.target(), false, 0);
                } else {
                    mob.location().show(mob, msg.target(), A, CMMsg.MSG_OK_VISUAL, L("<S-NAME> attempt(s) to ad-lib <O-NAME>!"));
                    A.invoke(mob, new XVector<String>(), null, false, 0);
                }
                unInvoke();
            }
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if ((target == null) || (target == mob))
            return false;

        final Room R = mob.location();
        if (R == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MAGIC | CMMsg.MSG_NOISYMOVEMENT | (auto ? CMMsg.MASK_ALWAYS : 0), L("<S-NAME> watch(es) <T-NAME> and prepares to ad-lib."));
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                Skill_AdLib A = (Skill_AdLib) beneficialAffect(mob, mob, asLevel, 3);
                if (A != null)
                    A.adLibbingM = target;
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> point(s) around, confusing <S-HIM-HERSELF>."));

        return success;
    }

}
