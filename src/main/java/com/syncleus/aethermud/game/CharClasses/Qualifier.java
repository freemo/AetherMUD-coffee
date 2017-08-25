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
package com.syncleus.aethermud.game.CharClasses;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Libraries.interfaces.AbilityMapper;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.Enumeration;
import java.util.List;


public class Qualifier extends StdCharClass {
    private final static String localizedStaticName = CMLib.lang().L("Qualifier");
    private static boolean abilitiesLoaded = false;

    public Qualifier() {
        super();
        for (final int i : CharStats.CODES.BASECODES())
            maxStatAdj[i] = 7;
    }

    @Override
    public String ID() {
        return "Qualifier";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public String baseClass() {
        return ID();
    }

    public boolean loaded() {
        return abilitiesLoaded;
    }

    public void setLoaded(boolean truefalse) {
        abilitiesLoaded = truefalse;
    }

    @Override
    public int availabilityCode() {
        return 0;
    }

    @Override
    public String getStatQualDesc() {
        return L("Must be granted by an Archon.");
    }

    @Override
    public boolean qualifiesForThisClass(MOB mob, boolean quiet) {
        if (!quiet)
            mob.tell(L("This class cannot be learned."));
        return false;
    }

    @Override
    public void startCharacter(MOB mob, boolean isBorrowedClass, boolean verifyOnly) {
        if (!loaded()) {
            setLoaded(true);
            for (final Enumeration<Ability> a = CMClass.abilities(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                final int lvl = CMLib.ableMapper().lowestQualifyingLevel(A.ID());
                if ((lvl > 0) && (!CMLib.ableMapper().classOnly("Archon", A.ID())))
                    CMLib.ableMapper().addCharAbilityMapping(ID(), lvl, A.ID(), false);
            }
        }
        super.startCharacter(mob, false, verifyOnly);
    }

    @Override
    public void grantAbilities(MOB mob, boolean isBorrowedClass) {
        super.grantAbilities(mob, isBorrowedClass);
        if (mob.playerStats() == null) {
            final List<AbilityMapper.AbilityMapping> V = CMLib.ableMapper().getUpToLevelListings(ID(),
                mob.charStats().getClassLevel(ID()),
                false,
                false);
            for (final AbilityMapper.AbilityMapping able : V) {
                final Ability A = CMClass.getAbility(able.abilityID());
                if ((A != null)
                    && (!CMLib.ableMapper().getAllQualified(ID(), true, A.ID()))
                    && (!CMLib.ableMapper().getDefaultGain(ID(), true, A.ID())))
                    giveMobAbility(mob, A, CMLib.ableMapper().getDefaultProficiency(ID(), true, A.ID()), CMLib.ableMapper().getDefaultParm(ID(), true, A.ID()), isBorrowedClass);
            }
        }
    }

}
