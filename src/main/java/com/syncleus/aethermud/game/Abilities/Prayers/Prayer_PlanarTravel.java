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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.PlanarAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_PlanarTravel extends PlanarAbility {
    private final static String localizedName = CMLib.lang().L("Planar Travel");
    private static final String[] triggerStrings = I(new String[]{"PRAY", "PR"});

    @Override
    public String ID() {
        return "Prayer_PlanarTravel";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CONJURATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY | Ability.FLAG_HOLY;
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

    protected String prayWord(MOB mob) {
        if (mob.getMyDeity() != null)
            return "pray(s) to " + mob.getMyDeity().name();
        return "pray(s)";
    }

    @Override
    protected String castingMessage(MOB mob, boolean auto) {
        return auto ? L("<S-NAME> gain(s) access to other planes of existence!") : L("^S<S-NAME> @x1 for access to other planes of existence!^?", prayWord(mob));
    }

    @Override
    protected String failMessage(MOB mob, boolean auto) {
        return L("<S-NAME> @x1 for planar travel, but nothing happens.", prayWord(mob));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!Prayer.prayerAlignmentCheck(this, mob, auto))
            return false;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        return true;
    }
}
