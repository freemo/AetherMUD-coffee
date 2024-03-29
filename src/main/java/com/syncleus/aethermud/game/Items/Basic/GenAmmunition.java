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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Ammunition;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenAmmunition extends StdItem implements Ammunition {
    protected String readableText = "";

    public GenAmmunition() {
        super();

        setName("a batch of arrows");
        setDisplayText("a generic batch of arrows sits here.");
        setUsesRemaining(100);
        setAmmunitionType("arrows");
        setDescription("");
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenAmmunition";
    }

    @Override
    public boolean subjectToWearAndTear() {
        return false;
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public String text() {
        return CMLib.aetherMaker().getPropertiesStr(this, false);
    }

    @Override
    public String readableText() {
        return readableText;
    }

    @Override
    public void setReadableText(String text) {
        if (isReadable())
            CMLib.flags().setReadable(this, false);
        readableText = text;
    }

    @Override
    public String ammunitionType() {
        return readableText;
    }

    @Override
    public void setAmmunitionType(String text) {
        readableText = text;
    }

    @Override
    public int ammunitionRemaining() {
        return usesRemaining();
    }

    @Override
    public void setAmmoRemaining(int amount) {
        this.setUsesRemaining(amount);
    }

    @Override
    public void setMiscText(String newText) {
        miscText = "";
        CMLib.aetherMaker().setPropertiesStr(this, newText, false);
        recoverPhyStats();
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        final MOB mob = msg.source();
        if (!msg.amITarget(this))
            return super.okMessage(myHost, msg);
        else
            switch (msg.targetMinor()) {
                case CMMsg.TYP_HOLD:
                    //mob.tell(L("You can't hold @x1.",name()));
                    //return false;
                    break;
                case CMMsg.TYP_WEAR:
                    mob.tell(L("You can't wear @x1.", name()));
                    return false;
                case CMMsg.TYP_WIELD:
                    mob.tell(L("You can't wield @x1 as a weapon.", name()));
                    return false;
            }
        return super.okMessage(myHost, msg);
    }
}
