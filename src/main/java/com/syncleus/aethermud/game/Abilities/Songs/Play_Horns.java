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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Items.interfaces.MusicalInstrument.InstrumentType;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;

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
public class Play_Horns extends Play_Instrument {
    private final static String localizedName = CMLib.lang().L("Horns");
    private static Ability theSpell = null;

    @Override
    public String ID() {
        return "Play_Horns";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected InstrumentType requiredInstrumentType() {
        return InstrumentType.HORNS;
    }

    @Override
    public String mimicSpell() {
        return "Spell_FaerieFire";
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
}
