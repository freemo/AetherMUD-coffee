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
package com.syncleus.aethermud.game.Items.MiscMagic;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.Basic.StdJournal;
import com.syncleus.aethermud.game.Items.interfaces.ArchonOnly;
import com.syncleus.aethermud.game.Items.interfaces.MiscMagic;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class ArchonJournal extends StdJournal implements ArchonOnly, MiscMagic {
    public ArchonJournal() {
        super();

        setName("");
        setName("the Archon Journal");
        setDisplayText("a fabulous tome of wisdom has been left here");
        setDescription("");
        secretIdentity = "The Archon's Journal.  Just READ me.";
        baseGoldValue = 20000;
        material = RawMaterial.RESOURCE_PAPER;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "ArchonJournal";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        final MOB mob = msg.source();
        if (mob.location() == null)
            return true;

        if (msg.amITarget(this))
            switch (msg.targetMinor()) {
                case CMMsg.TYP_HOLD:
                case CMMsg.TYP_WEAR:
                case CMMsg.TYP_WIELD:
                case CMMsg.TYP_GET:
                case CMMsg.TYP_PUSH:
                case CMMsg.TYP_PULL:
                    if (!CMSecurity.isASysOp(msg.source())) {
                        mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("@x1 flashes and falls out of <S-HIS-HER> hands!", name()));
                        return false;
                    }
                    break;
            }
        return true;
    }

}
