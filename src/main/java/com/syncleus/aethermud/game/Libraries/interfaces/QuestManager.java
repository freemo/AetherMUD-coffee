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

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.Quest;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.collections.DVector;
import com.syncleus.aethermud.game.core.collections.TriadList;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.Enumeration;
import java.util.List;

public interface QuestManager extends CMLibrary {
    public final static int QM_COMMAND_MASK = 127;
    public final static int QM_COMMAND_OPTIONAL = 128;

    public Quest objectInUse(Environmental E);

    public int numQuests();

    public Enumeration<Quest> enumQuests();

    public Quest fetchQuest(int i);

    public Quest fetchQuest(String qname);

    public Quest findQuest(String qname);

    public void addQuest(Quest Q);

    public void delQuest(Quest Q);

    public void save();

    public List<String> parseQuestSteps(List<String> script, int startLine, boolean rawLineInput);

    public List<List<String>> parseQuestCommandLines(List<?> script, String cmdOnly, int startLine);

    public int getHolidayIndex(String named);

    public String getHolidayName(int index);

    public String listHolidays(Area A, String otherParms);

    public String deleteHoliday(int holidayNumber);

    public void modifyHoliday(MOB mob, int holidayNumber);

    public String alterHoliday(String oldName, HolidayData newData);

    public String createHoliday(String named, String areaName, boolean save);

    public StringBuffer getDefaultHoliData(String named, String area);

    public Object getHolidayFile();

    public HolidayData getEncodedHolidayData(String dataFromStepsFile);

    public List<List<String>> breakOutMudChatVs(String MUDCHAT, TriadList<String, String, Integer> behaviors);

    public String breakOutMaskString(String s, List<String> p);

    public DVector getQuestTemplate(MOB mob, String fileToGet);

    public Quest questMaker(MOB mob);

    public List<Quest> getPlayerPersistantQuests(MOB player);

    public GenericEditor.CMEval getQuestCommandEval(QMCommand command);
    public enum QMCommand {
        $TITLE,
        $LABEL,
        $EXPRESSION,
        $UNIQUE_QUEST_NAME,
        $CHOOSE,
        $ITEMXML,
        $STRING,
        $ROOMID,
        $AREA,
        $MOBXML,
        $NAME,
        $LONG_STRING,
        $MOBXML_ONEORMORE,
        $ITEMXML_ONEORMORE,
        $ZAPPERMASK,
        $ABILITY,
        $EXISTING_QUEST_NAME,
        $HIDDEN,
        $FACTION,
        $TIMEEXPRESSION
    }

    public interface HolidayData {
        public TriadList<String, String, Integer> settings();

        public TriadList<String, String, Integer> behaviors();

        public TriadList<String, String, Integer> properties();

        public TriadList<String, String, Integer> stats();

        public List<String> stepV();

        public Integer pricingMobIndex();
    }

}
