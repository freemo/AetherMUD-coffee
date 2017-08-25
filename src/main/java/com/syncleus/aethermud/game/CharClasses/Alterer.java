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
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.core.CMLib;


public class Alterer extends SpecialistMage {
    private final static String localizedStaticName = CMLib.lang().L("Alterer");

    @Override
    public String ID() {
        return "Alterer";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int domain() {
        return Ability.DOMAIN_ALTERATION;
    }

    @Override
    public int opposed() {
        return Ability.DOMAIN_EVOCATION;
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY;
    }

    @Override
    public void initializeClass() {
        super.initializeClass();
        CMLib.ableMapper().delCharAbilityMapping(ID(), "Spell_Shield");

        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Spell_MagicBullets", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Spell_ShapeObject", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 4, "Spell_FlamingArrows", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 6, "Spell_KeenEdge", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Skill_Spellcraft", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 10, "Spell_MassFeatherfall", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Spell_IncreaseGravity", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Spell_AlterSubstance", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Spell_SlowProjectiles", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Spell_MassSlow", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Spell_Timeport", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Spell_PolymorphObject", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Spell_GravitySlam", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Spell_Fabricate", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Spell_Duplicate", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Spell_Wish", 25, true);
    }
}
