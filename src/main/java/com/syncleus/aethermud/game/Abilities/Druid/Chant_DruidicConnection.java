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
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;


public class Chant_DruidicConnection extends Chant {
    private final static String localizedName = CMLib.lang().L("Druidic Connection");
    protected long lastTime = System.currentTimeMillis();

    @Override
    public String ID() {
        return "Chant_DruidicConnection";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_AREAS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public boolean bubbleAffect() {
        return (affected instanceof Area);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((invoker == null)
            || (!CMLib.flags().isInTheGame(invoker(), true))
            || (invoker().location() == null)
            || ((affected instanceof Area) && (!((Area) affected).inMyMetroArea(invoker().location().getArea())))) {
            unInvoke();
            return false;
        }

        final long ellapsed = System.currentTimeMillis() - lastTime;
        if (affected instanceof Area) {
            final int hoursPerDay = ((Area) affected).getTimeObj().getHoursInDay();
            final long millisPerHoursPerDay = hoursPerDay * CMProps.getMillisPerMudHour();
            if (ellapsed >= millisPerHoursPerDay) {
                lastTime = System.currentTimeMillis();
                final List<Room> V = Druid_MyPlants.myAreaPlantRooms(invoker(), (Area) affected);
                int pct = 0;
                if (((Area) affected).getAreaIStats()[Area.Stats.VISITABLE_ROOMS.ordinal()] > 10)
                    pct = (int) Math.round(100.0 * CMath.div(V.size(), ((Area) affected).getAreaIStats()[Area.Stats.VISITABLE_ROOMS.ordinal()]));
                if (pct < 50) {
                    unInvoke();
                    return false;
                }
                invoker.tell(L("Your prolonged connection to this place fills you with harmony!"));
                final int xp = (int) Math.round(5.0 * CMath.mul(CMath.div(V.size(), ((Area) affected).getAreaIStats()[Area.Stats.VISITABLE_ROOMS.ordinal()])
                    , ((Area) affected).getAreaIStats()[Area.Stats.AVG_LEVEL.ordinal()]));
                CMLib.leveler().postExperience(invoker(), null, null, xp, false);
            }
        }
        return true;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats stats) {
        super.affectPhyStats(affected, stats);
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if ((mob == invoker())
                || CMLib.flags().isAnimalIntelligence(mob)
                || (mob.charStats().getMyRace().racialCategory().equalsIgnoreCase("Vegetation"))) {
                stats.setAttackAdjustment((int) Math.round(CMath.mul(1.1 + (0.05 * getXLEVELLevel(invoker())), stats.attackAdjustment())));
                stats.setArmor(stats.armor() - (2 * stats.level()) - (2 * getXLEVELLevel(invoker())));
            }
        }
    }

    @Override
    public void unInvoke() {
        final MOB invoker = this.invoker;
        final Physical affected = this.affected;
        if ((canBeUninvoked()) && (invoker != null) && (affected instanceof Area)) {
            final List<Room> V = Druid_MyPlants.myAreaPlantRooms(invoker, (Area) affected);
            if (V.size() > 1)
                V.remove(0);
            for (int v = 0; v < V.size(); v++) {
                Item I = Druid_MyPlants.myPlant(V.get(v), invoker, 0);
                int num = 0;
                while (I != null)
                    I = Druid_MyPlants.myPlant(V.get(v), invoker, ++num);
                for (int x = num - 1; x >= 0; x--) {
                    I = Druid_MyPlants.myPlant(V.get(v), invoker, x);
                    if (I != null)
                        I.destroy();
                }
            }
            invoker.tell(L("You have destroyed your connection with @x1!", affected.name()));
            for (final Enumeration<Room> e = ((Area) affected).getMetroMap(); e.hasMoreElements(); ) {
                final Room R = e.nextElement();
                if (R != null)
                    R.recoverRoomStats();
            }
        }
        super.unInvoke();
        if (invoker != null) {
            invoker.delEffect(this);
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((mob.location() == null))
            return false;
        Area target = mob.location().getArea();
        if ((auto) && (givenTarget instanceof Area))
            target = (Area) givenTarget;
        if (target == null)
            return false;
        final boolean quietly = ((commands != null) && (commands.size() > 0) && (commands.contains("QUIETLY")));
        if (target.fetchEffect(ID()) != null) {
            if (!quietly)
                mob.tell(L("This place is already connected to a druid."));
            return false;
        }
        final List<Room> V = Druid_MyPlants.myAreaPlantRooms(mob, target);
        int pct = 0;
        if (target.getAreaIStats()[Area.Stats.VISITABLE_ROOMS.ordinal()] > 10)
            pct = (int) Math.round(100.0 * CMath.div(V.size(), target.getAreaIStats()[Area.Stats.VISITABLE_ROOMS.ordinal()]));
        if (pct < 50) {
            if (!quietly)
                mob.tell(L("You'll need to summon more of your special plant-life here to develop the connection."));
            return false;
        }
        if ((!auto) && (!mob.charStats().getCurrentClass().baseClass().equalsIgnoreCase("Druid"))) {
            if (!quietly)
                mob.tell(L("Only druids can make this connection."));
            return false;
        }

        if (CMLib.law().isACity(target)
            && (!auto)) {
            if (!quietly)
                mob.tell(L("This chant does not work here."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("This area seems connected to <S-NAME>.") : L("^S<S-NAME> chant(s), establishing a natural connection with this area.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
                final Chant_DruidicConnection A = (Chant_DruidicConnection) target.fetchEffect(ID());
                if (A != null) {
                    A.setSavable(false);
                    A.makeLongLasting();
                    A.lastTime = System.currentTimeMillis();
                    mob.addEffect(A);
                    A.setAffectedOne(target);
                    for (final Enumeration<Room> e = target.getMetroMap(); e.hasMoreElements(); )
                        e.nextElement().recoverRoomStats();
                }
            } else
                success = false;
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s), but the magic fades."));

        // return whether it worked
        return success;
    }
}
