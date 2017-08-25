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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Libraries.interfaces.TrackingLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Chant_FindPlant extends Chant {
    private final static String localizedName = CMLib.lang().L("Find Plant");
    private final int[] myMats = {RawMaterial.MATERIAL_VEGETATION,
        RawMaterial.MATERIAL_WOODEN};
    private final int[] myRscs = {RawMaterial.RESOURCE_COTTON,
        RawMaterial.RESOURCE_HEMP};
    public int whatImLookingFor = -1;
    protected String lookingFor = "plants";
    protected List<Room> theTrail = null;
    protected int nextDirection = -2;
    protected Vector<Integer> allResources = null;

    @Override
    public String ID() {
        return "Chant_FindPlant";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
    }

    @Override
    public String displayText() {
        return L("(Finding " + lookingFor + ")");
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_TRACKING;
    }

    protected int[] okMaterials() {
        return myMats;
    }

    protected int[] okResources() {
        return myRscs;
    }

    protected Vector<Integer> allOkResources() {
        if (allResources == null) {
            allResources = new Vector<Integer>();
            if (okResources() != null) {
                for (int m = 0; m < okResources().length; m++) {
                    if (!allResources.contains(Integer.valueOf(okResources()[m])))
                        allResources.addElement(Integer.valueOf(okResources()[m]));
                }
            }
            for (final int cd : RawMaterial.CODES.ALL()) {
                if (okMaterials() != null) {
                    for (int m = 0; m < okMaterials().length; m++) {
                        if ((cd & RawMaterial.MATERIAL_MASK) == okMaterials()[m]) {
                            if (!allResources.contains(Integer.valueOf(cd)))
                                allResources.addElement(Integer.valueOf(cd));
                        }
                    }
                }
            }
        }
        return allResources;
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
                mob.tell(itsHere(mob, mob.location()));
                nextDirection = -2;
                unInvoke();
            } else if (nextDirection == -1) {
                if (itsHere(mob, mob.location()).length() == 0)
                    mob.tell(L("The trail fizzles out here."));
                nextDirection = -999;
                unInvoke();
            } else if (nextDirection >= 0) {
                mob.tell(L("You sense @x1 @x2.", lookingFor, CMLib.directions().getInDirectionName(nextDirection)));
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

    public String itsHere(MOB mob, Room R) {
        if (R == null)
            return "";
        final Room room = R;
        if (room.myResource() == whatImLookingFor)
            return "There seems to be " + lookingFor + " around here.\n\r";
        return "";
    }

    protected boolean findWhatImLookingFor(MOB mob, String s) {
        whatImLookingFor = -1;
        for (int i = 0; i < allOkResources().size(); i++) {
            final int c = allOkResources().elementAt(i).intValue();
            final String d = RawMaterial.CODES.NAME(c);
            if (d.equalsIgnoreCase(s)) {
                lookingFor = d.toLowerCase();
                whatImLookingFor = c;
                break;
            }
        }
        if (whatImLookingFor < 0) {
            mob.tell(L("'@x1' cannot be found with this chant.    Use 'CHANT \"@x2\" LIST' for a list.", s, name()));
            return false;
        }
        return true;
    }

    protected TrackingLibrary.TrackingFlags getTrackingFlags() {
        TrackingLibrary.TrackingFlags flags;
        flags = CMLib.tracking().newFlags()
            .plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
            .plus(TrackingLibrary.TrackingFlag.NOAIR)
            .plus(TrackingLibrary.TrackingFlag.NOWATER);
        return flags;
    }

    @Override
    public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats) {
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_TRACK);
        super.affectPhyStats(affectedEnv, affectableStats);
    }

    protected List<Room> makeTheTrail(MOB mob, MOB target, Room mobRoom) {
        final Vector<Room> rooms = new Vector<Room>();
        TrackingLibrary.TrackingFlags flags = getTrackingFlags();
        int range = 50 + (2 * super.getXLEVELLevel(mob)) + (10 * super.getXMAXRANGELevel(mob));
        final List<Room> checkSet = CMLib.tracking().getRadiantRooms(mobRoom, flags, range);
        for (final Room R : checkSet) {
            if (itsHere(target, R).length() > 0)
                rooms.addElement(R);
        }

        flags = getTrackingFlags();
        if (rooms.size() > 0)
            theTrail = CMLib.tracking().findTrailToAnyRoom(mobRoom, rooms, flags, range);
        return theTrail;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already trying to @x1", name()));
            return false;
        }
        final List<Ability> V = CMLib.flags().flaggedAffects(mob, Ability.FLAG_TRACKING);
        for (final Ability A : V)
            A.unInvoke();

        if ((commands.size() == 0) && (text().length() > 0))
            commands.add(text());
        if (commands.size() == 0) {
            mob.tell(L("Find which @x1?  Use 'CHANT \"@x2\" LIST' for a list.", lookingFor, name()));
            return false;
        }
        final String s = CMParms.combine(commands, 0);
        if (s.equalsIgnoreCase("LIST")) {
            final StringBuffer msg = new StringBuffer(L("You may search for any of the following: "));
            for (int i = 0; i < allOkResources().size(); i++)
                msg.append(RawMaterial.CODES.NAME(allOkResources().elementAt(i).intValue()).toLowerCase() + ", ");
            mob.tell(msg.substring(0, msg.length() - 2));
            return false;
        }
        if (!findWhatImLookingFor(mob, s)) {
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final String here = itsHere(target, target.location());
        if (here.length() > 0) {
            target.tell(here);
            return true;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);

        theTrail = makeTheTrail(mob, target, mob.location());

        if ((success) && (theTrail != null)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> begin(s) to @x1s!", name().toLowerCase()) : L("^S<S-NAME> chant(s) for @x1.^?", lookingFor));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Chant_FindPlant newOne = (Chant_FindPlant) this.copyOf();
                if (target.fetchEffect(newOne.ID()) == null)
                    target.addEffect(newOne);
                target.recoverPhyStats();
                newOne.nextDirection = CMLib.tracking().trackNextDirectionFromHere(newOne.theTrail, target.location(), false);
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> chant(s), but gain(s) nothing from it."));

        return success;
    }
}
