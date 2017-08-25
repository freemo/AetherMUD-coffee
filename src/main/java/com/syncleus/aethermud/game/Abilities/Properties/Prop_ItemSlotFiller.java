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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharState;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;


public class Prop_ItemSlotFiller extends Property {
    protected static Item fakeItem = null;
    protected int slotCount = 1;
    protected String slotType = "";
    protected Ability[] affects = new Ability[0];
    protected Physical affected2 = null;
    protected List<String> skips = new ArrayList<String>(0);

    @Override
    public String ID() {
        return "Prop_ItemSlotFiller";
    }

    @Override
    public String name() {
        return "Provides for enhanced item slots.";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public void setAffectedOne(Physical P) {
        if ((P == this.affected) || (affected == null)) {
            super.setAffectedOne(P);
        } else {
            affected2 = P;
            for (Ability A : getAffects()) {
                if ((A != null) && (!A.ID().equals("Prop_ItemSlot")))
                    A.setAffectedOne(P);
            }
        }
    }

    protected Physical getAffected() {
        if (affected2 instanceof Item)
            return affected2;
        if (fakeItem == null) {
            fakeItem = CMClass.getBasicItem("StdItem");
        }
        return fakeItem;
    }

    protected Ability[] getAffects() {
        if ((affects.length == 0) && (this.affecting() != null) && (this.affecting().numEffects() > 1)) {
            final List<Ability> newAffects = new LinkedList<Ability>();
            for (Enumeration<Ability> a = this.affecting().effects(); a.hasMoreElements(); ) {
                Ability A = a.nextElement();
                if ((A != this) && (!skips.contains(A.ID().toUpperCase()))) {
                    A.setAffectedOne(getAffected());
                    newAffects.add(A); // not the copy!
                }
            }
            affects = newAffects.toArray(new Ability[0]);
        }
        return affects;
    }

    @Override
    public void setMiscText(String text) {
        slotCount = CMParms.getParmInt(text, "NUM", 1);
        slotType = CMParms.getParmStr(text, "TYPE", "");
        skips.clear();
        skips.addAll(CMParms.parseCommas(CMParms.getParmStr(text(), "SKIPS", "").toUpperCase(), true));
        super.setMiscText(text);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        for (Ability A : getAffects()) {
            if ((A != null) && (!A.ID().equals("Prop_ItemSlot"))) {
                if (!A.okMessage(myHost, msg))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        for (Ability A : getAffects()) {
            if ((A != null) && (!A.ID().equals("Prop_ItemSlot"))) {
                A.executeMsg(myHost, msg);
            }
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public void affectPhyStats(Physical host, PhyStats affectableStats) {
        for (Ability A : getAffects()) {
            if ((A != null) && (!A.ID().equals("Prop_ItemSlot"))) {
                A.affectPhyStats(host, affectableStats);
            }
        }
        super.affectPhyStats(host, affectableStats);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
        for (Ability A : getAffects()) {
            if ((A != null) && (!A.ID().equals("Prop_ItemSlot"))) {
                A.affectCharStats(affectedMOB, affectedStats);
            }
        }
        super.affectCharStats(affectedMOB, affectedStats);
    }

    @Override
    public void affectCharState(MOB affectedMOB, CharState affectedState) {
        for (Ability A : getAffects()) {
            if ((A != null) && (!A.ID().equals("Prop_ItemSlot"))) {
                A.affectCharState(affectedMOB, affectedState);
            }
        }
        super.affectCharState(affectedMOB, affectedState);
    }
}
