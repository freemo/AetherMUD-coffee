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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Wand;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Spell_EnchantWand extends Spell {

    private final static String localizedName = CMLib.lang().L("Enchant Wand");

    @Override
    public String ID() {
        return "Spell_EnchantWand";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NOORDERING;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_ALL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("Enchant which spell onto what?"));
            return false;
        }
        final Physical target = mob.location().fetchFromMOBRoomFavorsItems(mob, null, commands.get(commands.size() - 1), Wearable.FILTER_UNWORNONLY);
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", (commands.get(commands.size() - 1))));
            return false;
        }
        if (!(target instanceof Wand)) {
            mob.tell(mob, target, null, L("You can't enchant <T-NAME>."));
            return false;
        }

        commands.remove(commands.size() - 1);
        final Wand wand = (Wand) target;

        final String spellName = CMParms.combine(commands, 0).trim();
        Spell wandThis = null;
        for (final Enumeration<Ability> a = mob.allAbilities(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null)
                && (A instanceof Spell)
                && ((!A.isSavable()) || (CMLib.ableMapper().qualifiesByLevel(mob, A)))
                && (A.name().equalsIgnoreCase(spellName))
                && (!A.ID().equals(this.ID())))
                wandThis = (Spell) A;
        }
        if (wandThis == null) {
            for (final Enumeration<Ability> a = mob.allAbilities(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null)
                    && (A instanceof Spell)
                    && ((!A.isSavable()) || (CMLib.ableMapper().qualifiesByLevel(mob, A)))
                    && (CMLib.english().containsString(A.name(), spellName))
                    && (!A.ID().equals(this.ID())))
                    wandThis = (Spell) A;
            }
        }
        if (wandThis == null) {
            mob.tell(L("You don't know how to enchant anything with '@x1'.", spellName));
            return false;
        }

        if ((CMLib.ableMapper().lowestQualifyingLevel(wandThis.ID()) > 24)
            || (((StdAbility) wandThis).usageCost(null, true)[0] > 45)) {
            mob.tell(L("That spell is too powerful to enchant into wands."));
            return false;
        }

        if (wand.getSpell() != null) {
            mob.tell(L("A spell has already been enchanted into '@x1'.", wand.name()));
            return false;
        }

        int experienceToLose = 10 * CMLib.ableMapper().lowestQualifyingLevel(wandThis.ID());
        if ((mob.getExperience() - experienceToLose) < 0) {
            mob.tell(L("You don't have enough experience to cast this spell."));
            return false;
        }
        // lose all the mana!
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        experienceToLose = getXPCOSTAdjustment(mob, experienceToLose);
        CMLib.leveler().postExperience(mob, null, null, -experienceToLose, false);
        mob.tell(L("You lose @x1 experience points for the effort.", "" + experienceToLose));

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            setMiscText(wandThis.ID());
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("^S<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                wand.setSpell((Ability) wandThis.copyOf());
                if ((wand.usesRemaining() == Integer.MAX_VALUE) || (wand.usesRemaining() < 0))
                    wand.setUsesRemaining(0);
                wand.basePhyStats().setLevel(wandThis.adjustedLevel(mob, asLevel));
                wand.setUsesRemaining(wand.usesRemaining() + 5);
                wand.text();
                wand.recoverPhyStats();
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly, and looking very frustrated."));

        // return whether it worked
        return success;
    }
}
