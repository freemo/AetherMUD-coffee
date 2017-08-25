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
package com.planet_ink.game.Abilities.Misc;

import com.planet_ink.game.core.CMLib;


public class Acidbreath extends Dragonbreath {
    private final static String localizedName = CMLib.lang().L("Acidbreath");
    private static final String[] triggerStrings = I(new String[]{"ACIDBREATH"});

    @Override
    public String ID() {
        return "Acidbreath";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String text() {
        return "acid";
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(text());
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }
}
