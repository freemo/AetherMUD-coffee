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
package com.syncleus.aethermud.game.Common.interfaces;

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;


/**
 * A LegalWarrant is an important object in the triad of LegalBehavior
 * and Law objects, as a part of the CoffeeMud legal system.  A LegalWarrant
 * is created by the LegalBehavior in response to the Law definitions, when
 * it has determined that a mob or player has broken one of the Laws.  The
 * Warrant is then used as a state object following the specific circumstances
 * around the violated law, and its adjudication.  After adjudication, the
 * object is transferred into an Old Warrants cue under the Law to use as
 * a reference for repeat offenders.
 *
 * @see com.syncleus.aethermud.game.Behaviors.interfaces.LegalBehavior
 * @see com.syncleus.aethermud.game.Common.interfaces.Law
 */
public interface LegalWarrant extends CMCommon {
    /**
     * If the criminal has been identified by an otherwise unoccupied
     * officer, this will be that officer.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setArrestingOfficer(Area, MOB)
     *
     * @return the actual living mob officer who will enforce the warrant
     */
    public MOB arrestingOfficer();

    /**
     * If the criminal has been identified by an otherwise unoccupied
     * officer, this will set that officer.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#arrestingOfficer()
     *
     * @param legalArea the Area governing the law
     * @param mob the actual living mob officer who will enforce the warrant
     */
    public void setArrestingOfficer(Area legalArea, MOB mob);

    /**
     * The actual player/mob criminal accused of the crime.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setCriminal(MOB)
     *
     * @return the actual player/mob criminal accused of the crime.
     */
    public MOB criminal();

    /**
     * Sets the actual player/mob criminal accused of the crime.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#criminal()
     *
     * @param mob the actual player/mob criminal accused of the crime.
     */
    public void setCriminal(MOB mob);

    /**
     * If applicable, the victim of the crime (or null)
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setVictim(MOB)
     *
     * @return the victim of the crime, or null
     */
    public MOB victim();

    /**
     * If applicable, sets the victim of the crime (or null)
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#victim()
     *
     * @param mob the victim of the crime, or null
     */
    public void setVictim(MOB mob);

    /**
     * If applicable, gets the witness of the crime (or null)
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setWitness(MOB)
     *
     * @return the witness of the crime or null
     */
    public MOB witness();

    /**
     * If applicable, sets the witness of the crime (or null)
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#witness()
     *
     * @param mob the witness of the crime (or null)
     */
    public void setWitness(MOB mob);

    /**
     * The crime name
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setCrime(String)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#BIT_CRIMENAME
     *
     * @return the crime name
     */
    public String crime();

    /**
     * Sets the crime name
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#crime()
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#BIT_CRIMENAME
     *
     * @param crime the crime name
     */
    public void setCrime(String crime);

    /**
     * Gets the full punishment code for the crime
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setPunishment(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#getPunishmentParm(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#addPunishmentParm(int, String)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#PUNISHMENT_DESCS
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#PUNISHMENTMASK_DESCS
     *
     * @return the full punishment code for the crime
     */
    public int punishment();

    /**
     * Sets the full punishment code for the crime
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#punishment()
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#getPunishmentParm(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#addPunishmentParm(int, String)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#PUNISHMENT_DESCS
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#PUNISHMENTMASK_DESCS
     *
     * @param code the full punishment code for the crime
     */
    public void setPunishment(int code);

    /**
     * If applicable, returns any parameters associated with a particular
     * bitmask on the punishment.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#addPunishmentParm(int, String)
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#punishment()
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setPunishment(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#PUNISHMENTMASK_DESCS
     *
     * @param code the punishment mask bitmap code
     * @return any parameters needed, or null
     */
    public String getPunishmentParm(int code);

    /**
     * If applicable, sets any parameters associated with a particular
     * bitmask on the punishment.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#getPunishmentParm(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#punishment()
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setPunishment(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#PUNISHMENTMASK_DESCS
     *
     * @param code the punishment mask bitmap code
     * @param parm the parameter to set
     */
    public void addPunishmentParm(int code, String parm);

    /**
     * If applicable, returns the number of ticks the punishment calls
     * for the criminal to remain in jail.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setJailTime(int)
     *
     * @return the number of ticks to be jailed for
     */
    public int jailTime();

    /**
     * If applicable, sets the number of ticks the punishment calls
     * for the criminal to remain in jail.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#jailTime()
     *
     * @param time the number of ticks to be jailed for
     */
    public void setJailTime(int time);

    /**
     * Returns the current state of adjudication.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setState(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#STATE_ARRESTING
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#STATE_SEEKING
     *
     * @return the current state of adjudication
     */
    public int state();

    /**
     * Sets the current state of adjudication.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#state()
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#getLastStateChangeTime()
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#STATE_DETAINING
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#STATE_EXECUTING
     *
     * @param state the current state of adjudication.
     */
    public void setState(int state);

    /**
     * Returns the number of times this criminal has done this crime.  This
     * is usually 0 unless it also exists in the old warrants list.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setOffenses(int)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#oldWarrants()
     *
     * @return the number of times this criminal has committed this crime.
     */
    public int offenses();

    /**
     * Sets the number of times this criminal has done this crime.  This
     * is usually 0 unless it also exists in the old warrants list.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#offenses()
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#oldWarrants()
     *
     * @param num the number of times this criminal has committed this crime.
     */
    public void setOffenses(int num);

    /**
     * Returns the last time, in miliseconds since 1970, the criminal has
     * committed this crime.  Very useful in oldWarrants
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setLastOffense(long)
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#oldWarrants()
     *
     * @return the last time this crime was done, in milis
     */
    public long lastOffense();

    /**
     * Sets the last time, in miliseconds since 1970, the criminal has
     * committed this crime.  Very useful in oldWarrants
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#lastOffense()
     * @see com.syncleus.aethermud.game.Common.interfaces.Law#oldWarrants()
     *
     * @param last the last time this crime was done, in milis
     */
    public void setLastOffense(long last);

    /**
     * Returns the time, in milis since 1970, that a trip to the judge
     * or to the jail was started.  For housekeeping purposes.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setTravelAttemptTime(long)
     *
     * @return the milis time since the trip began
     */
    public long travelAttemptTime();

    /**
     * Sets the time, in milis since 1970, that a trip to the judge
     * or to the jail was started.  For housekeeping purposes.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#travelAttemptTime()
     *
     * @param time the milis time since the trip began
     */
    public void setTravelAttemptTime(long time);

    /**
     * Returns the warning message given to the criminal by the officer.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setWarnMsg(String)
     *
     * @return the warning msg given
     */
    public String warnMsg();

    /**
     * Sets the warning message given to the criminal by the officer.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#warnMsg()
     *
     * @param msg the warning msg given
     */
    public void setWarnMsg(String msg);

    /**
     * Sets the room into which this criminal will be jailed.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#jail()
     *
     * @param R the room into which this criminal will be jailed.
     */
    public void setJail(Room R);

    /**
     * Returns the room into which this criminal will be jailed.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setJail(Room)
     *
     * @return the room into which this criminal will be jailed.
     */
    public Room jail();

    /**
     * Returns the room into which this criminal will be released after jail.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setReleaseRoom(Room)
     *
     * @return the room into which this criminal will be released after jail.
     */
    public Room releaseRoom();

    /**
     * Sets the room into which this criminal will be released after jail.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#releaseRoom()
     *
     * @param R the room into which this criminal will be released after jail.
     */
    public void setReleaseRoom(Room R);

    /**
     * Returns the real time in ms when the state last changed.
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setState(int)
     *
     * @return the real time in ms when the state last changed.
     */
    public long getLastStateChangeTime();

    /**
     * Returns the real time in ms when the warrant can no longer be ignored
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#setIgnoreUntilTime(long)
     *
     * @return the real time in ms when the warrant can no longer be ignored
     */
    public long getIgnoreUntilTime();

    /**
     * Sets the real time in ms when the warrant can no longer be ignored
     *
     * @see com.syncleus.aethermud.game.Common.interfaces.LegalWarrant#getIgnoreUntilTime()
     *
     * @param time the real time in ms when the warrant can no longer be ignored
     */
    public void setIgnoreUntilTime(long time);
}
