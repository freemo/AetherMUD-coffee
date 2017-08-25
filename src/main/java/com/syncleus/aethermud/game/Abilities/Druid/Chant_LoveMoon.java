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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.MUDCmdProcessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class Chant_LoveMoon extends Chant {
    private final static String localizedName = CMLib.lang().L("Love Moon");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Love Moon)");

    @Override
    public String ID() {
        return "Chant_LoveMoon";
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
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS | CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONALTERING;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB)) {
            if (affected instanceof Room)
                ((Room) affected).showHappens(CMMsg.MSG_OK_VISUAL, L("The love moon sets."));
            super.unInvoke();
            return;
        }

        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("You are no longer under the love moon."));

        super.unInvoke();

    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected == null)
            return false;
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (mob.location().fetchEffect(ID()) == null)
                unInvoke();
            else {
                final Vector<MOB> choices = new Vector<MOB>();
                for (int i = 0; i < mob.location().numInhabitants(); i++) {
                    final MOB M = mob.location().fetchInhabitant(i);
                    if ((M != null)
                        && (M != mob)
                        && (CMLib.flags().canBeSeenBy(M, mob))
                        && (M.charStats().getStat(CharStats.STAT_GENDER) != mob.charStats().getStat(CharStats.STAT_GENDER))
                        && (M.charStats().getStat(CharStats.STAT_GENDER) != 'N')
                        && (M.charStats().getSave(CharStats.STAT_CHARISMA) > 14))
                        choices.addElement(M);
                }
                if (choices.size() > 0) {
                    final MOB M = choices.elementAt(CMLib.dice().roll(1, choices.size(), -1));
                    if (CMLib.dice().rollPercentage() == 1) {
                        Item I = mob.fetchFirstWornItem(Wearable.WORN_WAIST);
                        if (I != null)
                            CMLib.commands().postRemove(mob, I, false);
                        I = mob.fetchFirstWornItem(Wearable.WORN_LEGS);
                        if (I != null)
                            CMLib.commands().postRemove(mob, I, false);
                        mob.doCommand(CMParms.parse("MATE " + M.Name()), MUDCmdProcessor.METAFLAG_FORCED);
                    } else if (CMLib.dice().rollPercentage() > 10)
                        switch (CMLib.dice().roll(1, 5, 0)) {
                            case 1:
                                mob.tell(L("You feel strange urgings towards @x1.", M.name(mob)));
                                break;
                            case 2:
                                mob.tell(L("You have strong happy feelings towards @x1.", M.name(mob)));
                                break;
                            case 3:
                                mob.tell(L("You feel very appreciative of @x1.", M.name(mob)));
                                break;
                            case 4:
                                mob.tell(L("You feel very close to @x1.", M.name(mob)));
                                break;
                            case 5:
                                mob.tell(L("You feel lovingly towards @x1.", M.name(mob)));
                                break;
                        }
                }
            }
        } else if (affected instanceof Room) {
            final Room room = (Room) affected;
            if (!room.getArea().getClimateObj().canSeeTheMoon(room, this))
                unInvoke();
            else
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB M = room.fetchInhabitant(i);
                    if ((M != null) && (M.fetchEffect(ID()) == null)) {
                        final Ability A = (Ability) copyOf();
                        M.addEffect(A);
                        M.recoverCharStats();
                    }
                }
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 6);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;
        if (!target.getArea().getClimateObj().canSeeTheMoon(target, null)) {
            mob.tell(L("You must be able to see the moon for this magic to work."));
            return false;
        }
        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("This place is already under the love moon."));
            return false;
        }
        for (final Enumeration<Ability> a = target.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null)
                && ((A.classificationCode() & Ability.ALL_DOMAINS) == Ability.DOMAIN_MOONALTERING)) {
                mob.tell(L("The moon is already under @x1, and can not be changed until this magic is gone.", A.name()));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to the sky.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The Love Moon Rises!"));
                    beneficialAffect(mob, target, asLevel, 0);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to the sky, but the magic fades."));
        // return whether it worked
        return success;
    }
}
