package com.planet_ink.coffee_mud.interfaces;
import java.util.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

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
public interface DatabaseEngine
{
	public String errorStatus();
	public void resetconnections();
	
	public static final int JOURNAL_KEY=0;
	public static final int JOURNAL_FROM=1;
	public static final int JOURNAL_DATE=2;
	public static final int JOURNAL_TO=3;
	public static final int JOURNAL_SUBJ=4;
	public static final int JOURNAL_MSG=5;
	public static final int JOURNAL_DATE2=6;
	
	public void DBUpdateFollowers(MOB mob);
	public void DBReadContent(Room thisRoom, Hashtable rooms);
	public void DBUpdateExits(Room room);
	public String DBReadRoomMOBData(String roomID, String mobID);
	public String DBReadRoomDesc(String roomID);
	public void DBUpdateTheseMOBs(Room room, Vector mobs);
	public void DBUpdateMOBs(Room room);
	public void DBCreateRoom(Room room, String LocaleID);
	public void DBUpdateRoom(Room room);
	public void DBUpdateMOB(MOB mob);
	public void DBUpdateRoomMOB(String keyName, Room room, MOB mob);
	public void DBUpdateItems(Room room);
	public void DBUpdateQuests(Vector quests);
	public void DBUpdateQuest(Quest Q);
	public void DBReadQuests(MudHost myHost);
	public void DBReCreate(Room room, String oldID);
	public void DBDeleteRoom(Room room);
	public void DBReadMOB(MOB mob);
	public void DBClanFill(String clan, Vector members, Vector roles, Vector lastDates);
	public void DBUpdateClanMembership(String name, String clan, int role);
	public void DBUpdateClan(Clan C);
	public void DBDeleteClan(Clan C);
	public void DBCreateClan(Clan C);
	public void DBUpdateEmail(MOB mob);
	public void DBUpdatePassword(MOB mob);
	public String[] DBFetchEmailData(String name);
	public String DBEmailSearch(String email);
	public Vector getUserList();
	public Vector userList();
	public void DBReadFollowers(MOB mob, boolean bringToLife);
	public void DBDeleteMOB(MOB mob);
	public void DBCreateCharacter(MOB mob);
	public Area DBCreateArea(String areaName, String areaType);
	public void DBDeleteArea(Area A);
	public void DBUpdateArea(String keyName,Area A);
	public Vector DBReadJournal(String Journal);
	public int DBCountJournal(String Journal, String from, String to);
	public void DBWriteJournal(String Journal, String from, String to, String subject, String message, int which);
	public void DBDeleteJournal(String Journal, int which);
	public void DBDeleteJournal(String oldkey);
	public boolean DBReadUserOnly(MOB mob);
	public boolean DBUserSearch(MOB mob, String Login);
	public void vassals(MOB mob, String liegeID);
	public Vector DBReadAllPlayerData(String playerID);
	public Vector DBReadData(String playerID, String section);
	public int DBCountData(String playerID, String section);
	public Vector DBReadData(String playerID, String section, String key);
	public Vector DBReadData(String section);
	public void DBDeleteData(String playerID, String section);
	public void DBDeleteData(String playerID, String section, String key);
	public void DBDeleteData(String section);
	public void DBCreateData(String player, String section, String key, String data);
	public Vector DBReadRaces();
	public void DBDeleteRace(String raceID);
	public void DBCreateRace(String raceID,String data);
	public Vector DBReadClasses();
	public void DBDeleteClass(String classID);
	public void DBCreateClass(String classID,String data);
	public Object DBReadStat(long startTime);
	public void DBDeleteStat(long startTime);
	public void DBCreateStat(long startTime,long endTime,String data);
	public void DBUpdateStat(long startTime, String data);
	public Vector DBReadStats(long startTime);
}
