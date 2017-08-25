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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Spell_GhostSound extends Spell {

    private final static String localizedName = CMLib.lang().L("Ghost Sound");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Ghost Sound spell)");

    @Override
    public String ID() {
        return "Spell_GhostSound";
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_MOB)
            && (CMLib.dice().rollPercentage() < 10)
            && (invoker != null)
            && (affected instanceof Room))
            switch (CMLib.dice().roll(1, 14, 0)) {
                case 1:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear something coming up behind you."));
                    break;
                case 2:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear somebody screaming in the distance."));
                    break;
                case 3:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear the snarl of a large ferocious beast."));
                    break;
                case 4:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear complete silence."));
                    break;
                case 5:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("CLANK! Someone just dropped their sword."));
                    break;
                case 6:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear a bird singing."));
                    break;
                case 7:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear a cat dying."));
                    break;
                case 8:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear some people talking."));
                    break;
                case 9:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear singing."));
                    break;
                case 10:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear a cow mooing."));
                    break;
                case 11:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear your shadow."));
                    break;
                case 12:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear someone trying to sneak by you."));
                    break;
                case 13:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear an annoying beeping sound."));
                    break;
                case 14:
                    ((Room) affected).showHappens(CMMsg.MSG_NOISE,
                        L("You hear your heart beating in your chest."));
                    break;
            }
        return super.tick(ticking, tickID);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if ((mob.isMonster()) && (mob.isInCombat()))
                return Ability.QUALITY_INDIFFERENT;
            if (target instanceof MOB) {
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
            mob.tell(mob, null, null, L("There are already ghost sounds here."));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> scream(s) loudly, then fall(s) silent.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, mob.location(), asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> scream(s) loudly, but then feel(s) disappointed."));

        // return whether it worked
        return success;
    }
}
