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
package com.planet_ink.game.CharClasses;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.core.CMLib;


public class Evoker extends SpecialistMage {
    private final static String localizedStaticName = CMLib.lang().L("Evoker");

    @Override
    public String ID() {
        return "Evoker";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int domain() {
        return Ability.DOMAIN_EVOCATION;
    }

    @Override
    public int opposed() {
        return Ability.DOMAIN_ALTERATION;
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY;
    }

    @Override
    public void initializeClass() {
        super.initializeClass();
        CMLib.ableMapper().addCharAbilityMapping(ID(), 1, "Spell_ProduceFlame", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 3, "Spell_HelpingHand", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 5, "Spell_PurgeInvisibility", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 7, "Spell_ForcefulHand", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 8, "Skill_Spellcraft", false);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 9, "Spell_ContinualLight", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 11, "Spell_Shockshield", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 13, "Spell_IceLance", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 15, "Spell_Ignite", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 17, "Spell_ForkedLightning", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 19, "Spell_Levitate", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 20, "Spell_Pocket", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 21, "Spell_IceStorm", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 22, "Spell_Shove", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 23, "Spell_Blademouth", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 25, "Spell_LimbRack", 0, "", false, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 27, "Spell_Lighthouse", 25, true);
        CMLib.ableMapper().addCharAbilityMapping(ID(), 30, "Spell_MassDisintegrate", 25, true);
    }
}
