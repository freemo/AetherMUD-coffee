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
package com.syncleus.aethermud.game.Abilities.Archon;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Archon_MatrixPossess extends ArchonSkill {
    private final static String localizedName = CMLib.lang().L("Matrix Possess");
    private static final String[] triggerStrings = I(new String[]{"MPOSSESS"});
    protected MOB possessingM = null;

    @Override
    public String ID() {
        return "Archon_MatrixPossess";
    }

    @Override
    public String name() {
        return localizedName;
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
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ARCHON;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        final Room R = CMLib.map().roomLocation(affected);
        if (R == null)
            return false;
        if (((invoker != null) && (!invoker.isPossessing()) && (possessingM != null))
            || (!CMLib.flags().isInTheGame(invoker, true))
            || (!CMLib.flags().isInTheGame(affected, true))) {
            unInvoke();
            return false;
        }
        if ((possessingM == null)
            || ((possessingM != null) && (possessingM.soulMate() == invoker) && (possessingM.location() != R))) {
            for (int i = 0; i < R.numInhabitants(); i++) {
                final MOB M = R.fetchInhabitant(i);
                if ((M != null) && (M.isMonster()) && (M.soulMate() == null)) {
                    if (possessingM != null)
                        possessingM.dispossess(false);
                    possessingM = M;
                    CMLib.commands().forceInternalCommand(invoker, "Possess", M, Boolean.TRUE);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        boolean emptyOK = false;
        final String name = (commands == null) ? "" : CMParms.combine(commands, 0);
        if ((name.length() == 0) && (emptyOK))
            return true;

        if (name.length() == 0) {
            mob.tell(L("Matrix possess around whom?"));
            return false;
        }

        MOB target = CMLib.players().getLoadPlayer(name);
        if (target == null) {
            mob.tell(L("There is no one called @x1.", name));
            return false;
        }
        if (!CMLib.flags().isInTheGame(target, true)) {
            mob.tell(L("@x1 is not online.", target.Name()));
            return false;
        }
        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("@x1 is already being matrix possessed.", target.Name()));
            return false;
        }

        if (!CMSecurity.isAllowed(mob, target.location(), CMSecurity.SecFlag.POSSESS)) {
            mob.tell(L("You can not possess @x1.", target.Name()));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MAGIC | CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0),
                L("<S-NAME> you matrix-possess <T-NAME>"), null, null);
            final Room R = target.location();
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                this.setInvoker(mob);
                this.unInvoked = false;
                this.possessingM = null;
                startTickDown(mob, target, Integer.MAX_VALUE / 2);
            }
        }
        return success;
    }
}
