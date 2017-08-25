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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Scroll;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Skill_ScrollCopy extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Memorize");
    private static final String[] triggerStrings = I(new String[]{"MEMORIZE"});

    @Override
    public String ID() {
        return "Skill_ScrollCopy";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_CALLIGRAPHY;
    }

    @Override
    protected int overrideMana() {
        return 0;
    } // -1=normal, Ability.COST_ALL=all, Ability.COST_PCT

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        if (commands.size() < 2) {
            mob.tell(L("Memorize what from what?"));
            return false;
        }
        final Item target = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, CMParms.combine(commands, 1));
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", CMParms.combine(commands, 1)));
            return false;
        }

        if (!(target instanceof Scroll)) {
            mob.tell(L("You can't memorize from that."));
            return false;
        }

        if (((Scroll) target).usesRemaining() < 1) {
            mob.tell(L("The scroll appears to be faded."));
            return false;
        }

        final List<Ability> theSpells = ((Scroll) target).getSpells();
        Ability thisSpell = null;
        for (int a = 0; a < theSpells.size(); a++) {
            final Ability A = theSpells.get(a);
            if (CMLib.english().containsString(A.name(), (commands.get(0)))) {
                thisSpell = A;
                break;
            }
        }

        if (thisSpell == null) {
            mob.tell(L("That is not written on @x1.", target.name(mob)));
            return false;
        }

        thisSpell = (Ability) thisSpell.copyOf();
        final MOB T = CMClass.getMOB("Teacher");
        T.setName(target.name());
        T.charStats().setStat(CharStats.STAT_GENDER, 'N');
        T.delAllAbilities();
        thisSpell.setProficiency(50);
        T.addAbility(thisSpell);
        if (!thisSpell.canBeLearnedBy(T, mob))
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (mob.location().show(mob, target, this, CMMsg.MSG_HANDS, L("<S-NAME> memorize(s) '@x1' from <T-NAME>.", thisSpell.name()))) {
                thisSpell.teach(T, mob);
                if ((mob.fetchAbility(thisSpell.ID()) != null)
                    && (CMLib.ableMapper().qualifyingClassLevel(mob, this) >= 0)
                    && (CMLib.ableMapper().qualifyingClassLevel(mob, thisSpell) >= 0)) {
                    final int xp = (int) Math.round(100.0 * CMath.div(CMLib.ableMapper().lowestQualifyingLevel(thisSpell.ID()), CMLib.ableMapper().qualifyingClassLevel(mob, this)));
                    if (xp >= 0)
                        CMLib.leveler().postExperience(mob, null, null, xp, false);
                }
            }
        } else
            mob.location().show(mob, null, CMMsg.MSG_HANDS, L("<S-NAME> attempt(s) to memorize '@x1' from @x2, but fail(s).", thisSpell.name(), target.name()));
        return success;
    }

}
