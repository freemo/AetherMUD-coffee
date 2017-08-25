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
package com.planet_ink.game.Abilities.Paladin;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.collections.SHashtable;
import com.planet_ink.game.core.interfaces.Environmental;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Paladin_AbidingAura extends PaladinSkill {
    private final static String localizedName = CMLib.lang().L("Abiding Aura");
    protected Map<MOB, Runnable> abiding = new SHashtable<MOB, Runnable>();

    public Paladin_AbidingAura() {
        super();
        paladinsGroup = new HashSet<MOB>();
    }

    @Override
    public String ID() {
        return "Paladin_AbidingAura";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_HOLYPROTECTION;
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
            && (invoker != null)
            && (CMLib.flags().isGood(invoker))
            && (super.paladinsGroup.contains(msg.source()))) {
            final MOB mob = msg.source();
            final Room startRoom = mob.getStartRoom();
            if ((startRoom != null) && (super.proficiencyCheck(mob, 0, false))) {
                if (mob.fetchAbility("Dueling") != null)
                    return super.okMessage(host, msg);
                final Room oldRoom = mob.location();
                mob.curState().setHitPoints(1);
                oldRoom.show(invoker, mob, CMMsg.MSG_OK_VISUAL, L("<T-YOUPOSS> death is prevented by <S-YOUPOSS> abiding aura!"));
                if (!abiding.containsKey(msg.source())) {
                    final Set<MOB> paladinsGroup = super.paladinsGroup;
                    final MOB killerM = (msg.tool() instanceof MOB) ? (MOB) msg.tool() : null;
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            abiding.remove(mob);
                            if (!mob.amDead()) {
                                if (mob.curState().getHitPoints() < CMath.mul(mob.maxState().getHitPoints(), 0.10)) {
                                    paladinsGroup.remove(mob);
                                    CMLib.combat().postDeath(killerM, mob, null);
                                }
                            }
                        }
                    };
                    super.helpProficiency(mob, 0);
                    abiding.put(mob, runnable);
                    CMLib.threads().scheduleRunnable(runnable, 10000);
                    mob.tell(L("^xThe aura will protect you for only 10 seconds, after which you must be above 10% hit points to survive.^?^."));
                }
                return false;
            }
        }
        return super.okMessage(host, msg);
    }
}
