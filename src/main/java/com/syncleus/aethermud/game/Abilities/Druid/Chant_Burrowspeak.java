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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMLib;


public class Chant_Burrowspeak extends Chant_SpeakWithAnimals {
    private final static String localizedName = CMLib.lang().L("Burrowspeak");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Burrowspeak)");

    @Override
    public String ID() {
        return "Chant_Burrowspeak";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return super.canBeUninvoked ? localizedStaticDisplay : "";
    }

    @Override
    protected boolean canSpeakWithThis(MOB mob) {
        if (CMLib.flags().isAnimalIntelligence(mob)) {
            final Race R = mob.charStats().getMyRace();
            if (R.racialCategory().equals("Rodent")
                || R.racialCategory().equals("Ophidian")
                || R.racialCategory().equals("Worm"))
                return true;
        }
        return false;
    }

    @Override
    protected String canSpeakWithWhat() {
        return "speak with burrowing creatures";
    }

    @Override
    protected String canSpeakWithWhatNoun() {
        return "speech of burrowing creatures";
    }

    @Override
    protected void sayYouAreDone(MOB mob) {
        if ((mob.location() != null) && (!mob.amDead()))
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> ability to speak with burrowing creatures has faded."));
    }
}
