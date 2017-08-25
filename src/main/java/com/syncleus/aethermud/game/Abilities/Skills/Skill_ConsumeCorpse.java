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
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Skill_ConsumeCorpse extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Consume Corpse");
    private static final String[] triggerStrings = I(new String[]{"CONSUMECORPSE"});

    public static Item getBody(Room R) {
        if (R != null)
            for (int i = 0; i < R.numItems(); i++) {
                final Item I = R.getItem(i);
                if ((I instanceof DeadBody)
                    && (!((DeadBody) I).isPlayerCorpse())
                    && (((DeadBody) I).getMobName().length() > 0))
                    return I;
            }
        return null;
    }

    @Override
    public String ID() {
        return "Skill_ConsumeCorpse";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_CORRUPTION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (getBody(((MOB) target).location()) != null)
                    return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Item target = null;
        if ((commands.size() == 0) && (!auto) && (givenTarget == null))
            target = getBody(mob.location());
        if ((mob.isMonster()) && (givenTarget instanceof MOB))
            target = getBody(mob.location());
        if (target == null)
            target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if ((!(target instanceof DeadBody))
            || (target.rawSecretIdentity().toUpperCase().indexOf("FAKE") >= 0)) {
            mob.tell(L("You may only consume the dead."));
            return false;
        }

        if ((((DeadBody) target).isPlayerCorpse())
            && (!((DeadBody) target).getMobName().equals(mob.Name()))
            && (((DeadBody) target).hasContent())) {
            mob.tell(L("You are not allowed to consume that corpse."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> consume(s) <T-HIM-HERSELF>.") : L("^S<S-NAME> consume(s) <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                double amtBack = CMath.div(target.phyStats().level(), adjustedLevel(mob, asLevel));
                int hp = (int) Math.round(CMath.mul(mob.maxState().getHitPoints(), amtBack));
                int mana = (int) Math.round(CMath.mul(mob.maxState().getMana(), amtBack));
                int move = (int) Math.round(CMath.mul(mob.maxState().getMovement(), amtBack));
                CMLib.combat().postHealing(mob, mob, this, hp, CMMsg.MASK_ALWAYS | CMMsg.TYP_HANDS, L("<S-NAME> feel(s) better!"));
                mob.curState().adjMana(mana, mob.maxState());
                mob.curState().adjMovement(move, mob.maxState());
                target.destroy();
                mob.location().recoverRoomStats();
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> attempt(s) to consume <T-NAMESELF>, but fail(s)."));

        // return whether it worked
        return success;
    }
}
