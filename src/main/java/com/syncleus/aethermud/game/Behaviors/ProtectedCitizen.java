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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Libraries.interfaces.TrackingLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.MUDCmdProcessor;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Vector;


public class ProtectedCitizen extends ActiveTicker {
    protected static String zapper = null;
    protected static String defcityguard = "cityguard";
    protected static String[] defclaims = {"Help! I'm being attacked!", "Help me!!"};
    protected String cityguard = null;
    protected String[] claims = null;
    protected int radius = 7;
    protected int maxAssistance = 1;
    protected boolean wander = false;
    public ProtectedCitizen() {
        super();
        minTicks = 1;
        maxTicks = 3;
        chance = 99;
        radius = 7;
        maxAssistance = 1;
        tickReset();
    }

    @Override
    public String ID() {
        return "ProtectedCitizen";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "whiney citizen";
    }

    @Override
    public void setParms(String parms) {
        super.setParms(parms);
        cityguard = null;
        zapper = null;
        wander = parms.toUpperCase().indexOf("WANDER") >= 0;
        radius = CMParms.getParmInt(parms, "radius", radius);
        maxAssistance = CMParms.getParmInt(parms, "maxassists", maxAssistance);
        claims = null;
    }

    public String getCityguardName() {
        if (cityguard != null)
            return "-NAME \"+" + cityguard + "\"";
        if (zapper != null)
            return zapper;
        final String s = getParmsNoTicks();
        if (s.length() == 0) {
            cityguard = defcityguard;
            return "-NAME \"+" + cityguard + "\"";
        }
        char c = ';';
        int x = s.indexOf(c);
        if (x < 0) {
            c = '/';
            x = s.indexOf(c);
        }
        if (x < 0) {
            cityguard = defcityguard;
            return "-NAME \"+" + cityguard + "\"";
        }
        cityguard = s.substring(0, x).trim();
        if (cityguard.length() == 0) {
            cityguard = defcityguard;
            return "-NAME \"+" + cityguard + "\"";
        }
        if ((cityguard.indexOf('+') > 0)
            || (cityguard.indexOf('-') > 0)
            || (cityguard.indexOf('>') > 0)
            || (cityguard.indexOf('<') > 0)
            || (cityguard.indexOf('=') > 0)) {
            zapper = cityguard;
            cityguard = null;
            return zapper;
        }
        return cityguard;
    }

    public String[] getClaims() {
        if (claims != null)
            return claims;
        String s = getParmsNoTicks();
        if (s.length() == 0) {
            claims = defclaims;
            return claims;
        }

        char c = ';';
        int x = s.indexOf(c);
        if (x < 0) {
            c = '/';
            x = s.indexOf(c);
        }
        if (x < 0) {
            claims = defclaims;
            return claims;
        }
        s = s.substring(x + 1).trim();
        if (s.length() == 0) {
            claims = defclaims;
            return claims;
        }
        final Vector<String> V = new Vector<String>();
        x = s.indexOf(c);
        while (x >= 0) {
            final String str = s.substring(0, x).trim();
            s = s.substring(x + 1).trim();
            if (str.length() > 0)
                V.addElement(str);
            x = s.indexOf(c);
        }
        if (s.length() > 0)
            V.addElement(s);
        claims = new String[V.size()];
        for (int i = 0; i < V.size(); i++)
            claims[i] = V.elementAt(i);
        return claims;
    }

    public boolean assistMOB(MOB mob) {
        int assistance = 0;
        for (int i = 0; i < mob.location().numInhabitants(); i++) {
            final MOB M = mob.location().fetchInhabitant(i);
            if ((M != null)
                && (M != mob)
                && (M.getVictim() == mob.getVictim()))
                assistance++;
        }
        if (assistance >= maxAssistance)
            return false;

        final String claim = getClaims()[CMLib.dice().roll(1, getClaims().length, -1)].trim();
        if (claim.startsWith(","))
            mob.doCommand(CMParms.parse("EMOTE \"" + claim.substring(1).trim() + "\""), MUDCmdProcessor.METAFLAG_FORCED);
        else
            mob.doCommand(CMParms.parse("YELL \"" + claim + "\""), MUDCmdProcessor.METAFLAG_FORCED);

        final Room thisRoom = mob.location();
        final Vector<Room> V = new Vector<Room>();
        TrackingLibrary.TrackingFlags flags;
        flags = CMLib.tracking().newFlags()
            .plus(TrackingLibrary.TrackingFlag.OPENONLY);
        if (!wander)
            flags.plus(TrackingLibrary.TrackingFlag.AREAONLY);
        CMLib.tracking().getRadiantRooms(thisRoom, V, flags, null, radius, null);
        for (int v = 0; v < V.size(); v++) {
            final Room R = V.elementAt(v);
            MOB M = null;
            if (R.getArea().Name().equals(mob.location().getArea().Name())) {
                for (int i = 0; i < R.numInhabitants(); i++) {
                    final MOB M2 = R.fetchInhabitant(i);
                    if ((M2 != null)
                        && (M2.mayIFight(mob.getVictim()))
                        && (M2 != mob.getVictim())
                        && (CMLib.flags().isAliveAwakeMobileUnbound(M2, true)
                        && (!M2.isInCombat())
                        && (CMLib.flags().isMobile(M2))
                        && (CMLib.masking().maskCheck(getCityguardName(), M2, false))
                        && (!BrotherHelper.isBrother(mob.getVictim(), M2, false))
                        && (canFreelyBehaveNormal(M2))
                        && (!CMLib.flags().isATrackingMonster(M2))
                        && (CMLib.flags().canHear(M2)))) {
                        M = M2;
                        break;
                    }
                }
            }
            if (M != null) {
                if (R == mob.location())
                    CMLib.combat().postAttack(M, mob.getVictim(), M.fetchWieldedItem());
                else {
                    final int dir = CMLib.tracking().radiatesFromDir(R, V);
                    if (dir >= 0)
                        CMLib.tracking().walk(M, dir, false, false);
                }
            }
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);
        if (canAct(ticking, tickID)) {
            if ((ticking instanceof MOB) && (((MOB) ticking).isInCombat()))
                assistMOB((MOB) ticking);
        }
        return true;
    }
}
