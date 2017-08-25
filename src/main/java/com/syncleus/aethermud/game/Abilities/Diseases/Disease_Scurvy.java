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
package com.syncleus.aethermud.game.Abilities.Diseases;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Disease_Scurvy extends Disease {
    private final static String localizedName = CMLib.lang().L("Scurvy");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Scurvy)");

    @Override
    public String ID() {
        return "Disease_Scurvy";
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
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 15 * CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
    }

    @Override
    protected int DISEASE_DELAY() {
        return 30;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your scurvy is cured!");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> get(s) scurvy!^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> spit(s) out a tooth.");
    }

    @Override
    public int spreadBitmap() {
        return 0;
    }

    @Override
    public int difficultyLevel() {
        return 5;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected == null)
            return false;
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, DISEASE_AFFECT());
            return true;
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) / 2);
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        if ((msg.source() == affected)
            && (msg.target() instanceof Item)
            && (msg.targetMinor() == CMMsg.TYP_EAT)) {
            switch (((Item) msg.target()).material()) {
                case RawMaterial.RESOURCE_ORANGES:
                case RawMaterial.RESOURCE_PEPPERS:
                case RawMaterial.RESOURCE_LIMES:
                case RawMaterial.RESOURCE_LEMONS:
                    unInvoke();
                    break;
                default:
                    break;
            }
        }
        super.executeMsg(myHost, msg);
    }
}
