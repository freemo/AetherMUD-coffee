package com.planet_ink.coffee_mud.Libraries.interfaces;
import com.planet_ink.coffee_mud.core.CMFile.CMVFSFile;
import com.planet_ink.coffee_mud.core.database.ClanLoader;
import com.planet_ink.coffee_mud.core.database.DBConnections;
import com.planet_ink.coffee_mud.core.database.DBConnector;
import com.planet_ink.coffee_mud.core.database.DataLoader;
import com.planet_ink.coffee_mud.core.database.GAbilityLoader;
import com.planet_ink.coffee_mud.core.database.GCClassLoader;
import com.planet_ink.coffee_mud.core.database.GRaceLoader;
import com.planet_ink.coffee_mud.core.database.JournalLoader;
import com.planet_ink.coffee_mud.core.database.MOBloader;
import com.planet_ink.coffee_mud.core.database.PollLoader;
import com.planet_ink.coffee_mud.core.database.QuestLoader;
import com.planet_ink.coffee_mud.core.database.RoomLoader;
import com.planet_ink.coffee_mud.core.database.StatLoader;
import com.planet_ink.coffee_mud.core.database.VFSLoader;
import com.planet_ink.coffee_mud.core.exceptions.CMException;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.Clan.MemberRecord;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2004-2016 Bo Zimmerman

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

/**
 * Not really much point in saying a lot here.
 * This has all the methods most closely related
 * to reading from, writing to, and updating 
 * the database.  That's all there is to it.
 * 
 * @author Bo Zimmerman
 *
 */
public interface DatabaseEngine extends CMLibrary
{
	/**
	 * An enum of all the database table types.
	 * These are the dividers by which different
	 * connections to different databases can be
	 * assigned to different tables.
	 * 
	 * @author Bo Zimmerman
	 *
	 */
	public static enum DatabaseTables
	{
		DBABILITY,
		DBCHARCLASS,
		DBRACE,
		DBPLAYERS,
		DBPLAYERDATA,
		DBMAP,
		DBSTATS,
		DBPOLLS,
		DBVFS,
		DBJOURNALS,
		DBQUEST,
		DBCLANS,
		DBBACKLOG
	}

	/**
	 * Returns the database status, formatted for html.
	 * @return the database status, formatted for html.
	 */
	public String errorStatus();

	/**
	 * Forces all existing database connections to be closed,
	 * and then re-open.
	 */
	public void resetConnections();

	/**
	 * Returns the connector object to the database, allowing
	 * SQL statements to be run.  
	 * @return the connector object to the database
	 */
	public DBConnector getConnector();

	/**
	 * "Pings" all connections to the database by issueing
	 * a "SELECT 1 FROM CMCHAR".
	 * @return the number of connections pinged
	 */
	public int pingAllConnections();

	/**
	 * "Pings" all connections to the database by issueing
	 * a "SELECT 1 FROM CMCHAR", if the connection has
	 * not seen any action in the given number of milliseconds.
	 * @param overrideTimeoutIntervalMillis the connection timeout
	 * @return the number of connections pinged
	 */
	public int pingAllConnections(final long overrideTimeoutIntervalMillis);

	/**
	 * Returns whether the database is connected.
	 * @return whether the database is connected.
	 */
	public boolean isConnected();

	/**
	 * Executes an arbitrary SQL statement against your
	 * main database and returns the response number/code.
	 * @param sql the SQL statement
	 * @return the exec response number (usually num rows)
	 * @throws CMException any errors that occur
	 */
	public int DBRawExecute(String sql) throws CMException;

	/**
	 * Executes an arbitrary SQL query against your 
	 * main database and returns the results as a list of
	 * string arrays, where each array is a row, and each
	 * column is a column from the query.
	 * @param sql the SQL query 
	 * @return the results of the query
	 * @throws CMException any errors that occur
	 */
	public List<String[]> DBRawQuery(String sql) throws CMException;

	/**
	 * Table category: DBMAP
	 * Loads both the mob and item catalogs into the catalog library.
	 */
	public void DBReadCatalogs();

	/**
	 * Table category: DBMAP
	 * Logs all the items in outer space into the space map
	 */
	public void DBReadSpace();

	/**
	 * Table category: DBMAP
	 * This method is used to load the content (items and mobs) of the 
	 * given room id into the given room object, and optionally activate
	 * the contents to live use.  startItemRejuv() is not called, however.
	 * 
	 * @param roomID the id of the room to load
	 * @param thisRoom the room object to load the content into (required!)
	 * @param makeLive true to bring the mobs to life, false to leave them dead.
	 */
	public void DBReadContent(String roomID, Room thisRoom, boolean makeLive);

	/**
	 * Table category: DBMAP
	 * Reloads the basic data of the given area, with a prefilled Name.
	 * It does not load or alter rooms or anything like that, only
	 * the internal variables of the area.
	 * @param A the area to reload
	 */
	public void DBReadAreaData(Area A);

	/**
	 * Table category: DBMAP
	 * Permanently Loads and returns a single Room object, 
	 * without populating its contents yet. This is often done in 
	 * preparation to read the content, the exits, or both.  
	 * It sets the area and room ID, as if the room will have a 
	 * permanent home in the game.
	 * 
	 * @see DatabaseEngine#DBReadContent(String, Room, boolean)
	 * @see DatabaseEngine#DBReReadRoomData(Room)
	 * @see DatabaseEngine#DBReadRoomObject(String, boolean)
	 * 
	 * @param roomID the room id of the room object to load
	 * @param reportStatus true to populate global status, false otherwise
	 * @return the room loaded, or null if it could not be
	 */
	public Room DBReadRoom(String roomID, boolean reportStatus);

	/**
	 * Table category: DBMAP
	 * Reloads the title, description, affects, and other internal 
	 * fields of the given room.
	 * @param room the room to re-read dat afor
	 * @return true, unless something went wrong
	 */
	public boolean DBReReadRoomData(Room room);

	/**
	 * Table category: DBMAP
	 * Read the Room object of the given roomID and returns
	 * the object.  It does not load the contents.  The
	 * difference between this and dbreadroom is beyond me.
	 * I don't think this method actually adds the room
	 * to an area or to the map. 
	 * @see DatabaseEngine#DBReadRoom(String, boolean)
	 * @param roomIDtoLoad the id of the room to load
	 * @param reportStatus true to populate global status, false otherwise
	 * @return the room loaded, or null if it could not be
	 */
	public Room DBReadRoomObject(String roomIDtoLoad, boolean reportStatus);

	/**
	 * Table category: DBMAP
	 * Reads all the Room objects in the given area name
	 * and returns them as an array of rooms.  It does not load the
	 * item or mob contents.  The rooms are not actually
	 * added to the Area or the map.
	 * @param areaName the area name of rooms to load
	 * @param reportStatus
	 * @param reportStatus true to populate global status, false otherwise
	 * @return the rooms loaded
	 */
	public Room[] DBReadRoomObjects(String areaName, boolean reportStatus);
	
	/**
	 * Table category: DBMAP
	 * Reads the room description of the given room id and
	 * returns it.
	 * @param roomID the room id of the description to read
	 * @return the description, or null
	 */
	public String DBReadRoomDesc(String roomID);

	/**
	 * Table category: DBMAP
	 * Counts the number of mobs and items in the room
	 * according to the database, and returns the counts
	 * as a numeric array where the first element is the
	 * number of mobs and the second the number of items.
	 * @param roomID the room id to return counts for
	 * @return the counts as a 2 entry array
	 */
	public int[] DBCountRoomMobsItems(String roomID);
	
	/**
	 * Table category: DBMAP
	 * Reads the exits of the room with the given room id
	 * and populates them into the given room object. It
	 * also connects each exit to the room if it can
	 * get to it through the given rooms area object.
	 * 
	 * @param roomID the room id
	 * @param room the room object to populate
	 * @param reportStatus true to populate global status, false otherwise
	 */
	public void DBReadRoomExits(String roomID, Room room, boolean reportStatus);

	/**
	 * Table category: DBMAP
	 * Resaves the given rooms exit objects, and
	 * linkages to other rooms.
	 * @param room the room whose exits to save
	 */
	public void DBUpdateExits(Room room);

	/**
	 * Table category: DBMAP
	 * Inserts the given item into the given room id
	 * in the database, regardless of its worthiness to
	 * be there.
	 * @param roomID the room id to save the item at
	 * @param thisItem the item to save
	 */
	public void DBCreateThisItem(String roomID, Item thisItem);

	/**
	 * Table category: DBMAP
	 * Inserts the given mob into the given room id
	 * in the database, regardless of its worthiness to
	 * be there.
	 * @param roomID the room id to save the mob at
	 * @param thisMOB the mob to save
	 */
	public void DBCreateThisMOB(String roomID, MOB thisMOB);

	/**
	 * Table category: DBMAP
	 * Given a specific room id, and a specific mob id for
	 * that room (this is a unique coded string found only
	 * in the db, and loaded as the database id), this method
	 * returns the misctext for the mob.
	 * @see Environmental#text()
	 * @see MOB#databaseID()
	 * @param roomID the room id
	 * @param mobID the unique mob string for that room
	 * @return the misc text of the mob
	 */
	public String DBReadRoomMOBMiscText(String roomID, String mobID);

	/**
	 * Table category: DBMAP
	 * This method does a conventional boot load of all rooms and areas
	 * in the given set, including exits and content.  Thin areas
	 * are not loaded, as normal.  Logging and status are as boot.
	 * Rooms and Areas are added to the map.
	 * @param roomsToRead null to read all, or the set of rooms to read
	 */
	public void DBReadAllRooms(RoomnumberSet roomsToRead);

	/**
	 * Table category: DBMAP
	 * Deletes all mobs from the given room from the database and then
	 * adds the given mobs to the database.
	 * @see DatabaseEngine#DBUpdateMOBs(Room)
	 * @param room the savable room
	 * @param mobs the mobs in the room that need saving
	 */
	public void DBUpdateTheseMOBs(Room room, List<MOB> mobs);

	/**
	 * Table category: DBMAP
	 * Deletes all items from the given room from the database and then
	 * adds the given items to the database.
	 * @param room the savable room
	 * @param item the items in the room that need saving
	 */
	public void DBUpdateTheseItems(Room room, List<Item> item);

	/**
	 * Table category: DBMAP
	 * Deletes all mobs from the given room from the database and then
	 * adds all mobs currently in the room which are savable back to
	 * the database.
	 * @see DatabaseEngine#DBUpdateTheseMOBs(Room, List)
	 * @param room the savable room
	 */
	public void DBUpdateMOBs(Room room);

	/**
	 * Table category: DBMAP
	 * Creates the basic room object entry in the data.  Does not
	 * save content or exits, just the room object stuff.
	 * @param room the room to save
	 */
	public void DBCreateRoom(Room room);

	/**
	 * Table category: DBMAP
	 * Updates only the room object for the given room, not the
	 * items or content -- just the title, description, behaviors
	 * and properties, that sort of thing.  No exits either.
	 * Just the room.
	 * @param room the room that needs resaving.
	 */
	public void DBUpdateRoom(Room room);
	
	/**
	 * Table category: DBMAP
	 * Reads all the room numbers for the area with the given name from the
	 * database and returns a compressed roomnumberset object.  
	 * @see com.planet_ink.coffee_mud.Common.interfaces.RoomnumberSet
	 * @param areaName the name of the area to load numbers from
	 * @param reportStatus true to update the global status, false otherwise
	 * @return the rooms in this area, as a compressed set
	 */
	public RoomnumberSet DBReadAreaRoomList(String areaName, boolean reportStatus);

	/**
	 * Table category: DBMAP
	 * Assuming the given mob is savable, this method will update the
	 * database record for the given mob in the given room by deleting
	 * the mob from the room's database record and re-creating him in it.
	 * @param roomID the id of the room that the mob was in
	 * @param mob the mob to save
	 */
	public void DBUpdateMOB(String roomID, MOB mob);

	/**
	 * Table category: DBMAP
	 * Assuming the given item is savable, this method will update the
	 * database record for the given item in the given room by deleting
	 * the item from the room's database record and re-creating it in it.
	 * @param roomID the id of the room that the item was in
	 * @param item the item to save
	 */
	public void DBUpdateItem(String roomID, Item item);

	/**
	 * Table category: DBMAP
	 * Removes the given mob, and only the given mob, from the database
	 * records for the given room id.
	 * @param roomID the id of the room that the mob was in
	 * @param mob the mob to remove
	 */
	public void DBDeleteMOB(String roomID, MOB mob);

	/**
	 * Table category: DBMAP
	 * Removes the given item, and only the given item, from the database
	 * records for the given room id.
	 * @param roomID the id of the room that the item was in
	 * @param item the item to remove
	 */
	public void DBDeleteItem(String roomID, Item item);

	/**
	 * Table category: DBMAP
	 * Updates all of the savable items in the given room by
	 * removing all item records from the database and re-inserting
	 * all of the current item records back in.  
	 * @param room the room to update
	 */
	public void DBUpdateItems(Room room);

	/**
	 * Table category: DBMAP
	 * Changes the room ID of the given room in the database
	 * by updating all of the records with the old room id
	 * to instead use the new room id of the room given.
	 * @param room the room with the new id
	 * @param oldID the old room id
	 */
	public void DBReCreate(Room room, String oldID);

	/**
	 * Table category: DBMAP
	 * Deletes the room and all of its exits and contents
	 * from the database entirely.
	 * @param room the room to blow away
	 */
	public void DBDeleteRoom(Room room);

	/**
	 * Table category: DBMAP
	 * Creates a new area record in the database.
	 * Only does the area record, not the rooms.
	 * @param A the area to create.
	 */
	public void DBCreateArea(Area A);

	/**
	 * Table category: DBMAP
	 * Removes the given area record from the database.
	 * Only does the area record, not the rooms.
	 * @param A the area to destroy.
	 */
	public void DBDeleteArea(Area A);

	/**
	 * Table category: DBMAP
	 * Removes the given area record from the database.
	 * Also removes all the proper rooms from the DB, 
	 * along with exits, items, and characters in those 
	 * rooms.
	 * @param A the area to destroy.
	 */
	public void DBDeleteAreaAndRooms(Area A);

	/**
	 * Table category: DBMAP
	 * Updates the area record in the database with the
	 * given areaID with the data from the given area
	 * object.  The areaID and the name of the area can
	 * be different for area renamings.  
	 * @param areaID the current db area name
	 * @param A the area data to save
	 */
	public void DBUpdateArea(String areaID, Area A);

	/**
	 * Table category: DBPLAYERS
	 * Updates an existing account record in the database
	 * by altering its key fields in the existing record.
	 * @param account the account to update
	 */
	public void DBUpdateAccount(PlayerAccount account);

	/**
	 * Table category: DBPLAYERS
	 * Inserts a new row into the account record in the
	 * database.  Only works the first time for a given
	 * account name
	 * @param account the account to insert
	 */
	public void DBCreateAccount(PlayerAccount account);

	/**
	 * Table category: DBPLAYERS
	 * Removes only the given account from the database.
	 * Does not delete players or any player data.
	 * @param account the account to delete
	 */
	public void DBDeleteAccount(PlayerAccount account);

	/**
	 * Table category: DBPLAYERS
	 * Loads an account with the given name from the 
	 * database, populates a playeraccount object,
	 * and returns it. Does not load players, only
	 * the account record.
	 * @param Login the name of the account to load
	 * @return the player account object
	 */
	public PlayerAccount DBReadAccount(String Login);

	/**
	 * Table category: DBPLAYERS
	 * Populates and returns a list of player account
	 * objects that match the given lowercase substring.
	 * Does this by doing a full scan. :/
	 * @param mask lowercase substring to search for or null
	 * @return the list of playeraccount objects
	 */
	public List<PlayerAccount> DBListAccounts(String mask);

	/**
	 * Table category: DBPLAYERS
	 * Re-builds the entire top-10 player tables from the
	 * database.  It returns a two dimensional array of
	 * lists of players and their scores, in reverse sorted
	 * order by score.  The first dimension of the array is
	 * the time period ordinal (month, year, whatever), and the 
	 * second is the pridestat ordinal. 
	 * 
	 * The cpu percent is the percent (0-100) of each second of work
	 * to spend actually working.  The balance is spent sleeping. 
	 * 
	 * @see com.planet_ink.coffee_mud.Common.interfaces.TimeClock.TimePeriod
	 * @see com.planet_ink.coffee_mud.Common.interfaces.AccountStats.PrideStat
	 * @param topThisMany the number of items in each list
	 * @param scanCPUPercent the percent (0-100) to spend working
	 * @return the arrays of lists of top winner players
	 */
	public List<Pair<String,Integer>>[][] DBScanPridePlayerWinners(int topThisMany, short scanCPUPercent);

	/**
	 * Table category: DBPLAYERS
	 * Re-builds the entire top-10 account tables from the
	 * database.  It returns a two dimensional array of
	 * lists of accounts and their scores, in reverse sorted
	 * order by score.  The first dimension of the array is
	 * the time period ordinal (month, year, whatever), and the 
	 * second is the pridestat ordinal. 
	 * 
	 * The cpu percent is the percent (0-100) of each second of work
	 * to spend actually working.  The balance is spent sleeping. 
	 * 
	 * @see com.planet_ink.coffee_mud.Common.interfaces.TimeClock.TimePeriod
	 * @see com.planet_ink.coffee_mud.Common.interfaces.AccountStats.PrideStat
	 * @param topThisMany the number of items in each list
	 * @param scanCPUPercent the percent (0-100) to spend working
	 * @return the arrays of lists of top winner accounts
	 */
	public List<Pair<String,Integer>>[][] DBScanPrideAccountWinners(int topThisMany, short scanCPUPercent);

	/**
	 * Table category: DBPLAYERS
	 * Does a complete update of the given player mob,
	 * including their items, abilities, and account.
	 * Followers are not included.
	 * @param mob the player mob to update
	 */
	public void DBUpdatePlayer(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * If this system uses the character expiration system, then
	 * this method will scan all the players for expired characters,
	 * and return the list of names, or an empty list if there are
	 * none.  The skipNames is a list of protected names that are
	 * never expired.
	 * @param skipNames the names to never expire, or null
	 * @return the list of expired names, or an empty list
	 */
	public List<String> DBExpiredCharNameSearch(Set<String> skipNames);

	/**
	 * Table category: DBPLAYERS
	 * Updates only the player stats record for the given player.
	 * These are player-unique variables, such as the prompt.
	 * @param mob the player to update
	 */
	public void DBUpdatePlayerPlayerStats(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * Updates only the base mob data in the database for
	 * the given player.  This includes playerstat data
	 * and clan affiliation records, but not items, or
	 * abilities, or other such stuff.
	 * @param mob the player mob to update
	 */
	public void DBUpdatePlayerMOBOnly(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * Updates only the ability records in the database
	 * for the given player.  This also includes effects,
	 * behaviors, and scripts on players, but nothing else.
	 * @param mob the player to update
	 */
	public void DBUpdatePlayerAbilities(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * Updates only the item/inventory/equipment records
	 * in the database for the given player.  Nothing else.
	 * @param mob the player to update
	 */
	public void DBUpdatePlayerItems(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * This method deletes and re-saves the non-player
	 * npc followers of the given player mob.
	 * @param mob the mob whose followers to save
	 */
	public void DBUpdateFollowers(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * Reads and populates a player MOB object
	 * from the database. Does not include 
	 * followers, but does items and abilities.
	 * @param name the name of the player
	 * @return the player mob object, or null
	 */
	public MOB DBReadPlayer(String name);

	/**
	 * Table category: DBPLAYERS
	 * Renames all player records belonging to the old
	 * name to the new name.  Does nothing to existing
	 * cached objects, and the new name better not already
	 * exist!
	 * @param oldName the previous existing name in the db
	 * @param newName the new name to change them to
	 */
	public void DBPlayerNameChange(String oldName, String newName);

	/**
	 * Table category: DBPLAYERS
	 * Updates the email address of the given player in the 
	 * database.  Does not update the account system.
	 * @param mob the mob containing the email addy to change
	 */
	public void DBUpdateEmail(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * Updates the password of the given player in the 
	 * database.  Does not update the account system.
	 * @param name the mob name containing the pw to change
	 * @param password the new password string
	 */
	public void DBUpdatePassword(String name, String password);

	/**
	 * Table category: DBPLAYERS
	 * Returns the email address and autoforward flag for the
	 * player with the given name.  Does not check accounts.
	 * Does, however, check the cache, so its not necessarily
	 * a db check.
	 * @param name the name of the player to get info for
	 * @return the email address and autoforward flag
	 */
	public Pair<String, Boolean> DBFetchEmailData(String name);

	/**
	 * Table category: DBPLAYERS
	 * Returns the name of the player with the given email
	 * address.
	 * @param email the email address to look for
	 * @return the name of the player, or null if not found
	 */
	public String DBPlayerEmailSearch(String email);

	/**
	 * Table category: DBPLAYERS
	 * Returns the list of all characters as thinplayer
	 * objects.  This is the whole bloody list.
	 * @see PlayerLibrary.ThinPlayer
	 * @return the list of all players
	 */
	public List<PlayerLibrary.ThinPlayer> getExtendedUserList();

	/**
	 * Table category: DBPLAYERS
	 * Returns a ThinPlayer object with information about the 
	 * character with the given name.
	 * @see PlayerLibrary.ThinPlayer
	 * @param name the name of the character to return.
	 * @return the thin player of this user, or null if not found
	 */
	public PlayerLibrary.ThinPlayer getThinUser(String name);

	/**
	 * Table category: DBPLAYERS
	 * Returns a list of all the characters in the database.
	 * Each and every one.
	 * @return the list of all character names
	 */
	public List<String> getUserList();

	/**
	 * Table category: DBPLAYERS
	 * Queries and creates mob objects for all the followers of
	 * the player with the given name and returns the list.
	 * It does not bring them to life, add to to any mob,
	 * or start them ticking.  It just makes and returns the
	 * mob objects.
	 * @see DatabaseEngine#DBReadFollowers(MOB, boolean)
	 * @param mobName the name of the mob to return
	 * @return the list of follower mob objects
	 */
	public List<MOB> DBScanFollowers(String mobName);

	/**
	 * Table category: DBPLAYERS
	 * Loads the followers of the given mob and optionally
	 * brings them to life. This will query and create the
	 * follower mob objects, add them to the given mob
	 * at the very least.
	 * @see DatabaseEngine#DBScanFollowers(String)
	 * @param mob the mob whose players to load
	 * @param bringToLife true to bring them to life, false not
	 */
	public void DBReadFollowers(MOB mob, boolean bringToLife);

	/**
	 * Table category: DBPLAYERS
	 * Removes the character and clan affiliation records
	 * from the database and nothing else.  Not abilities, or
	 * items, or followers, or anything else... there are
	 * other methods for that.  This just does in the char
	 * record and clan affiliation.
	 * @param mobName the mob to delete
	 */
	public void DBDeletePlayer(String mobName);

	/**
	 * Table category: DBPLAYERS
	 * Creates the character record for the given mob in
	 * the CMCHAR table in the database, and updates
	 * the account record if any.  Does not handle items,
	 * followers, abilities, or anything else -- just the
	 * base character record.
	 * @param mob the character to create
	 */
	public void DBCreateCharacter(MOB mob);

	/**
	 * Table category: DBPLAYERS
	 * Attempts to return an extremely thin player record by
	 * searching the database for a character with the exact given
	 * name.
	 * @param Login the name to look for
	 * @return null if not found, or a thinner player record
	 */
	public PlayerLibrary.ThinnerPlayer DBUserSearch(String Login);

	/**
	 * Table category: DBPLAYERS
	 * Attempts to return a list of all characters who are
	 * listed as vassals of the character with the given exact
	 * name.  Vassals are characters that are SERVEing another
	 * player.
	 * @param liegeID the character name who would be the liege
	 * @return a list containing as many thin vassal records
	 */
	public List<PlayerLibrary.ThinPlayer> vassals(String liegeID);

	/**
	 * Table category: DBPLAYERS
	 * Attempts to return a list of all characters who are
	 * listed as worshippers of the deity with the given exact
	 * name.  
	 * @param deityID the exact name of the deity to look for
	 * @return a list containing as many thin worshipper records
	 */
	public List<PlayerLibrary.ThinPlayer> worshippers(String deityID);

	/**
	 * Table category: DBQUEST
	 * Updates the entire complete quest list by deleting all quests
	 * from the database and re-inserting the given list of quests.
	 * @see Quest
	 * @see DatabaseEngine#DBUpdateQuest(Quest)
	 * @see DatabaseEngine#DBReadQuests()
	 * @param quests the list of quests to end up with
	 */
	public void DBUpdateQuests(List<Quest> quests);

	/**
	 * Table category: DBQUEST
	 * Updates the given quest in the database by deleting
	 * the old one and re-inserting this one.
	 * @see DatabaseEngine#DBUpdateQuests(List)
	 * @see DatabaseEngine#DBReadQuests()
	 * @see Quest
	 * @param Q the quest to update
	 */
	public void DBUpdateQuest(Quest Q);

	/**
	 * Table category: DBQUEST
	 * Reads all the Quest objects from the database and
	 * returns them as a list.  The Quests are pre-parsed 
	 * and ready to go.  
	 * @see DatabaseEngine#DBUpdateQuests(List)
	 * @see DatabaseEngine#DBUpdateQuest(Quest)
	 * @see Quest
	 * @return the list of quests
	 */
	public List<Quest> DBReadQuests();

	/**
	 * Table category: DBCLANS
	 * Given an exact clan name, this method returns the
	 * entire membership as MemberRecords.
	 * @see MemberRecord
	 * @see DatabaseEngine#DBGetClanMember(String, String)
	 * @see DatabaseEngine#DBUpdateClanMembership(String, String, int)
	 * @see DatabaseEngine#DBUpdateClanKills(String, String, int, int)
	 * @param clan the name of the clan to read members for
	 * @return the list of all the clans members
	 */
	public List<MemberRecord> DBReadClanMembers(String clan);

	/**
	 * Table category: DBCLANS
	 * Reads information about a single clan member of the given exact
	 * name from the clan of the given exact name.
	 * @see MemberRecord
	 * @see DatabaseEngine#DBReadClanMembers(String)
	 * @see DatabaseEngine#DBUpdateClanMembership(String, String, int)
	 * @see DatabaseEngine#DBUpdateClanKills(String, String, int, int)
	 * @param clan the name of the clan to read a member for
	 * @param name the name of the member to read in
	 * @return the member record, or null
	 */
	public MemberRecord DBGetClanMember(String clan, String name);

	/**
	 * Table category: DBCLANS
	 * Updates the Role of the clan member of the given exact name
	 * for the given exact clan.
	 * @see DatabaseEngine#DBReadClanMembers(String)
	 * @see DatabaseEngine#DBGetClanMember(String, String)
	 * @see DatabaseEngine#DBUpdateClanKills(String, String, int, int)
	 * @param name the name of the member to update
	 * @param clan the name of the clan to update a member for
	 * @param role the new role constant
	 */
	public void DBUpdateClanMembership(String name, String clan, int role);

	/**
	 * Table category: DBCLANS
	 * Updates the clan-kill counts for the clan member of the given 
	 * exact name for the given exact clan
	 * @param clan the name of the clan to update a member for
	 * @param name the name of the member to update
	 * @param adjMobKills the number of ADDITIONAL (plus or minus) clan mob kills
	 * @param adjPlayerKills the number of ADDITIONAL (plus or minus) clan pvp kills
	 */
	public void DBUpdateClanKills(String clan, String name, int adjMobKills, int adjPlayerKills);

	/**
	 * Table category: DBCLANS
	 * Reads the entire list of clans, not including their stored items.
	 * The list of clans is then returned for adding to the official 
	 * list, or whatever.
	 * @see Clan
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBUpdateClan(Clan)
	 * @see DatabaseEngine#DBUpdateClanItems(Clan)
	 * @see DatabaseEngine#DBDeleteClan(Clan)
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBCreateClan(Clan)
	 * @return the official list of clan objects
	 */
	public List<Clan> DBReadAllClans();

	/**
	 * Table category: DBCLANS
	 * Reads the entire list of clan items, and uses the map
	 * to get the clan object, and then adds the given item
	 * to both the clan object and  to the World.
	 * @see Clan
	 * @see DatabaseEngine#DBReadAllClans()
	 * @see DatabaseEngine#DBUpdateClan(Clan)
	 * @see DatabaseEngine#DBUpdateClanItems(Clan)
	 * @see DatabaseEngine#DBDeleteClan(Clan)
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBCreateClan(Clan)
	 * @param clans the map of clanids to clan objects
	 */
	public void DBReadClanItems(Map<String,Clan> clans);
	
	/**
	 * Table category: DBCLANS
	 * Updates the given clan objects record in the database.
	 * Does not update the clan items, however.
	 * @see Clan
	 * @see DatabaseEngine#DBReadAllClans()
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBUpdateClanItems(Clan)
	 * @see DatabaseEngine#DBDeleteClan(Clan)
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBCreateClan(Clan)
	 * @param C the clan to update
	 */
	public void DBUpdateClan(Clan C);

	/**
	 * Table category: DBCLANS
	 * Updates the external clan items in the database
	 * for the given clan by deleting all the records
	 * and re-inserting them.
	 * @see Clan
	 * @see DatabaseEngine#DBReadAllClans()
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBUpdateClan(Clan)
	 * @see DatabaseEngine#DBDeleteClan(Clan)
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBCreateClan(Clan)
	 * @param C the clan whose items to update
	 */
	public void DBUpdateClanItems(Clan C);

	/**
	 * Table category: DBCLANS
	 * Removes the given clan, all of its items and records,
	 * membership, and everything about it from the database.
	 * @see Clan
	 * @see DatabaseEngine#DBReadAllClans()
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBUpdateClan(Clan)
	 * @see DatabaseEngine#DBUpdateClanItems(Clan)
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBCreateClan(Clan)
	 * @param C the clan to delete
	 */
	public void DBDeleteClan(Clan C);

	/**
	 * Table category: DBCLANS
	 * Creates the given clan in the database.
	 * @see Clan
	 * @see DatabaseEngine#DBReadAllClans()
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBUpdateClan(Clan)
	 * @see DatabaseEngine#DBUpdateClanItems(Clan)
	 * @see DatabaseEngine#DBReadClanItems(Map)
	 * @see DatabaseEngine#DBDeleteClan(Clan)
	 * @param C the clan to create
	 */
	public void DBCreateClan(Clan C);

	/**
	 * Table category: DBJOURNALS
	 * Returns the list of every journalID in the database that
	 * has at least one message, or an intro.
	 * @see DatabaseEngine#DBUpdateJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBReadJournalEntry(String, String)
	 * @see DatabaseEngine#DBWriteJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, String, String, String, String)
	 * @see DatabaseEngine#DBDeleteJournal(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, String, String, long)
	 * @return the list of every journal ID
	 */
	public List<String> DBReadJournals();

	/**
	 * Table category: DBJOURNALS
	 * Updates the entire database record for the given journal
	 * entry, which must have already been created.
	 * @see JournalEntry
	 * @see DatabaseEngine#DBReadJournals()
	 * @see DatabaseEngine#DBReadJournalEntry(String, String)
	 * @see DatabaseEngine#DBWriteJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, String, String, String, String)
	 * @see DatabaseEngine#DBDeleteJournal(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, String, String, long)
	 * @param journalID the name/id of the journal
	 * @param entry the complete entry record
	 */
	public void DBUpdateJournal(String journalID, JournalEntry entry);

	/**
	 * Table category: DBJOURNALS
	 * Reads an individual message from the given journal by its
	 * message key.
	 * @see JournalEntry
	 * @see DatabaseEngine#DBReadJournals()
	 * @see DatabaseEngine#DBUpdateJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, String, String, String, String)
	 * @see DatabaseEngine#DBDeleteJournal(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, String, String, long)
	 * @param journalID the name/id of the journal
	 * @param messageKey the message key
	 * @return the complete journal entry, or null if it wasn't found
	 */
	public JournalEntry DBReadJournalEntry(String journalID, String messageKey);

	/**
	 * Table category: DBJOURNALS
	 * Creates a new entry in the journal.  If the entries key already
	 * exists, this will make an error.  If the entries key is null, however,
	 * it will generate a new one.
	 * @see JournalEntry
	 * @see DatabaseEngine#DBReadJournals()
	 * @see DatabaseEngine#DBReadJournalEntry(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, String, String, String, String)
	 * @see DatabaseEngine#DBDeleteJournal(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, String, String, long)
	 * @param journalID the name/id of the journal
	 * @param entry the enttry to create
	 */
	public void DBWriteJournal(String journalID, JournalEntry entry);

	/**
	 * Table category: DBJOURNALS
	 * Creates a new entry in this journal.  Will generate a new key.
	 * @see DatabaseEngine#DBReadJournals()
	 * @see DatabaseEngine#DBReadJournalEntry(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBDeleteJournal(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, String, String, long)
	 * @param journalID the name/id of the journal
	 * @param from the author of the message
	 * @param to who the message is to, such as ALL
	 * @param subject the subject of the message
	 * @param message the message to write
	 */
	public void DBWriteJournal(String journalID, String from, String to, String subject, String message);

	/**
	 * Table category: DBJOURNALS
	 * Deletes enter a specific message, or all messages, from the given 
	 * journal.  Deleting all messages deletes the journal.  Take care, 
	 * because of the null thing, this method is dangerous. :)
	 * @see DatabaseEngine#DBReadJournals()
	 * @see DatabaseEngine#DBReadJournalEntry(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, String, String, String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, String, String, long)
	 * @param journalID the name/id of the journal
	 * @param msgKeyOrNull the message key, or null to delete ALL messages
	 */
	public void DBDeleteJournal(String journalID, String msgKeyOrNull);


	/**
	 * Table category: DBJOURNALS
	 * Updates the existing journal message record in the database. 
	 * Nothing is optional, it updates all of the given fields.
	 * @see DatabaseEngine#DBReadJournals()
	 * @see DatabaseEngine#DBReadJournalEntry(String, String)
	 * @see DatabaseEngine#DBUpdateJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, JournalEntry)
	 * @see DatabaseEngine#DBWriteJournal(String, String, String, String, String)
	 * @see DatabaseEngine#DBDeleteJournal(String, String)
	 * @param messageKey the unique message key
	 * @param subject the new message subject
	 * @param msg the new message text
	 * @param newAttributes the new message attributes bitmap
	 */
	public void DBUpdateJournal(String messageKey, String subject, String msg, long newAttributes);
	
	/**
	 * Table category: DBJOURNALS
	 * Writes a new journal entry formatted for the email system and generates
	 * a new message key.
	 * @see CMProps.Str.MAILBOX
	 * @param mailBoxID the journal name/id of the email system MAILBOX
	 * @param journalSource the name/id of the journal that originated the message 
	 * @param from who the author of the email
	 * @param to the recipient 
	 * @param subject the subject of the message
	 * @param message the email message
	 */
	public void DBWriteJournalEmail(String mailBoxID, String journalSource, String from, String to, String subject, String message);

	/**
	 * Table category: DBJOURNALS
	 * Adds to an existing journal entry by taking on a fake/stupid reply as a text
	 * appendage to the main message.  I should do something better some day, and
	 * I did to forums right, but, well, that's how these still are, just like Zelch 64 1.0.
	 * @param journalID the name/id of the journal
	 * @param messageKey the key of the message to append to
	 * @param from who the Reply is from
	 * @param to who the recipient of the reply is
	 * @param subject the subject of the reply
	 * @param message the reply text
	 */
	public void DBWriteJournalReply(String journalID, String messageKey, String from, String to, String subject, String message);

	/**
	 * Table category: DBJOURNALS
	 * Searches all the messages in the journal for the search string as
	 * a (typically) case-insensitive substring of either the subject 
	 * or the message text.  Returns up to 100 journal entries that
	 * form a match.
	 * @param journalID the name/id of the journal to search
	 * @param searchStr the search substring
	 * @return the list of journal entries that match
	 */
	public List<JournalEntry> DBSearchAllJournalEntries(String journalID, String searchStr);

	/**
	 * Table category: DBJOURNALS
	 * For forum journals, updates the number of replies registered with the
	 * parent message represented by the given message Key.
	 * @see DatabaseEngine#DBReadJournalMetaData(com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary.JournalMetaData)
	 * @see DatabaseEngine#DBUpdateJournalMetaData(String, com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary.JournalMetaData)
	 * @param messageKey the key of the parent op message
	 * @param numReplies the new number of replies to register
	 */
	public void DBUpdateMessageReplies(String messageKey, int numReplies);

	/**
	 * Table category: DBJOURNALS
	 * Takes an empty JournalMetaData object, and the journal NAME
	 *  and fills in the rest by querying the database.
	 * @see DatabaseEngine#DBUpdateMessageReplies(String, int)
	 * @see DatabaseEngine#DBUpdateJournalMetaData(String, com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary.JournalMetaData)
	 * @param journalID the name of the journals whose stats to update
	 * @param metaData the created metadata object to fill in
	 */
	public void DBReadJournalMetaData(String journalID, JournalsLibrary.JournalMetaData metaData);

	/**
	 * Table category: DBJOURNALS
	 * Primarily for forum journals, this method updates all of the given
	 * meta data, such as the intro, and so forth by deleting the old 
	 * record and re-inserting it into the database.
	 * @see DatabaseEngine#DBReadJournalMetaData(com.planet_ink.coffee_mud.Libraries.interfaces.JournalsLibrary.JournalMetaData)
	 * @see DatabaseEngine#DBUpdateMessageReplies(String, int)
	 * @param journalID the name of the journals whose stats to update
	 * @param metaData the metadata to resave into the database
	 */
	public void DBUpdateJournalMetaData(String journalID, JournalsLibrary.JournalMetaData metaData);

	/**
	 * Table category: DBJOURNALS
	 * Returns a window of messages from the given journal, either primary messages or replies
	 * to messages, sorted by date, that matches a search, and can be limited, and older (or newer)
	 * than a given timestamp.
	 * @param journalID the name of the journal/forum to load from
	 * @param parent the parent message (for getting replies), or null
	 * @param searchStr the string to search for in the msg/subject, or null
	 * @param newerDate 0 for all real msgs, parent for newer than timestamp, otherwise before timestamp 
	 * @param limit the maximum number of messages to return 
	 * @return the journal entries/messages that match this query
	 */
	public List<JournalEntry> DBReadJournalPageMsgs(String journalID, String parent, String searchStr, long newerDate, int limit);

	/**
	 * Table category: DBJOURNALS
	 * Returns all the messages in the given journal, optionally sorted by update date, ascending.
	 * This is a legacy method, and should be used with care.
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean, int)
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean, int, String[])
	 * @param journalID the journal to read all the messages from
	 * @param ascending true to order the read entries by date
	 * @return the list of all the messages in this journal, an empty list, or null on error
	 */
	public List<JournalEntry> DBReadJournalMsgsByUpdateDate(String journalID, boolean ascending);

	/**
	 * Table category: DBJOURNALS
	 * Returns a limited number of messages from the given journal, optional sorted by update date,
	 * ascending. 
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean)
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean, int, String[])
	 * @param journalID the journal to read all the messages from
	 * @param ascending true to order the read entries by date
	 * @param limit the number of messages to return, max
	 * @return the list of the messages in this journal, an empty list, or null on error
	 */
	public List<JournalEntry> DBReadJournalMsgsByUpdateDate(String journalID, boolean ascending, int limit);

	/**
	 * Table category: DBJOURNALS
	 * Returns a limited number of messages from the given journal, optionally sorted by update date,
	 * ascending, but only those marked as TO the given string array.
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean, int)
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean)
	 * @param journalID the journal to read all the messages from
	 * @param ascending true to order the read entries by date
	 * @param limit the number of messages to return, max
	 * @param tos return only messages marked TO one of the names in this array
	 * @return the list of the messages in this journal, an empty list, or null on error
	 */
	public List<JournalEntry> DBReadJournalMsgsByUpdateDate(String journalID, boolean ascending, int limit, String[] tos);
	
	/**
	 * Table category: DBJOURNALS
	 * Returns all the messages in the given journal, optionally sorted by create date, ascending.
	 * This is a legacy method, and should be used with care.
	 * @see DatabaseEngine#DBReadJournalMsgsByCreateDate(String, boolean, int)
	 * @see DatabaseEngine#DBReadJournalMsgsByCreateDate(String, boolean, int, String[])
	 * @param journalID the journal to read all the messages from
	 * @param ascending true to order the read entries by date
	 * @return the list of all the messages in this journal, an empty list, or null on error
	 */
	public List<JournalEntry> DBReadJournalMsgsByCreateDate(String journalID, boolean ascending);

	/**
	 * Table category: DBJOURNALS
	 * Returns a limited number of messages from the given journal, optional sorted by create date,
	 * ascending. 
	 * @see DatabaseEngine#DBReadJournalMsgsByCreateDate(String, boolean)
	 * @see DatabaseEngine#DBReadJournalMsgsByUpdateDate(String, boolean, int, String[])
	 * @param journalID the journal to read all the messages from
	 * @param ascending true to order the read entries by date
	 * @param limit the number of messages to return, max
	 * @return the list of the messages in this journal, an empty list, or null on error
	 */
	public List<JournalEntry> DBReadJournalMsgsByCreateDate(String journalID, boolean ascending, int limit);

	/**
	 * Table category: DBJOURNALS
	 * Returns a limited number of messages from the given journal, optionally sorted by create date,
	 * ascending, but only those marked as TO the given string array.
	 * @see DatabaseEngine#DBReadJournalMsgsByCreateDate(String, boolean, int)
	 * @see DatabaseEngine#DBReadJournalMsgsByCreateDate(String, boolean)
	 * @param journalID the journal to read all the messages from
	 * @param ascending true to order the read entries by date
	 * @param limit the number of messages to return, max
	 * @param tos return only messages marked TO one of the names in this array
	 * @return the list of the messages in this journal, an empty list, or null on error
	 */
	public List<JournalEntry> DBReadJournalMsgsByCreateDate(String journalID, boolean ascending, int limit, String[] tos);

	/**
	 * Table category: DBJOURNALS
	 * Returns all the messages optionally sent to the given user (or ALL) that 
	 * are newer than the given date.
	 * @see DatabaseEngine#DBReadJournalMsgsOlderThan(String, String, long)
	 * @param journalID the name/ID of the journal/forum
	 * @param to NULL, ALL, or a user the messages were sent to
	 * @param olderDate the date beyond which to return messages for
	 * @return the list of messages that were found
	 */
	public List<JournalEntry> DBReadJournalMsgsNewerThan(String journalID, String to, long olderDate);

	/**
	 * Table category: DBJOURNALS
	 * Returns all the messages optionally sent to the given user (or ALL) that 
	 * are older than the given date.
	 * @see DatabaseEngine#DBReadJournalMsgsNewerThan(String, String, long)
	 * @param journalID the name/ID of the journal/forum
	 * @param to NULL, ALL, or a user the messages were sent to
	 * @param newestDate the date before which to return messages for
	 * @return the list of messages that were found
	 */
	public List<JournalEntry> DBReadJournalMsgsOlderThan(String journalID, String to, long newestDate);

	/**
	 * Table category: DBJOURNALS
	 * Returns the number of messages found in the given journal optionally
	 * sent from the given user name, and/or optionally to the given user
	 * name.
	 * @param journalID the name/id of the journal
	 * @param from NULL, or the user id of a user the messages are from
	 * @param to NULL, or the user id of a user the messages are to
	 * @return the number of messages found
	 */
	public int DBCountJournal(String journalID, String from, String to);

	/**
	 * Table category: DBJOURNALS
	 * Writes a new journal message/entry to the database.  One which is
	 * probably a reply to a parent message, denoted by the parentKey,
	 * which is the message key of the parent.  This method also updates
	 * the number of replies being tracked for the given parent message.
	 * 
	 * @param journalID the name/id of the journal/forum
	 * @param journalSource the originating name/id of an originating journal?
	 * @param from who the message is written by, a user id
	 * @param to who the message is to, or ALL
	 * @param parentKey the message key of the parent message this is a reply to
	 * @param subject the subject of the reply message
	 * @param message the message text of the reply message
	 */
	public void DBWriteJournalChild(String journalID, String journalSource, String from, String to, 
									String parentKey, String subject, String message);

	/**
	 * Table category: DBJOURNALS
	 * A silly function that queries the database for a journal of the given exact
	 * name, and if it is found, it returns it, otherwise it returns null.
	 * @param possibleName the possible name of a journal
	 * @return null, or the name returned
	 */
	public String DBGetRealJournalName(String possibleName);

	/**
	 * Table category: DBJOURNALS
	 * 
	 * @param journalID
	 * @param to
	 * @param olderTime
	 * @return
	 */
	public long[] DBJournalLatestDateNewerThan(String journalID, String to, long olderTime);

	/**
	 * Table category: DBJOURNALS
	 * 
	 * @param name
	 */
	public void DBDeletePlayerJournals(String name);

	/**
	 * Table category: DBJOURNALS
	 * 
	 * @param messageKey
	 * @param views
	 */
	public void DBViewJournalMessage(String messageKey, int views);

	/**
	 * Table category: DBJOURNALS
	 * 
	 * @param messageKey
	 */
	public void DBTouchJournalMessage(String messageKey);

	/**
	 * Table category: DBJOURNALS
	 * 
	 * @param messageKey
	 * @param newDate
	 */
	public void DBTouchJournalMessage(String messageKey, long newDate);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param playerID
	 * @return
	 */
	public List<PlayerData> DBReadAllPlayerData(String playerID);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param playerID
	 * @param section
	 * @return
	 */
	public List<PlayerData> DBReadData(String playerID, String section);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param playerID
	 * @param section
	 * @return
	 */
	public int DBCountData(String playerID, String section);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param playerID
	 * @param section
	 * @param key
	 * @return
	 */
	public List<PlayerData> DBReadData(String playerID, String section, String key);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param section
	 * @param keyMask
	 * @return
	 */
	public List<PlayerData> DBReadDataKey(String section, String keyMask);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param key
	 * @return
	 */
	public List<PlayerData> DBReadDataKey(String key);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param section
	 * @return
	 */
	public List<PlayerData> DBReadData(String section);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param player
	 * @param sections
	 * @return
	 */
	public List<PlayerData> DBReadData(String player, List<String> sections);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param name
	 */
	public void DBDeletePlayerData(String name);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param playerID
	 * @param section
	 */
	public void DBDeleteData(String playerID, String section);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param playerID
	 * @param section
	 * @param key
	 */
	public void DBDeleteData(String playerID, String section, String key);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param key
	 * @param xml
	 */
	public void DBUpdateData(String key, String xml);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param name
	 * @param section
	 * @param key
	 * @param xml
	 */
	public void DBReCreateData(String name, String section, String key, String xml);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param section
	 */
	public void DBDeleteData(String section);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @param player
	 * @param section
	 * @param key
	 * @param data
	 */
	public void DBCreateData(String player, String section, String key, String data);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 */
	public void DBReadArtifacts();

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @return
	 */
	public PlayerData createPlayerData();

	/**
	 * Table category: DBRACE
	 * 
	 * @return
	 */
	public List<AckRecord> DBReadRaces();

	/**
	 * Table category: DBRACE
	 * 
	 * @param raceID
	 */
	public void DBDeleteRace(String raceID);

	/**
	 * Table category: DBRACE
	 * 
	 * @param raceID
	 * @param data
	 */
	public void DBCreateRace(String raceID, String data);

	/**
	 * Table category: DBCHARCLASS
	 * 
	 * @return
	 */
	public List<AckRecord> DBReadClasses();

	/**
	 * Table category: DBCHARCLASS
	 * 
	 * @param classID
	 */
	public void DBDeleteClass(String classID);

	/**
	 * Table category: DBCHARCLASS
	 * 
	 * @param classID
	 * @param data
	 */
	public void DBCreateClass(String classID,String data);

	/**
	 * Table category: DBABILITY
	 * 
	 * @return
	 */
	public List<AckRecord> DBReadAbilities();

	/**
	 * 
	 * @param classID
	 */
	public void DBDeleteAbility(String classID);

	/**
	 * Table category: DBABILITY
	 * 
	 * @param classID
	 * @param typeClass
	 * @param data
	 */
	public void DBCreateAbility(String classID, String typeClass, String data);

	/**
	 * Table category: DBSTATS
	 * 
	 * @param startTime
	 * @return
	 */
	public Object DBReadStat(long startTime);

	/**
	 * Table category: DBSTATS
	 * 
	 * @param startTime
	 */
	public void DBDeleteStat(long startTime);

	/**
	 * Table category: DBSTATS
	 * 
	 * @param startTime
	 * @param endTime
	 * @param data
	 * @return
	 */
	public boolean DBCreateStat(long startTime,long endTime,String data);

	/**
	 * Table category: DBSTATS
	 * 
	 * @param startTime
	 * @param data
	 * @return
	 */
	public boolean DBUpdateStat(long startTime, String data);

	/**
	 * Table category: DBSTATS
	 * 
	 * @param startTime
	 * @return
	 */
	public List<CoffeeTableRow> DBReadStats(long startTime);

	/**
	 * Table category: DBPOLLS
	 * 
	 * @param name
	 * @param player
	 * @param subject
	 * @param description
	 * @param optionXML
	 * @param flag
	 * @param qualZapper
	 * @param results
	 * @param expiration
	 */
	public void DBCreatePoll(String name, String player, String subject, String description, String optionXML, 
							 int flag, String qualZapper, String results, long expiration);

	/**
	 * Table category: DBPOLLS
	 * 
	 * @param OldName
	 * @param name
	 * @param player
	 * @param subject
	 * @param description
	 * @param optionXML
	 * @param flag
	 * @param qualZapper
	 * @param results
	 * @param expiration
	 */
	public void DBUpdatePoll(String OldName, String name, String player, String subject, String description, 
							 String optionXML, int flag, String qualZapper, String results, long expiration);

	/**
	 * Table category: DBPOLLS
	 * 
	 * @param name
	 * @param results
	 */
	public void DBUpdatePollResults(String name, String results);

	/**
	 * Table category: DBPOLLS
	 * 
	 * @param name
	 */
	public void DBDeletePoll(String name);

	/**
	 * Table category: DBPOLLS
	 * 
	 * @return
	 */
	public List<PollData> DBReadPollList();

	/**
	 * Table category: DBPOLLS
	 * 
	 * @param name
	 * @return
	 */
	public PollData DBReadPoll(String name);

	/**
	 * Table category: DBVFS
	 * 
	 * @return
	 */
	public CMFile.CMVFSDir DBReadVFSDirectory();

	/**
	 * Table category: DBVFS
	 * 
	 * @param filename
	 * @return
	 */
	public CMFile.CMVFSFile DBReadVFSFile(String filename);

	/**
	 * Table category: DBVFS
	 * 
	 * @param filename
	 * @param bits
	 * @param creator
	 * @param updateTime
	 * @param data
	 */
	public void DBCreateVFSFile(String filename, int bits, String creator, long updateTime, Object data);

	/**
	 * Table category: DBVFS
	 * 
	 * @param filename
	 * @param bits
	 * @param creator
	 * @param updateTime
	 * @param data
	 */
	public void DBUpSertVFSFile(String filename, int bits, String creator, long updateTime, Object data);

	/**
	 * Table category: DBVFS
	 * 
	 * @param filename
	 */
	public void DBDeleteVFSFile(String filename);

	/**
	 * Table category: DBBACKLOG
	 * 
	 * @param channelName
	 * @param entry
	 */
	public void addBackLogEntry(String channelName, final String entry);

	/**
	 * Table category: DBBACKLOG
	 * 
	 * @param channelName
	 * @param newestToSkip
	 * @param numToReturn
	 * @return
	 */
	public List<Pair<String,Long>> getBackLogEntries(String channelName, final int newestToSkip, final int numToReturn);

	/**
	 * Table category: DBBACKLOG
	 * 
	 * @param channels
	 * @param maxMessages
	 * @param oldestTime
	 */
	public void trimBackLogEntries(final String[] channels, final int maxMessages, final long oldestTime);

	/**
	 * Table category: DBPLAYERDATA
	 * 
	 * @author Bo Zimmerman
	 *
	 */
	public static interface PlayerData
	{

		/**
		 * 
		 * @return
		 */
		public String who();

		/**
		 * 
		 * @param who
		 * @return
		 */
		public PlayerData who(String who);

		/**
		 * 
		 * @return
		 */
		public String section();

		/**
		 * 
		 * @param section
		 * @return
		 */
		public PlayerData section(String section);

		/**
		 * 
		 * @return
		 */
		public String key();

		/**
		 * 
		 * @param key
		 * @return
		 */
		public PlayerData key(String key);

		/**
		 * 
		 * @return
		 */
		public String xml();

		/**
		 * 
		 * @param xml
		 * @return
		 */
		public PlayerData xml(String xml);
	}

	/**
	 * Table category: DBPOLLS
	 * 
	 * @author Bo Zimmerman
	 *
	 */
	public static interface PollData
	{

		/**
		 * 
		 * @return
		 */
		public String name();

		/**
		 * 
		 * @return
		 */
		public long flag();

		/**
		 * 
		 * @return
		 */
		public String byName();

		/**
		 * 
		 * @return
		 */
		public String subject();

		/**
		 * 
		 * @return
		 */
		public String description();

		/**
		 * 
		 * @return
		 */
		public String options();

		/**
		 * 
		 * @return
		 */
		public String qual();

		/**
		 * 
		 * @return
		 */
		public String results();

		/**
		 * 
		 * @return
		 */
		public long expiration();
	}

	/**
	 * Table category: DBRACE, DBCHARCLASS, DBABILITY
	 * 
	 * @author Bo Zimmerman
	 *
	 */
	public static interface AckRecord
	{

		/**
		 * 
		 * @return
		 */
		public String ID();

		/**
		 * 
		 * @return
		 */
		public String data();

		/**
		 * 
		 * @return
		 */
		public String typeClass();
	}

}
