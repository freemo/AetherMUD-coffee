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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Enlarge extends Spell {

    private final static String localizedName = CMLib.lang().L("Enlarge Object");
    private static final String addOnString = " of ENORMOUS SIZE!!!";

    @Override
    public String ID() {
        return "Spell_Enlarge";
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setWeight(affectableStats.weight() + 9999);
        affectableStats.setHeight(affectableStats.height() + 9999);
        affectableStats.setName(affected.name() + addOnString);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if ((canBeUninvoked()) && (affected instanceof Item) && (CMLib.flags().isInTheGame((Item) affected, true))) {
            final Item I = (Item) affected;
            if (I.owner() instanceof MOB)
                ((MOB) I.owner()).tell(L("@x1 in your inventory shrinks back to size.", I.name((MOB) I.owner())));
            else {
                final Room R = CMLib.map().roomLocation(I);
                if (R != null)
                    R.showHappens(CMMsg.MSG_OK_VISUAL, L("@x1 shrinks back to normal size.", I.name()));
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if (mob.isMine(target)) {
            mob.tell(L("You'd better put it down first."));
            return false;
        }
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(L("@x1 is already HUGE!", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Ability A = target.fetchEffect("Spell_Shrink");
                if ((A != null) && (A.canBeUninvoked()))
                    A.unInvoke();
                else {
                    mob.location().show(mob, target, CMMsg.MSG_OK_ACTION, L("<T-NAME> grow(s) to an enormous size!"));
                    beneficialAffect(mob, target, asLevel, 100);
                }
            }

        } else
            beneficialVisualFizzle(mob, target, L("<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>, incanting but nothing happens."));

        // return whether it worked
        return success;
    }
}
