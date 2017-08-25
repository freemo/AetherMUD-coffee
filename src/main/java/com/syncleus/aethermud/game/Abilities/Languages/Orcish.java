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
import java.util.Vector;


public class Orcish extends StdLanguage {
    private final static String localizedName = CMLib.lang().L("Orcish");
    public static List<String[]> wordLists = null;

    public Orcish() {
        super();
    }

    @Override
    public String ID() {
        return "Orcish";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public List<String[]> translationVector(String language) {
        if (wordLists == null) {
            final String[] one = {"a"};
            final String[] two = {"uk", "ik", "og", "eg", "ak", "ag"};
            final String[] three = {"uko", "ugg", "ick", "ehk", "akh", "oog"};
            final String[] four = {"blec", "mugo", "guck", "gook", "kill", "dead", "twak", "kwat", "klug"};
            final String[] five = {"bleko", "thwak", "klarg", "gluck", "kulgo", "mucka", "splat", "kwath", "garth", "blark"};
            final String[] six = {"kalarg", "murder", "bleeke", "kwargh", "guttle", "thungo"};
            wordLists = new Vector<String[]>();
            wordLists.add(one);
            wordLists.add(two);
            wordLists.add(three);
            wordLists.add(four);
            wordLists.add(five);
            wordLists.add(six);
        }
        return wordLists;
    }
}
