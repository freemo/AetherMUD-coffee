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
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_ShapeObject extends Spell {

    private final static String localizedName = CMLib.lang().L("Shape Object");

    @Override
    public String ID() {
        return "Spell_ShapeObject";
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
    public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats) {
        affectableStats.setName(L("@x1 shaped like @x2", affectedEnv.Name(), text()));
    }

    @Override
    public void unInvoke() {
        final Physical affected = super.affected;
        super.unInvoke();
        if (canBeUninvoked() && (affected instanceof Item)) {
            final Item item = (Item) affected;
            if (item.owner() instanceof Room)
                ((Room) item.owner()).showHappens(CMMsg.MSG_OK_VISUAL, item, L("<S-NAME> reverts to its previous shape."));
            else if (item.owner() instanceof MOB)
                ((MOB) item.owner()).tell(((MOB) item.owner()), item, null, L("<T-NAME> reverts to its previous shape."));
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        // add something to disable traps
        //
        if (commands.size() < 2) {
            mob.tell(L("Shape what like what?"));
            return false;
        }
        String itemName = commands.get(0);
        final Item targetI = super.getTarget(mob, null, givenTarget, new XVector<String>(itemName), Wearable.FILTER_UNWORNONLY);
        if (targetI == null) {
            mob.tell(L("You don't seem to have a '@x1'.", itemName));
            return false;
        }
        if (targetI instanceof DeadBody) {
            mob.tell(L("You can't shape that."));
            return false;
        }
        String likeWhat = CMParms.combineQuoted(commands, 1);

        if (CMLib.login().isBadName(likeWhat) || CMProps.isAnyINIFiltered(likeWhat)) {
            mob.tell(L("You can't shape anything like '@x1'.", likeWhat));
            return false;
        }

        if ((targetI.name().toLowerCase().indexOf("shaped like") > 0) || (targetI.fetchEffect(ID()) != null)) {
            mob.tell(mob, targetI, null, L("<T-NAME> is already shaped!"));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, targetI, this, somanticCastCode(mob, targetI, auto), L("^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAME> shaping it like @x1.^?", likeWhat));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);

                if (msg.value() > 0)
                    return false;
                Ability A = super.beneficialAffect(mob, targetI, asLevel, 0);
                if (A != null)
                    A.setMiscText(likeWhat);
                targetI.recoverPhyStats();
            }
        } else {

            return beneficialVisualFizzle(mob, targetI, L("<S-NAME> attempt(s) to shape <T-NAME> like @x1, but flub(s) it.", likeWhat));
        }

        // return whether it worked
        return success;
    }
}
