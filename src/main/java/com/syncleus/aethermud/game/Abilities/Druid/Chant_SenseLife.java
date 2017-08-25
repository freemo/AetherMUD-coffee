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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.Directions;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Chant_SenseLife extends Chant {
    private final static String localizedName = CMLib.lang().L("Life Echoes");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Life Echoes)");
    protected Room lastRoom = null;

    @Override
    public String ID() {
        return "Chant_SenseLife";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked()) {
            lastRoom = null;
            mob.tell(L("Your life echo sensations fade."));
        }
    }

    public boolean inhabitated(MOB mob, Room R) {
        if (R == null)
            return false;
        for (int i = 0; i < R.numInhabitants(); i++) {
            final MOB M = R.fetchInhabitant(i);
            if ((M != null)
                && (!CMLib.flags().isGolem(M))
                && (M.charStats().getMyRace().canBreedWith(M.charStats().getMyRace()))
                && (M != mob))
                return true;
        }
        return false;
    }

    public void messageTo(MOB mob) {
        String last = "";
        String dirs = "";
        for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
            final Room R = mob.location().getRoomInDir(d);
            final Exit E = mob.location().getExitInDir(d);
            if ((R != null) && (E != null) && (inhabitated(mob, R))) {
                if (last.length() > 0)
                    dirs += ", " + last;
                last = CMLib.directions().getFromCompassDirectionName(d);
            }
        }
        if (inhabitated(mob, mob.location())) {
            if (last.length() > 0)
                dirs += ", " + last;
            last = "here";
        }

        if ((dirs.length() == 0) && (last.length() == 0))
            mob.tell(L("You do not feel any life beyond your own."));
        else if (dirs.length() == 0)
            mob.tell(L("You feel a life force coming from @x1.", last));
        else
            mob.tell(L("You feel a life force coming from @x1, and @x2.", dirs.substring(2), last));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((tickID == Tickable.TICKID_MOB)
            && (affected instanceof MOB)
            && (((MOB) affected).location() != null)
            && ((lastRoom == null) || (((MOB) affected).location() != lastRoom))) {
            lastRoom = ((MOB) affected).location();
            messageTo((MOB) affected);
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already sensing life echoes."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> gain(s) life-senses!") : L("^S<S-NAME> chant(s) softly, and then stop(s) to listen.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) softly, but nothing happens."));

        // return whether it worked
        return success;
    }
}
