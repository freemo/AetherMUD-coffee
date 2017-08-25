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
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.LandTitle;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.NoSuchElementException;


public class Spell_KnowOrigin extends Spell {

    private final static String localizedName = CMLib.lang().L("Know Origin");

    @Override
    public String ID() {
        return "Spell_KnowOrigin";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS | Ability.CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    public Room origin(MOB mob, Environmental meThang) {
        if (meThang instanceof LandTitle)
            return ((LandTitle) meThang).getAllTitledRooms().get(0);
        else if (meThang instanceof MOB)
            return ((MOB) meThang).getStartRoom();
        else if (meThang instanceof Item) {
            final Item me = (Item) meThang;
            try {
                // check mobs worn items first!
                final String srchStr = "$" + me.Name() + "$";
                Environmental E = CMLib.map().findFirstShopStocker(CMLib.map().rooms(), mob, srchStr, 10);
                if (E != null)
                    return CMLib.map().getStartRoom(E);
                E = CMLib.map().findFirstInventory(CMLib.map().rooms(), mob, srchStr, 10);
                if (E != null)
                    return CMLib.map().getStartRoom(E);
                return CMLib.map().findWorldRoomLiberally(mob, srchStr, "I", 10, 600000);
            } catch (final NoSuchElementException nse) {
            }
        }
        return null;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Room R = origin(mob, target);
        final boolean success = proficiencyCheck(mob, 0, auto);
        if ((success) && (R != null)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> incant(s), divining the origin of <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.tell(L("@x1 seems to come from '@x2'.", target.name(mob), R.displayText(mob)));
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to divine something, but fail(s)."));

        return success;
    }
}
