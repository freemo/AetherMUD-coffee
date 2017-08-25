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
package com.planet_ink.game.Exits;

import com.planet_ink.game.Exits.interfaces.PrepositionExit;


public class OpenPrepositional extends StdOpenDoorway implements PrepositionExit {
    private String entryPreposition = "";
    private String exitPreposition = "";

    @Override
    public String ID() {
        return "OpenPrepositional";
    }

    @Override
    public String Name() {
        return miscText;
    }

    @Override
    public String displayText() {
        return miscText;
    }

    @Override
    public String description() {
        return miscText;
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        if (newMiscText.length() > 0) {
            entryPreposition = "through " + newMiscText;
            exitPreposition = "through " + newMiscText;
        }
    }

    @Override
    public String getEntryPreposition() {
        return entryPreposition;
    }

    @Override
    public void setEntryPreposition(String phrase) {
        entryPreposition = phrase;
    }

    @Override
    public String getExitPreposition() {
        return exitPreposition;
    }

    @Override
    public void setExitPreposition(String phrase) {
        exitPreposition = phrase;
    }
}
