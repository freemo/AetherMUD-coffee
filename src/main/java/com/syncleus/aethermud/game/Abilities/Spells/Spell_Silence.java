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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Silence extends Spell {

    private final static String localizedName = CMLib.lang().L("Silence");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Silence spell)");
    public Room theRoom = null;

    @Override
    public String ID() {
        return "Spell_Silence";
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
    protected int canAffectCode() {
        return CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (affected == null)
            return;
        if (!(affected instanceof Room))
            return;
        final Room room = (Room) affected;
        if (canBeUninvoked())
            room.showHappens(CMMsg.MSG_OK_ACTION, L("The sounds here begin to return."));
        super.unInvoke();
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((affected instanceof MOB) || (affected instanceof Item)) {
            final Room R = CMLib.map().roomLocation(affected);
            if ((R != null) && (R == theRoom) && (!unInvoked) && (R.fetchEffect(ID()) == this)) {
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK);
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_HEAR);
            } else {
                affected.delEffect(this);
                affected.recoverPhyStats();
            }
        } else if ((affected instanceof Room) && (!unInvoked)) {
            final Room R = (Room) affected;
            theRoom = R;
            MOB M = null;
            for (int i = 0; i < R.numInhabitants(); i++) {
                M = R.fetchInhabitant(i);
                if ((M != null) && (M.fetchEffect(ID()) == null)) {
                    M.addEffect(this);
                    setAffectedOne(R);
                }
            }
        }

    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (!CMLib.flags().canSpeak((MOB) target))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Physical target = mob.location();

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(mob, null, null, L("This place is already silent."));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), L((auto ? "S" : "^S<S-NAME> whisper(s) and gesture(s) and s") + "ilence falls like a blanket.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, mob.location(), asLevel, 0);
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> whisper(s) about silence, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
