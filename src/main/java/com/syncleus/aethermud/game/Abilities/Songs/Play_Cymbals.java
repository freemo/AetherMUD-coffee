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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.MusicalInstrument.InstrumentType;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.Directions;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.LinkedList;
import java.util.List;

/*
   Copyright 2003-2017 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class Play_Cymbals extends Play_Instrument {
    private final static String localizedName = CMLib.lang().L("Cymbals");
    private static Ability theSpell = null;

    @Override
    public String ID() {
        return "Play_Cymbals";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected InstrumentType requiredInstrumentType() {
        return InstrumentType.CYMBALS;
    }

    @Override
    public String mimicSpell() {
        return "Spell_Knock";
    }

    @Override
    protected Ability getSpell() {
        if (theSpell != null)
            return theSpell;
        if (mimicSpell().length() == 0)
            return null;
        theSpell = CMClass.getAbility(mimicSpell());
        return theSpell;
    }

    @Override
    protected void inpersistantAffect(MOB mob) {
        if (getSpell() != null) {
            final Room R = mob.location();
            if (R != null) {
                final List<Physical> knockables = new LinkedList<Physical>();
                int dirCode = -1;
                if (mob == invoker()) {
                    for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                        final Exit e = R.getExitInDir(d);
                        if ((e != null) && (e.hasADoor()) && (e.hasALock()) && (e.isLocked())) {
                            knockables.add(e);
                            dirCode = d;
                        }
                    }
                    for (int i = 0; i < R.numItems(); i++) {
                        final Item I = R.getItem(i);
                        if ((I != null) && (I instanceof Container) && (I.container() == null)) {
                            final Container C = (Container) I;
                            if (C.hasADoor() && C.hasALock() && C.isLocked())
                                knockables.add(C);
                        }
                    }
                }
                for (int i = 0; i < mob.numItems(); i++) {
                    final Item I = mob.getItem(i);
                    if ((I != null) && (I instanceof Container) && (I.container() == null)) {
                        final Container C = (Container) I;
                        if (C.hasADoor() && C.hasALock() && C.isLocked())
                            knockables.add(C);
                    }
                }
                for (final Physical P : knockables) {
                    int levelDiff = P.phyStats().level() - (mob.phyStats().level() + (2 * super.getXLEVELLevel(mob)));
                    if (levelDiff < 0)
                        levelDiff = 0;
                    if (proficiencyCheck(mob, -(levelDiff * 25), false)) {
                        CMMsg msg = CMClass.getMsg(mob, P, this, CMMsg.MSG_CAST_VERBAL_SPELL, L("@x1 begin(s) to glow!", P.name()));
                        if (R.okMessage(mob, msg)) {
                            R.send(mob, msg);
                            msg = CMClass.getMsg(mob, P, null, CMMsg.MSG_UNLOCK, null);
                            CMLib.utensils().roomAffectFully(msg, R, dirCode);
                            msg = CMClass.getMsg(mob, P, null, CMMsg.MSG_OPEN, L("<T-NAME> opens."));
                            CMLib.utensils().roomAffectFully(msg, R, dirCode);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }
}
