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
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Spell_ReverseGravity extends Spell {

    private final static String localizedName = CMLib.lang().L("Reverse Gravity");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Gravity is Reversed)");
    protected Vector<Ability> childrenAffects = new Vector<Ability>();

    @Override
    public String ID() {
        return "Spell_ReverseGravity";
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
    public long flags() {
        return Ability.FLAG_MOVING;
    }

    @Override
    public int overrideMana() {
        return Ability.COST_PCT + 50;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        else {
            final MOB invoker = this.invoker;
            if ((affected != null) && (affected instanceof Room) && (invoker != null)) {
                final Room room = (Room) affected;
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB inhab = room.fetchInhabitant(i);
                    if (!CMLib.flags().isInFlight(inhab)) {
                        int diff = invoker.phyStats().level() - inhab.phyStats().level();
                        if (diff > 0) // no bonus, only negatives
                            diff = 0;
                        if (CMLib.dice().rollPercentage() > (inhab.charStats().getSave(CharStats.STAT_SAVE_JUSTICE) - diff + inhab.charStats().getStat(CharStats.STAT_STRENGTH))) {
                            inhab.makePeace(true);
                            Ability A = CMClass.getAbility("Falling");
                            A.setAffectedOne(null);
                            A.setMiscText("REVERSED");
                            A.invoke(null, null, inhab, true, 0);
                            A = inhab.fetchEffect("Falling");
                            if (A != null)
                                childrenAffects.addElement(A);
                        } else if (CMLib.flags().isWateryRoom(room))
                            room.showHappens(CMMsg.MSG_OK_VISUAL, inhab, L("<S-NAME> manage(s) to keep a grip on the water around <S-HIM-HER>"));
                        else if (CMLib.flags().isAiryRoom(room))
                            room.showHappens(CMMsg.MSG_OK_VISUAL, inhab, L("<S-NAME> manage(s) to keep a grip on the air around <S-HIM-HER>"));
                        else
                            room.showHappens(CMMsg.MSG_OK_VISUAL, inhab, L("<S-NAME> manage(s) to keep a grip on the ground below <S-HIM-HER>"));
                    }
                }
                for (int i = 0; i < room.numItems(); i++) {
                    final Item inhab = room.getItem(i);
                    if ((inhab != null)
                        && (inhab.container() == null)
                        && (CMLib.flags().isGettable(inhab))
                        && (!CMLib.flags().isInFlight(inhab.ultimateContainer(null)))) {
                        Ability A = CMClass.getAbility("Falling");
                        A.setAffectedOne(room);
                        A.setProficiency(100);
                        A.invoke(null, null, inhab, true, 0);
                        A = inhab.fetchEffect("Falling");
                        if (A != null)
                            childrenAffects.addElement(A);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (affected == null)
            return;
        if (canBeUninvoked()) {
            if (affected instanceof Room) {
                final Room room = (Room) affected;
                room.showHappens(CMMsg.MSG_OK_VISUAL, L("Gravity returns to normal..."));
                if (invoker != null) {
                    final Ability me = invoker.fetchEffect(ID());
                    if (me != null)
                        me.setProficiency(0);
                }
            } else if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if (mob.location() != null) {
                    mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("Gravity returns to normal.."));
                    final Ability me = mob.location().fetchEffect(ID());
                    if (me != null)
                        me.setProficiency(0);
                }
            }
            while (childrenAffects.size() > 0) {
                final Ability A = childrenAffects.elementAt(0);
                A.setProficiency(0);
                childrenAffects.removeElement(A);
            }
        }
        super.unInvoke();
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.location().fetchEffect(this.ID()) != null)
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Physical target = mob.location();

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(mob, null, null, L("Gravity has already been reversed here!"));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L((auto ? "G" : "^S<S-NAME> speak(s) and wave(s) and g") + "ravity begins to reverse!^?"));
            if (mob.location().okMessage(mob, msg)) {
                childrenAffects = new Vector<Ability>();
                mob.location().send(mob, msg);
                beneficialAffect(mob, mob.location(), asLevel, 4 + (super.getXLEVELLevel(mob) / 3));
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> speak(s) in reverse, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
