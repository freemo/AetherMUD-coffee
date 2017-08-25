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
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_MysticShine extends Spell {

    private final static String localizedName = CMLib.lang().L("Mystic Shine");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mystic Shine)");

    @Override
    public String ID() {
        return "Spell_MysticShine";
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
        return CAN_ITEMS;
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (!(affected instanceof Room))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_LIGHTSOURCE);
        if (CMLib.flags().isInDark(affected))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_DARK);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        final Room room = CMLib.map().roomLocation(affected);
        if ((canBeUninvoked()) && (room != null))
            room.showHappens(CMMsg.MSG_OK_VISUAL, affected, L("The gleam upon <S-NAME> dims."));
        super.unInvoke();
        if ((canBeUninvoked()) && (room != null))
            room.recoverRoomStats();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_ANY);
        if (target == null) {
            return false;
        }
        if ((!(target instanceof Item))
            || (((((Item) target).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_METAL)
            && ((((Item) target).material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL))) {
            mob.tell(L("This magic only affects metallic items."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        final Room room = mob.location();
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("^S<T-NAME> begin(s) to really shine!") : L("^S<S-NAME> cause(s) the surface of <T-NAME> to mystically shine!^?"));
            if (room.okMessage(mob, msg)) {
                room.send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
                room.recoverRoomStats(); // attempt to handle followers
            }
        } else
            beneficialWordsFizzle(mob, mob.location(), L("<S-NAME> attempt(s) to cause shininess, but fail(s)."));

        return success;
    }
}
