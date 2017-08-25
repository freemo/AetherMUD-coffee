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
package com.syncleus.aethermud.game.Abilities.Languages;

import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;


public class Blah extends StdLanguage {
    private final static String localizedName = CMLib.lang().L("Blah");
    public static List<String[]> wordLists = null;
    private static boolean mapped = false;

    public Blah() {
        super();
        if (!mapped) {
            mapped = true;
            CMLib.ableMapper().addCharAbilityMapping("Archon", 1, ID(), false);
        }
    }

    @Override
    public String ID() {
        return "Blah";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public List<String[]> translationVector(String language) {
        return wordLists;
    }

    @Override
    public String translate(String language, String word) {
        return fixCase(word, "blah");
    }
}
