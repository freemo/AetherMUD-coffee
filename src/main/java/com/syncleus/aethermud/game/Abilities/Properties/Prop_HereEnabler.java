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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.TriggeredAffect;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Prop_HereEnabler extends Prop_HaveEnabler {

    public Prop_HereEnabler() {
    }

    @Override
    public String ID() {
        return "Prop_HereEnabler";
    }

    @Override
    public String name() {
        return "Granting skills on arrival";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS;
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_ENTER;
    }

    @Override
    public String accountForYourself() {
        return spellAccountingsWithMask("Grants ", " to the one who enters.");
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if ((msg.sourceMinor() == CMMsg.TYP_ENTER) || (msg.sourceMinor() == CMMsg.TYP_LEAVE)) {
            final Physical baseAffectedP = this.affected;
            if (msg.source().fetchEffect(ID()) == null) {
                synchronized (this) {
                    addMeIfNeccessary(msg.source(), msg.source(), maxTicks);
                    final Prop_HereEnabler here = new Prop_HereEnabler() {
                        @Override
                        public void executeMsg(Environmental host, CMMsg msg) {
                            if ((this.affected == msg.source())
                                && ((msg.sourceMinor() == CMMsg.TYP_ENTER) || (msg.sourceMinor() == CMMsg.TYP_LEAVE))) {
                                final Room R = msg.source().location();
                                if ((R != null) && (R.getArea() != null)) {
                                    if ((baseAffectedP instanceof Room) && (R != baseAffectedP))
                                        unInvoke();
                                    else if ((baseAffectedP instanceof Area) && (((Area) baseAffectedP).inMyMetroArea(R.getArea())))
                                        unInvoke();
                                    else if (R != CMLib.map().roomLocation(baseAffectedP))
                                        unInvoke();
                                }
                            }
                        }

                        @Override
                        public boolean canBeUninvoked() {
                            return true;
                        }

                        @Override
                        public boolean isSavable() {
                            return false;
                        }

                        @Override
                        public void unInvoke() {
                            final Physical M = this.lastMOB;
                            super.removeMyAffectsFromLastMob();
                            super.unInvoke();
                            if (M != null) {
                                M.delEffect(this);
                            }
                        }
                    };
                    msg.source().addEffect(here);
                    here.lastMOB = msg.source();
                    here.spellV = spellV;
                    here.lastMOBeffects = lastMOBeffects;
                    here.processing2 = false;
                    here.clearedYet = clearedYet;
                }
            }
        }

    }

    @Override
    public void affectPhyStats(Physical host, PhyStats affectableStats) {
        processing = false;
    }
}
