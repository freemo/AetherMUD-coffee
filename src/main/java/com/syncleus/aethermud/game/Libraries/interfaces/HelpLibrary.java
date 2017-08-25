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
package com.syncleus.aethermud.game.Libraries.interfaces;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;

import java.util.List;
import java.util.Properties;

public interface HelpLibrary extends CMLibrary {
    public List<String> getTopics(boolean archonHelp, boolean standardHelp);

    public String fixHelp(String tag, String str, MOB forMOB);

    public StringBuilder getHelpText(String helpStr, MOB forMOB, boolean favorAHelp);

    public StringBuilder getHelpText(String helpStr, MOB forMOB, boolean favorAHelp, boolean noFix);

    public StringBuilder getHelpText(String helpStr, Properties rHelpFile, MOB forMOB);

    public StringBuilder getHelpList(String helpStr, Properties rHelpFile1, Properties rHelpFile2, MOB forMOB);

    public StringBuilder getHelpText(String helpStr, Properties rHelpFile, MOB forMOB, boolean noFix);

    public Properties getArcHelpFile();

    public Properties getHelpFile();

    public void unloadHelpFile(MOB mob);

    public boolean isPlayerSkill(String helpStr);

    public void addHelpEntry(String ID, String text, boolean archon);

    /**
     * Returns a description of the amount of a particular cost is required
     * by the given mob to use the given ability.
     * @see Ability#USAGE_DESCS
     * @param A the Ability to find usage for
     * @param whichUsageCode the Ability.USAGE_ code.
     * @param forMOB null, or a mob to make the report more specific
     * @return either the word "all" or a number in string form
     */
    public String getActualAbilityUsageDesc(Ability A, int whichUsageCode, MOB forMOB);

    public String getAbilityTargetDesc(Ability A);

    public String getAbilityQualityDesc(Ability A);

    public String getAbilityRangeDesc(Ability A);

    public String getAbilityCostDesc(Ability A, final MOB forMOB);
}
