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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Libraries.interfaces.MaskingLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.MUDCmdProcessor;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class Aggressive extends StdBehavior {
    protected int tickWait = 0;
    protected int tickDown = 0;
    protected boolean wander = false;
    protected boolean mobkill = false;
    protected boolean misbehave = false;
    protected boolean levelcheck = false;
    protected String attackMessage = null;
    protected Room lastRoom = null;
    protected int lastRoomInhabCount = 0;
    protected MaskingLibrary.CompiledZMask mask = null;

    public static boolean startFight(MOB monster, MOB mob, boolean fightMOBs, boolean misBehave, String attackMsg) {
        if ((mob != null) && (monster != null) && (mob != monster)) {
            final Room R = monster.location();
            if ((R != null)
                && ((!mob.isMonster()) || (fightMOBs))
                && (R.isInhabitant(mob))
                && (R.getArea().getAreaState() == Area.State.ACTIVE)
                && ((misBehave && (!monster.isInCombat())) || canFreelyBehaveNormal(monster))
                && (CMLib.flags().canBeSeenBy(mob, monster))
                && (!CMSecurity.isAllowed(mob, R, CMSecurity.SecFlag.ORDER))
                && (!CMSecurity.isAllowed(mob, R, CMSecurity.SecFlag.CMDROOMS))
                && (!CMLib.flags().isATrackingMonster(mob))
                && (!CMLib.flags().isATrackingMonster(monster))
                && (!monster.getGroupMembers(new HashSet<MOB>()).contains(mob))) {
                // special backstab sneak attack!
                if (CMLib.flags().isHidden(monster)) {
                    final Ability A = monster.fetchAbility("Thief_BackStab");
                    if (A != null) {
                        A.setProficiency(CMLib.dice().roll(1, 50, A.adjustedLevel(mob, 0) * 15));
                        monster.enqueCommand(new XVector<String>(A.triggerStrings()[0], R.getContextName(mob)), MUDCmdProcessor.METAFLAG_FORCED, 0);
                    }
                }
                if ((attackMsg != null) && (monster.getVictim() != mob))
                    monster.enqueCommand(new XVector<String>("SAY", attackMsg), MUDCmdProcessor.METAFLAG_FORCED, 0);
                // normal attack
                monster.enqueCommand(new XVector<String>("KILL", R.getContextName(mob)), MUDCmdProcessor.METAFLAG_FORCED, 0);
                return true;
            }
        }
        return false;
    }

    @Override
    public String ID() {
        return "Aggressive";
    }

    @Override
    public long flags() {
        return Behavior.FLAG_POTENTIALLYAGGRESSIVE | Behavior.FLAG_TROUBLEMAKING;
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        if ((msg.sourceMinor() == CMMsg.TYP_ENTER) || (msg.sourceMinor() == CMMsg.TYP_LEAVE))
            lastRoomInhabCount = -1;
    }

    @Override
    public boolean grantsAggressivenessTo(MOB M) {
        if (M == null)
            return true;
        return CMLib.masking().maskCheck(getParms(), M, false);
    }

    @Override
    public String accountForYourself() {
        if (getParms().trim().length() > 0)
            return "aggression against " + CMLib.masking().maskDesc(getParms(), true).toLowerCase();
        else
            return "aggressiveness";
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        tickWait = CMParms.getParmInt(newParms, "delay", 0);
        attackMessage = CMParms.getParmStr(newParms, "MESSAGE", null);
        final Vector<String> V = CMParms.parse(newParms.toUpperCase());
        wander = V.contains("WANDER");
        levelcheck = V.contains("CHECKLEVEL");
        mobkill = V.contains("MOBKILL") || (V.contains("MOBKILLER"));
        misbehave = V.contains("MISBEHAVE");
        tickDown = tickWait;
        this.mask = CMLib.masking().getPreCompiledMask(newParms);
    }

    public boolean pickAFight(MOB observer, MaskingLibrary.CompiledZMask mask, boolean mobKiller, boolean misBehave, boolean levelCheck, String attackMsg) {
        if (!canFreelyBehaveNormal(observer))
            return false;
        final Room R = observer.location();
        if ((R != null) && (R.getArea().getAreaState() == Area.State.ACTIVE)) {
            if ((R != lastRoom) || (lastRoomInhabCount != R.numInhabitants())) {
                lastRoom = R;
                lastRoomInhabCount = R.numInhabitants();
                final Set<MOB> groupMembers = observer.getGroupMembers(new HashSet<MOB>());
                for (int i = 0; i < R.numInhabitants(); i++) {
                    final MOB mob = R.fetchInhabitant(i);
                    if ((mob != null)
                        && (mob != observer)
                        && ((!levelCheck) || (observer.phyStats().level() < (mob.phyStats().level() + 5)))
                        && ((!mob.isMonster()) || (mobKiller))
                        && (CMLib.masking().maskCheck(mask, mob, false))
                        && (!groupMembers.contains(mob))
                        && (startFight(observer, mob, mobKiller, misBehave, attackMsg)))
                        return true;
                }
            }
        }
        return false;
    }

    public void tickAggressively(Tickable ticking, int tickID, boolean mobKiller, boolean misBehave, boolean levelCheck, MaskingLibrary.CompiledZMask mask, String attackMsg) {
        if (tickID != Tickable.TICKID_MOB)
            return;
        if (ticking == null)
            return;
        if (!(ticking instanceof MOB))
            return;
        pickAFight((MOB) ticking, mask, mobKiller, misBehave, levelCheck, attackMsg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);
        if (tickID != Tickable.TICKID_MOB)
            return true;
        if ((--tickDown) < 0) {
            tickDown = tickWait;
            tickAggressively(ticking,
                tickID,
                mobkill,
                misbehave,
                this.levelcheck,
                this.mask,
                attackMessage);
        }
        return true;
    }
}
