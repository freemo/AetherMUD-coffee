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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.PlanarAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Planeshift extends PlanarAbility {

    private final static String localizedName = CMLib.lang().L("Planeshift");
    private static final String[] triggerStrings = I(new String[]{"CAST", "CA", "C"});

    @Override
    public String ID() {
        return "Spell_Planeshift";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
    }

    @Override
    public long flags() {
        return 0;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_ALL - 90;
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
    protected String castingMessage(MOB mob, boolean auto) {
        return auto ? L("<S-NAME> <S-IS-ARE> conjured to another plane!") : L("^S<S-NAME> conjur(s) a powerful planar connection!^?");
    }

    @Override
    protected String failMessage(MOB mob, boolean auto) {
        return L("^S<S-NAME> attempt(s) to conjur a powerful planar connection, and fails.");
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!Spell.spellArmorCheck(this, mob, auto))
            return false;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        return true;
    }
}
