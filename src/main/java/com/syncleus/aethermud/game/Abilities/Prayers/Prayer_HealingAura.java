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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Libraries.interfaces.CMFlagLibrary;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.Set;


public class Prayer_HealingAura extends Prayer {
    private final static String localizedName = CMLib.lang().L("Healing Aura");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Healing Aura)");
    protected int fiveDown = 5;
    protected int tenDown = 10;
    protected int twentyDown = 20;

    @Override
    public String ID() {
        return "Prayer_HealingAura";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HEALING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if ((mob.isInCombat()) && (!mob.charStats().getMyRace().racialCategory().equalsIgnoreCase("Undead")))
                return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return false;
        if (tickID != Tickable.TICKID_MOB)
            return true;
        final MOB myChar = (MOB) affected;
        if (((--fiveDown) > 0) && ((--tenDown) > 0) && ((--twentyDown) > 0))
            return true;

        final Set<MOB> followers = myChar.getGroupMembers(new HashSet<MOB>());
        final Room R = myChar.location();
        if (R != null) {
            final CMFlagLibrary lib = CMLib.flags();
            for (int i = 0; i < R.numInhabitants(); i++) {
                final MOB M = R.fetchInhabitant(i);
                if ((M != null)
                    && ((M.getVictim() == null) || (!followers.contains(M.getVictim())))) {
                    if ((!lib.isUndead(M)) || (myChar.mayIFight(M)))
                        followers.add(M);
                }
            }
        }
        if ((fiveDown) <= 0) {
            fiveDown = 5;
            final Ability A = CMClass.getAbility("Prayer_CureLight");
            if (A != null) {
                for (final MOB M : followers)
                    A.invoke(myChar, M, true, 0);
            }
        }
        if ((tenDown) <= 0) {
            tenDown = 10;
            final Ability A = CMClass.getAbility("Prayer_RemovePoison");
            if (A != null) {
                for (final MOB M : followers)
                    A.invoke(myChar, M, true, 0);
            }
        }
        if ((twentyDown) <= 0) {
            twentyDown = 20;
            final Ability A = CMClass.getAbility("Prayer_CureDisease");
            if (A != null) {
                for (final MOB M : followers)
                    A.invoke(myChar, M, true, 0);
            }
        }
        return true;
    }
}
