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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.Set;

/**
 * Title: False Realities Flavored CoffeeMUD
 * Description: The False Realities Version of CoffeeMUD
 * Copyright: Copyright (c) 2004 Jeremy Vyska
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Company: http://www.falserealities.com
 * @author FR - Jeremy Vyska; CM - Bo Zimmerman
 * @version 1.0.0.0
 */
public class TargetPlayer extends ActiveTicker {
    public TargetPlayer() {
        super();
        minTicks = 3;
        maxTicks = 12;
        chance = 100;
        tickReset();
    }

    @Override
    public String ID() {
        return "TargetPlayer";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "hero targeting";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (canAct(ticking, tickID)) {
            final MOB mob = (MOB) ticking;
            if (mob.getVictim() != null) {
                final Set<MOB> theBadGuys = mob.getVictim().getGroupMembers(new HashSet<MOB>());
                MOB shouldFight = null;
                for (final Object element : theBadGuys) {
                    final MOB consider = (MOB) element;
                    if (consider.isMonster())
                        continue;
                    if (shouldFight == null) {
                        shouldFight = consider;
                    } else {
                        if (((shouldFight.phyStats() != null) && (consider.phyStats() != null))
                            && (shouldFight.phyStats().level() > consider.phyStats().level()))
                            shouldFight = consider;
                    }
                }
                if (shouldFight != null) {
                    if (shouldFight.equals(mob.getVictim()))
                        return true;
                    else if (CMLib.flags().canBeSeenBy(shouldFight, mob)) {
                        mob.setVictim(shouldFight);
                    }
                }
            }
            return true;
        }
        return true;
    }
}
