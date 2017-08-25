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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_SpiderClimb extends Spell {

    private final static String localizedName = CMLib.lang().L("Spider Climb");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Spider Climb)");

    @Override
    public String ID() {
        return "Spell_SpiderClimb";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (affected instanceof MOB) {
            if (CMLib.flags().isStanding((MOB) affected))
                affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_CLIMBING);
        }
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        final Room room = ((MOB) affected).location();
        if ((canBeUninvoked()) && (!mob.amDead()) && (room != null))
            room.show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> no longer <S-HAS-HAVE> a spidery gait."));
        super.unInvoke();
        if (canBeUninvoked() && (room != null))
            room.recoverRoomStats();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> already <S-HAS-HAVE> spidery magic."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("^S<S-NAME> attains a climbers stance!") : L("^S<S-NAME> invoke(s) a spidery spell upon <S-HIM-HERSELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 10);
            }
        } else
            beneficialWordsFizzle(mob, mob.location(), L("<S-NAME> attempt(s) to invoke a spell, but fail(s)."));

        return success;
    }
}
