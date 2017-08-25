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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.MiscMagic;
import com.syncleus.aethermud.game.Items.interfaces.Scroll;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Skill_SongWrite extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Song Write");
    private static final String[] triggerStrings = I(new String[]{"SONGWRITE"});

    @Override
    public String ID() {
        return "Skill_SongWrite";
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
        return CAN_ITEMS;
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
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("Write which song onto what?"));
            return false;
        }
        final Environmental target = mob.location().fetchFromMOBRoomFavorsItems(mob, null, commands.get(commands.size() - 1), Wearable.FILTER_UNWORNONLY);
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            mob.tell(L("You don't see '@x1' here.", (commands.get(commands.size() - 1))));
            return false;
        }
        if ((!(target instanceof Scroll))
            || (!(target instanceof MiscMagic))) {
            mob.tell(L("You can't write music on that."));
            return false;
        }
        if ((mob.curState().getMana() < mob.maxState().getMana()) && (!auto)) {
            mob.tell(L("You need to be at full mana to cast this."));
            return false;
        }

        commands.remove(commands.size() - 1);
        final Scroll scroll = (Scroll) target;

        final String spellName = CMParms.combine(commands, 0).trim();
        Song scrollThis = null;
        for (int a = 0; a < mob.numAbilities(); a++) {
            final Ability A = mob.fetchAbility(a);
            if ((A != null)
                && (A instanceof Song)
                && (A.name().toUpperCase().startsWith(spellName.toUpperCase()))
                && (!A.ID().equals(this.ID())))
                scrollThis = (Song) A;
        }
        if (scrollThis == null) {
            mob.tell(L("You don't know how to write '@x1'.", spellName));
            return false;
        }
        int numSpells = (CMLib.ableMapper().qualifyingClassLevel(mob, this) + (2 * getXLEVELLevel(mob)) - CMLib.ableMapper().qualifyingLevel(mob, this));
        if (numSpells < 0)
            numSpells = 0;
        if (scroll.getSpells().size() > numSpells) {
            mob.tell(L("You aren't powerful enough to write any more magic onto @x1.", scroll.name()));
            return false;
        }

        final List<Ability> spells = scroll.getSpells();
        for (final Ability spell : spells) {
            if (spell.ID().equals(scrollThis.ID())) {
                mob.tell(L("That spell is already written on @x1.", scroll.name()));
                return false;
            }

        }
        // lose all the mana!
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if (!auto)
            mob.curState().setMana(0);

        int experienceToLose = 20 * CMLib.ableMapper().lowestQualifyingLevel(scrollThis.ID());
        experienceToLose = getXPCOSTAdjustment(mob, experienceToLose);
        CMLib.leveler().postExperience(mob, null, null, -experienceToLose, false);
        mob.tell(L("You lose @x1 experience points for the effort.", "" + experienceToLose));

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            setMiscText(scrollThis.ID());
            final CMMsg msg = CMClass.getMsg(mob, target, this, (auto ? CMMsg.MASK_ALWAYS : 0) | CMMsg.MSG_DELICATE_SMALL_HANDS_ACT, L("^S<S-NAME> write(s) music onto <T-NAMESELF>, singing softly.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (scroll.getSpellList().length() == 0)
                    scroll.setSpellList(scrollThis.ID());
                else
                    scroll.setSpellList(scroll.getSpellList() + ";" + scrollThis.ID());
                if ((scroll.usesRemaining() == Integer.MAX_VALUE) || (scroll.usesRemaining() < 0))
                    scroll.setUsesRemaining(0);
                scroll.setUsesRemaining(scroll.usesRemaining() + 1);
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to write music on <T-NAMESELF>, singing softly, and looking very frustrated."));

        // return whether it worked
        return success;
    }
}
