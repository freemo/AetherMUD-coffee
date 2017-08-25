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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Prayer_HuntEvil extends Prayer {
    private final static String localizedName = CMLib.lang().L("Hunt Evil");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Hunting Evil)");
    public int nextDirection = -2;
    protected List<Room> theTrail = null;

    @Override
    public String ID() {
        return "Prayer_HuntEvil";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY | Ability.FLAG_TRACKING;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    protected String word() {
        return "evil";
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (tickID == Tickable.TICKID_MOB) {
            if (nextDirection == -999)
                return true;

            if ((theTrail == null)
                || (affected == null)
                || (!(affected instanceof MOB)))
                return false;

            final MOB mob = (MOB) affected;

            if (nextDirection == 999) {
                mob.tell(L("The hunt seems to pause here."));
                nextDirection = -2;
                unInvoke();
            } else if (nextDirection == -1) {
                mob.tell(L("The hunt dries up here."));
                nextDirection = -999;
                unInvoke();
            } else if (nextDirection >= 0) {
                mob.tell(L("The hunt seems to continue @x1.", CMLib.directions().getDirectionName(nextDirection)));
                nextDirection = -2;
            }

        }
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if (!(affected instanceof MOB))
            return;

        final MOB mob = (MOB) affected;
        if ((msg.amISource(mob))
            && (msg.amITarget(mob.location()))
            && (CMLib.flags().canBeSeenBy(mob.location(), mob))
            && (msg.targetMinor() == CMMsg.TYP_LOOK))
            nextDirection = CMLib.tracking().trackNextDirectionFromHere(theTrail, mob.location(), false);
    }

    @Override
    public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats) {
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_TRACK);
        super.affectPhyStats(affectedEnv, affectableStats);
    }

    protected MOB gameHere(Room room) {
        if (room == null)
            return null;
        for (int i = 0; i < room.numInhabitants(); i++) {
            final MOB mob = room.fetchInhabitant(i);
            if (CMLib.flags().isEvil(mob))
                return mob;
        }
        return null;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.fetchEffect(this.ID()) != null) {
            mob.tell(L("You are already trying to hunt @x1.", word()));
            return false;
        }
        final List<Ability> V = CMLib.flags().flaggedAffects(mob, Ability.FLAG_TRACKING);
        for (final Ability A : V) A.unInvoke();

        theTrail = null;
        nextDirection = -2;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if (gameHere(mob.location()) != null) {
            mob.tell(L("Try 'look'."));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);

        final Vector<Room> rooms = new Vector<Room>();
        final TrackingLibrary.TrackingFlags flags = CMLib.tracking().newFlags();
        int range = 50 + super.getXLEVELLevel(mob) + (2 * super.getXMAXRANGELevel(mob));
        final List<Room> checkSet = CMLib.tracking().getRadiantRooms(mob.location(), flags, range);
        for (final Room R : checkSet) {
            if (gameHere(R) != null)
                rooms.addElement(R);
        }

        if (rooms.size() > 0)
            theTrail = CMLib.tracking().findTrailToAnyRoom(mob.location(), rooms, flags, range);

        MOB target = null;
        if ((theTrail != null) && (theTrail.size() > 0))
            target = gameHere(theTrail.get(0));

        if ((success) && (theTrail != null) && (target != null)) {
            theTrail.add(mob.location());
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("^S<S-NAME> @x1 for the trail to @x2.^?", prayWord(mob), word()));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                invoker = mob;
                final Prayer_HuntEvil newOne = (Prayer_HuntEvil) this.copyOf();
                if (mob.fetchEffect(newOne.ID()) == null)
                    mob.addEffect(newOne);
                mob.recoverPhyStats();
                newOne.nextDirection = CMLib.tracking().trackNextDirectionFromHere(newOne.theTrail, mob.location(), false);
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> @x1 for the trail to @x2, but nothing happens.", prayWord(mob), word()));

        // return whether it worked
        return success;
    }
}
