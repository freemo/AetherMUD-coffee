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
package com.planet_ink.coffee_mud.Abilities.Languages;

import com.planet_ink.coffee_mud.core.CMLib;

import java.util.List;
import java.util.Vector;


public class Gnomish extends StdLanguage {
    private final static String localizedName = CMLib.lang().L("Gnomish");
    public static List<String[]> wordLists = null;

    public Gnomish() {
        super();
    }

    @Override
    public String ID() {
        return "Gnomish";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public List<String[]> translationVector(String language) {
        if (wordLists == null) {
            final String[] one = {"y"};
            final String[] two = {"te", "it", "at", "to"};
            final String[] three = {"nep", "tem", "tit", "nip", "pop", "pon", "upo", "wip", "pin"};
            final String[] four = {"peep", "meep", "neep", "pein", "nopo", "popo", "woop", "weep", "teep", "teet"};
            final String[] five = {"whemp", "thwam", "nippo", "punno", "upoon", "teepe", "tunno", "ponno", "twano", "ywhap"};
            final String[] six = {"tawhag", "ponsol", "paleep", "ponpopol", "niptittle", "minwap", "tinmipmip", "niptemtem", "wipwippoo"};
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
