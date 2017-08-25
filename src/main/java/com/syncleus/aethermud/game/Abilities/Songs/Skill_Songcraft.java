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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Skill_Songcraft extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Songcraft");
    public String lastID = "";

    @Override
    public String ID() {
        return "Skill_Songcraft";
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
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ARCANELORE;
    }

    public int craftType() {
        return Ability.ACODE_SONG;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if ((msg.sourceMinor() == CMMsg.TYP_CAST_SPELL)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (!msg.amISource(mob))
            && (msg.tool() instanceof Ability)
            && (msg.sourceMessage() != null)
            && (msg.sourceMessage().length() > 0)
            && ((((Ability) msg.tool()).classificationCode() & Ability.ALL_ACODES) == craftType())
            && (!lastID.equalsIgnoreCase(msg.tool().ID()))
            && (mob.location() != null)
            && (mob.location().isInhabitant(msg.source()))
            && (CMLib.flags().canBeSeenBy(msg.source(), mob))
            && (msg.source().fetchAbility(msg.tool().ID()) != null)) {
            final boolean hasAble = (mob.fetchAbility(ID()) != null);
            final int lowestLevel = CMLib.ableMapper().lowestQualifyingLevel(msg.tool().ID());
            int myLevel = 0;
            if (hasAble)
                myLevel = adjustedLevel(mob, 0) - lowestLevel + 1;
            final int lvl = (mob.phyStats().level() / 3) + getXLEVELLevel(mob);
            if (myLevel < lvl)
                myLevel = lvl;
            if (((!hasAble) || proficiencyCheck(mob, 0, false)) && (lowestLevel <= myLevel)) {
                final Ability A = (Ability) copyOf();
                A.setMiscText(msg.tool().ID());
                lastID = msg.tool().ID();
                msg.addTrailerMsg(CMClass.getMsg(mob, msg.source(), A, CMMsg.MSG_OK_VISUAL, L("<T-NAME> casts '@x1'.", msg.tool().name()), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null));
                helpProficiency(mob, 0);
            }
        }
    }
}
