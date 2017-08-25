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
import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Fighter_Toughness extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Toughness");

    @Override
    public String ID() {
        return "Fighter_Toughness";
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
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
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
    public void affectCharState(MOB affected, CharState affectableState) {
        super.affectCharState(affected, affectableState);
        affectableState.setHitPoints(affectableState.getHitPoints() + 30 + (int) Math.round(2.0 * adjustedLevel(invoker, 0) * CMath.div(proficiency(), 100.0)));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        if ((tickID == Tickable.TICKID_MOB)
            && (affected instanceof MOB)
            && (((MOB) affected).location() != null)) {
            final MOB mob = (MOB) affected;
            if (mob.isInCombat() && (CMLib.dice().rollPercentage() < 5)) {
                super.helpProficiency((MOB) affected, 0);
            }
        }
        return true;
    }
}
