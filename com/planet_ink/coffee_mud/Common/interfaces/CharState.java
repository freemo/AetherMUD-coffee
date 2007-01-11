package com.planet_ink.coffee_mud.Common.interfaces;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
/* 
   Copyright 2000-2007 Bo Zimmerman

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
 * An object to access and change fields representing the most variable aspects of a MOB
 * @see com.planet_ink.coffee_mud.MOBS.interfaces.MOB
 * @author Bo Zimmerman
 *
 */
public interface CharState extends CMCommon
{
    public final static int STAT_HITPOINTS=0;
    public final static int STAT_MANA=1;
    public final static int STAT_MOVE=2;
    public final static int STAT_HUNGER=3;
    public final static int STAT_THIRST=4;
    public final static int STAT_NUMSTATS=5;
    public final static int STAT_NUM_BASE_STATS=5;
    
    /** constant representing how many ticks between hunger/thirst messages*/
	public final static int ANNOYANCE_DEFAULT_TICKS=60;
    /** constant representing something*/
	public final static int ADJUST_FACTOR=5;
    /** constant representing how many ticks a MOB can  be thirsty before death */
	public final static int DEATH_THIRST_TICKS=(30*30)*6; // 6 hours
    /** constant representing how many ticks a MOB can  be hungry before death */
	public final static int DEATH_HUNGER_TICKS=(30*30)*12; // 12 hours
    /** constant for how many fatigue points are lost per tick of rest */
	public final static long REST_PER_TICK=Tickable.TIME_TICK*200;
    /** constant for how many fatigue points are required to be considered fatigued */
	public final static long FATIGUED_MILLIS=Tickable.TIME_TICK*3000;

	/**
     * Get primary combat stats as displayable code string
     * @return primary combat stats as displayable code string
     */
	public String getCombatStats();
	
   /**
     * Get the number of fatigue points for the player
     * @return number of fatigue points
     */
	public long getFatigue();
    /**
     * Set the number of fatigue points
     * @param newVal number of fatigue points
     */
	public void setFatigue(long newVal);
    /**
     * Set the number of fatigue points, respecting boundaries. 0 is always lowest.
     * @param byThisMuch a positive or negative change in value
     * @param max the highest amount to allow the fatigue number to reach
     * @return whether the highest or lowest boundary was reached
     */
	public boolean adjFatigue(long byThisMuch, CharState max);

    /**
     * Get the number of hit points for the player
     * @return number of hit points
     */
	public int getHitPoints();
    /**
     * Set the number of hit points
     * @param newVal number of hit points
     */
	public void setHitPoints(int newVal);
    /**
     * Set the number of hit points, respecting boundaries. 0 is always lowest.
     * @param byThisMuch a positive or negative change in value
     * @param max the highest amount to allow the hit points number to reach
     * @return whether the highest or lowest boundary was reached
     */
	public boolean adjHitPoints(int byThisMuch, CharState max);

    /**
     * Get the number of hunger points for the player
     * @return number of hunger points
     */
	public int getHunger();
    /**
     * Set the number of hunger points
     * @param newVal number of hunger points
     */
	public void setHunger(int newVal);
    /**
     * Set the number of hunger points, respecting boundaries. 0 is always lowest.
     * @param byThisMuch a positive or negative change in value
     * @param maxHunger the highest amount to allow the hunger number to reach
     * @return whether the highest or lowest boundary was reached
     */
	public boolean adjHunger(int byThisMuch, int maxHunger);
    /**
     * This method is used to recalculate the maximum thirhungerst for a mob, based
     * on their weight and the default maximum hunger
     * @param baseWeight the base weight of  the mob
     * @return the new maximum hunger to set
     */
	public int maxHunger(int baseWeight);

    /**
     * Get the number of thirst points for the player
     * @return number of thirst points
     */
	public int getThirst();
    /**
     * Set the number of thirst points
     * @param newVal number of thirst points
     */
	public void setThirst(int newVal);
    /**
     * Set the number of thirst points, respecting boundaries. 0 is always lowest.
     * @param byThisMuch a positive or negative change in value
     * @param maxThirst the highest amount to allow the thirst number to reach
     * @return whether the highest or lowest boundary was reached
     */
	public boolean adjThirst(int byThisMuch, int maxThirst);
    /**
     * This method is used to recalculate the maximum thirst for a mob, based
     * on their weight and the default maximum thirst
     * @param baseWeight the base weight of  the mob
     * @return the new maximum thirst to set
     */
	public int maxThirst(int baseWeight);

    /**
     * Get the number of mana points for the player
     * @return number of mana points
     */
	public int getMana();
    /**
     * Set the number of mana points
     * @param newVal number of mana points
     */
	public void setMana(int newVal);
    /**
     * Set the number of mana points, respecting boundaries. 0 is always lowest.
     * @param byThisMuch a positive or negative change in value
     * @param max the highest amount to allow the mana number to reach
     * @return whether the highest or lowest boundary was reached
     */
	public boolean adjMana(int byThisMuch, CharState max);

    /**
     * Get the number of movement points for the player
     * @return number of movement points
     */
	public int getMovement();
    /**
     * Set the number of movement points
     * @param newVal number of movement points
     */
	public void setMovement(int newVal);
    /**
     * Set the number of movement points, respecting boundaries. 0 is always lowest.
     * @param byThisMuch a positive or negative change in value
     * @param max the highest amount to allow the movement number to reach
     * @return whether the highest or lowest boundary was reached
     */
	public boolean adjMovement(int byThisMuch, CharState max);

    /**
     * During rest, and even standing still, this method will be called to allow the
     * fields in this object to recover some of their value.  It is called by the
     * mob tick(Tickable,int) method every Tickable.TIME_TICK milliseconds
     * @see com.planet_ink.coffee_mud.MOBS.interfaces.MOB
     * @param mob the mob recovering
     * @param maxState The CharState objects which represents the maximum values for these fields
     */
	public void recoverTick(MOB mob, CharState maxState);
    /**
     * During movement and combat, this method will be called to allow the
     * movement to be expended and hunger/thirst to occur.  It is called by methods executing
     * the appropriate events when they are performed and by the mob tick(Tickable,int) method
     * @see com.planet_ink.coffee_mud.MOBS.interfaces.MOB
     * @param mob the mob expending
     * @param maxState The CharState objects which represents the maximum values for these fields
     * @param expendMovement whether to skip movement expending
     */
	public void expendEnergy(MOB mob, CharState maxState, boolean expendMovement);

	/**
     * Sets all the values in this object to a single given value
     * @param def the value to give to all
	 */
    public void setAllValues(int def);
    
	/**
     * Copies the internal data of this object into another of kind.
     * @param intoState another CharState object.
	 */
    public void copyInto(CharState intoState);
    
    /** 
     * Returns an array of the string names of those fields which are modifiable on this object at run-time by
     * builders.
     * @see #getStat(String)
     * @see #setStat(String, String)
     * @return list of the fields which may be set.
     */
    public String[] getStatCodes();
    /**
     * An alternative means of retreiving the values of those fields on this object which are modifiable at 
     * run-time by builders.  See getStatCodes() for possible values for the code passed to this method.  
     * Values returned are always strings, even if the field itself is numeric or a list.
     * @see #getStatCodes()
     * @param code the name of the field to read.
     * @return the value of the field read
     */
    public String getStat(String code);
    /**
     * An alternative means of setting the values of those fields on this object which are modifiable at 
     * run-time by builders.  See getStatCodes() for possible values for the code passed to this method.  
     * The value passed in is always a string, even if the field itself is numeric or a list.
     * @see #getStatCodes()
     * @param code the name of the field to set
     * @param val the value to set the field to
     */
    public void setStat(String code, String val);
    /**
     * Whether this object instance is functionally identical to the object passed in.  Works by repeatedly
     * calling getStat on both objects and comparing the values.
     * @see #getStatCodes()
     * @see #getStat(String)
     * @param E the object to compare this one to
     * @return whether this object is the same as the one passed in
     */
    public boolean sameAs(CharState E);
}
