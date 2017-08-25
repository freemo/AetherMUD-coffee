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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.Faction;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Prayer extends StdAbility {
    private final static String localizedName = CMLib.lang().L("a Prayer");
    private static final String[] triggerStrings = I(new String[]{"PRAY", "PR"});

    protected static boolean prayerAlignmentCheck(StdAbility A, MOB mob, boolean auto) {
        if ((!auto)
            && (!mob.isMonster())
            && (!A.disregardsArmorCheck(mob))
            && (mob.isMine(A))
            && (!A.appropriateToMyFactions(mob))) {
            int hq = 500;
            if (CMath.bset(A.flags(), Ability.FLAG_HOLY)) {
                if (!CMath.bset(A.flags(), Ability.FLAG_UNHOLY))
                    hq = 1000;
            } else if (CMath.bset(A.flags(), Ability.FLAG_UNHOLY))
                hq = 0;

            int basis = 0;
            if (hq == 0)
                basis = CMLib.factions().getAlignPurity(mob.fetchFaction(CMLib.factions().AlignID()), Faction.Align.EVIL);
            else if (hq == 1000)
                basis = CMLib.factions().getAlignPurity(mob.fetchFaction(CMLib.factions().AlignID()), Faction.Align.GOOD);
            else {
                basis = CMLib.factions().getAlignPurity(mob.fetchFaction(CMLib.factions().AlignID()), Faction.Align.NEUTRAL);
                basis -= 10;
            }

            if (CMLib.dice().rollPercentage() > basis)
                return true;

            if (hq == 0)
                mob.tell(A.L("The evil nature of @x1 disrupts your prayer.", A.name()));
            else if (hq == 1000)
                mob.tell(A.L("The goodness of @x1 disrupts your prayer.", A.name()));
            else if (CMLib.flags().isGood(mob))
                mob.tell(A.L("The anti-good nature of @x1 disrupts your thought.", A.name()));
            else if (CMLib.flags().isEvil(mob))
                mob.tell(A.L("The anti-evil nature of @x1 disrupts your thought.", A.name()));
            return false;
        }
        return true;
    }

    @Override
    public String ID() {
        return "Prayer";
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
        return 0;
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
        return Ability.ACODE_PRAYER;
    }

    protected String prayWord(MOB mob) {
        if (mob.getMyDeity() != null)
            return "pray(s) to " + mob.getMyDeity().name();
        return "pray(s)";
    }

    protected String prayForWord(MOB mob) {
        if (mob.getMyDeity() != null)
            return "pray(s) for " + mob.getMyDeity().name();
        return "pray(s)";
    }

    protected String inTheNameOf(MOB mob) {
        if (mob.getMyDeity() != null)
            return " in the name of " + mob.getMyDeity().name();
        return "";
    }

    protected String againstTheGods(MOB mob) {
        if (mob.getMyDeity() != null)
            return " against " + mob.getMyDeity().name();
        return " against the gods";
    }

    protected String hisHerDiety(MOB mob) {
        if (mob.getMyDeity() != null)
            return mob.getMyDeity().name();
        return "<S-HIS-HER> god";
    }

    protected String ofDiety(MOB mob) {
        if (mob.getMyDeity() != null)
            return " of " + mob.getMyDeity().name();
        return "";
    }

    protected String prayingWord(MOB mob) {
        if (mob.getMyDeity() != null)
            return "praying to " + mob.getMyDeity().name();
        return "praying";
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical target, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, target, auto, asLevel))
            return false;
        if (!prayerAlignmentCheck(this, mob, auto))
            return false;
        return true;
    }

}
