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
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.game.Common.interfaces.LegalWarrant;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Skill_AboveTheLaw extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Above The Law");
    protected LegalBehavior B = null;
    protected Area O = null;
    protected Area A = null;

    @Override
    public String ID() {
        return "Skill_AboveTheLaw";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_LEGAL;
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
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            final Room room = mob.location();
            if (room != null) {
                if ((A == null) || (room.getArea() != A)) {
                    if (isSavable()
                        || ((mob.getStartRoom() != null) && (room.getArea() == mob.getStartRoom().getArea()))) {
                        A = room.getArea();
                        if (isSavable() || proficiencyCheck(mob, 0, false)) {
                            O = CMLib.law().getLegalObject(A);
                            B = CMLib.law().getLegalBehavior(room);
                        }
                    }
                }
            }
            if (B != null) {
                final List<LegalWarrant> warrants = B.getWarrantsOf(O, mob);
                for (final LegalWarrant W : warrants) {
                    W.setCrime("pardoned");
                    W.setOffenses(0);
                    if ((!isSavable()) && (CMLib.dice().rollPercentage() < 10))
                        helpProficiency(mob, 0);
                }
            }
        }
        return true;
    }
}
