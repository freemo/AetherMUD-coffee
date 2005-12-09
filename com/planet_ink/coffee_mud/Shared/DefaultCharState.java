package com.planet_ink.coffee_mud.common;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.utils.*;

/* 
   Copyright 2000-2005 Bo Zimmerman

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
public class DefaultCharState implements Cloneable, CharState
{
	protected int HitPoints=10;
	protected int Mana=100;
	protected int Movement=50;
	protected int Hunger=1000;
	protected int Thirst=500;
	protected long Fatigue=0;

	protected int botherCycle=0;
	protected int ticksHungry=0;
	protected int ticksThirsty=0;

	protected int annoyanceTicker=ANNOYANCE_DEFAULT_TICKS;

	public DefaultCharState(){}
	public DefaultCharState(int def)
	{
		HitPoints=def;
		Mana=def;
		Movement=def;
		Hunger=def;
		Thirst=def;
		Fatigue=def;
	}


	public int getHitPoints(){return HitPoints;}
	public void setHitPoints(int newVal){HitPoints=newVal;}
	public boolean adjHitPoints(int byThisMuch, CharState max)
	{
		HitPoints+=byThisMuch;
		if(HitPoints<1)
		{
			HitPoints=0;
			return false;
		}
		if(HitPoints>max.getHitPoints())
		{
			HitPoints=max.getHitPoints();
			return false;
		}
		return true;
	}
	public long getFatigue(){return Fatigue;}
	public void setFatigue(long newVal){Fatigue=newVal;}
	public boolean adjFatigue(long byThisMuch, CharState max)
	{
		Fatigue+=byThisMuch;
		if(Fatigue<1)
		{
			Fatigue=0;
			return false;
		}
		return true;
	}
	public int getHunger(){return Hunger;}
	public void setHunger(int newVal){Hunger=newVal; if(Hunger>0)ticksHungry=0;}
	public boolean adjHunger(int byThisMuch, int max)
	{
	    if((byThisMuch>0)&&(Hunger==Integer.MAX_VALUE))
	        return false;
		Hunger+=byThisMuch;
		if(Hunger<0)
		{
			Hunger=0;
			return false;
		}
		if(Hunger>0) ticksHungry=0;
		if(Hunger>max)
		{
			Hunger=max;
			return false;
		}
		return true;
	}
	public int maxHunger(int baseWeight)
	{
	    long factor=baseWeight/250;
	    if(factor==0) factor=1;
	    factor*=getHunger();
	    if(factor>Integer.MAX_VALUE)
	        return Integer.MAX_VALUE;
        return (int)factor;
	}
	public int getThirst(){return Thirst;}
	public void setThirst(int newVal){Thirst=newVal; if(Thirst>0) ticksThirsty=0;}
	public boolean adjThirst(int byThisMuch, int max)
	{
	    if((byThisMuch>0)&&(Thirst==Integer.MAX_VALUE))
	        return false;
		Thirst+=byThisMuch;
		if(Thirst<0)
		{
			Thirst=0;
			return false;
		}
		if(Thirst>0) ticksThirsty=0;
		if(Thirst>max)
		{
			Thirst=max;
			return false;
		}
		return true;
	}
	public int maxThirst(int baseWeight)
	{
	    long factor=baseWeight/250;
	    if(factor==0) factor=1;
	    factor*=getThirst();
	    if(factor>Integer.MAX_VALUE)
	        return Integer.MAX_VALUE;
        return (int)factor;
	}
	
	public int getMana(){return Mana;}
	public void setMana(int newVal){ Mana=newVal;}
	public boolean adjMana(int byThisMuch, CharState max)
	{
		Mana+=byThisMuch;
		if(Mana<0)
		{
			Mana=0;
			return false;
		}
		if(Mana>max.getMana())
		{
			Mana=max.getMana();
			return false;
		}
		return true;
	}
	public int getMovement(){return Movement;}
	public void setMovement(int newVal){ Movement=newVal;}
	public boolean adjMovement(int byThisMuch, CharState max)
	{
		Movement+=byThisMuch;
		if(Movement<0)
		{
			Movement=0;
			return false;
		}
		if(Movement>max.getMovement())
		{
			Movement=max.getMovement();
			return false;
		}
		return true;
	}

	public void recoverTick(MOB mob, CharState maxState)
	{
		if(++botherCycle<ADJUST_FACTOR)
			return;

		botherCycle=0;

		double con=new Integer(mob.charStats().getStat(CharStats.CONSTITUTION)).doubleValue();
		double man=new Integer((mob.charStats().getStat(CharStats.INTELLIGENCE)+mob.charStats().getStat(CharStats.WISDOM))).doubleValue();
		double str=new Integer(mob.charStats().getStat(CharStats.STRENGTH)).doubleValue();
		if(getHunger()<1)
		{
			con=con*.85;
			man=man*.75;
			str=str*.85;
		}
		if(getThirst()<1)
		{
			con=con*.85;
			man=man*.75;
			str=str*.85;
		}
		if(getFatigue()>FATIGUED_MILLIS)
			man=man*.5;

		double lvl=new Integer(mob.envStats().level()).doubleValue();
		double lvlby1p5=Util.div(lvl,1.5);
		//double lvlby2=Util.div(lvl,2.0);
		//double lvlby3=Util.div(lvl,3.0);

		double hpGain=(con>1.0)?((con/40.0)*lvlby1p5)+(con/4.5)+2.0:1.0;
		double manaGain=(man>2.0)?((man/80.0)*lvl)+(man/4.5)+2.0:1.0;
		double moveGain=(str>1.0)?((str/40.0)*lvl)+(str/3.0)+5.0:1.0;

		if(Sense.isSleeping(mob))
		{
			hpGain+=(hpGain/2.0);
			manaGain+=(manaGain/2.0);
			moveGain+=(moveGain/2.0);
			if((mob.riding()!=null)&&(mob.riding() instanceof Item))
			{
				hpGain+=(hpGain/8.0);
				manaGain+=(manaGain/8.0);
				moveGain+=(moveGain/8.0);
			}
		}
		else
		if((Sense.isSitting(mob))||(mob.riding()!=null))
		{
			hpGain+=(hpGain/4.0);
			manaGain+=(manaGain/4.0);
			moveGain+=(moveGain/4.0);
			if((mob.riding()!=null)&&(mob.riding() instanceof Item))
			{
				hpGain+=(hpGain/8.0);
				manaGain+=(manaGain/8.0);
				moveGain+=(moveGain/8.0);
			}
		}
		else
		{
			if(Sense.isFlying(mob))
				moveGain+=(moveGain/8.0);
			else
			if(Sense.isSwimming(mob))
			{
				hpGain-=(hpGain/2.0);
				manaGain-=(manaGain/4.0);
				moveGain-=(moveGain/2.0);
			}
		}

		if((!mob.isInCombat())
		&&(!Sense.isClimbing(mob)))
		{
			if((hpGain>0)&&(!Sense.isGolem(mob)))
				adjHitPoints((int)Math.round(hpGain),maxState);
			if(manaGain>0)
				adjMana((int)Math.round(manaGain),maxState);
			if(moveGain>0)
				adjMovement((int)Math.round(moveGain),maxState);
		}
	}

	public void expendEnergy(MOB mob, CharState maxState, boolean expendMovement)
	{
		if(mob.location()!=null)
		{
			if(expendMovement)
			{
				int move=-mob.location().pointsPerMove(mob);
				if((mob.movesSinceLastTick()>4)
				&&(mob.riding()==null)
				&&(!Sense.isInFlight(mob)))
					move=move-(mob.movesSinceLastTick()-4);
				if(mob.envStats().weight()>mob.maxCarry())
					move+=(int)Math.round(Util.mul(move,10.0*Util.div(mob.envStats().weight()-mob.maxCarry(),mob.maxCarry())));
				adjMovement(move,maxState);
			}
			if((!Sense.isSleeping(mob))
			&&(!CMSecurity.isAllowed(mob,mob.location(),"IMMORT")))
			{
				int factor=mob.baseWeight()/500;
				if(factor<1) factor=1;
				adjThirst(-(mob.location().thirstPerRound(mob)*factor),maxState.maxThirst(mob.baseWeight()));
				adjHunger(-factor,maxState.maxHunger(mob.baseWeight()));
			}
			boolean thirsty=(getThirst()<=0);
			boolean hungry=(getHunger()<=0);
			if((hungry||thirsty)&&(!expendMovement))
			{
				if(thirsty)ticksThirsty++;
				if(hungry)ticksHungry++;

				if((ticksThirsty>DEATH_THIRST_TICKS)
				||(ticksHungry>DEATH_HUNGER_TICKS))
				{
					if(thirsty)
						mob.tell("YOU ARE DYING OF THIRST!");
					if(hungry)
						mob.tell("YOU ARE DYING OF HUNGER!");
					MUDFight.postDeath(null,mob,null);
				}
				else
				if(ticksThirsty>DEATH_THIRST_TICKS-30)
					mob.tell("You are dehydrated, and near death.  DRINK SOMETHING!");
				else
				if(ticksHungry>DEATH_HUNGER_TICKS-30)
					mob.tell("You are starved, and near death.  EAT SOMETHING!");
				else
				if((--annoyanceTicker)<=0)
				{
					annoyanceTicker=ANNOYANCE_DEFAULT_TICKS;

					if(thirsty)
					{
						if(ticksThirsty>((DEATH_THIRST_TICKS/2)+(DEATH_THIRST_TICKS/4)))
							mob.tell("You are dehydrated! Drink something!");
						else
						if(ticksThirsty>(DEATH_THIRST_TICKS/2))
							mob.tell("You are parched! Drink something!");
						else
							mob.tell("You are thirsty.");
					}
					if(hungry)
					{
						if(ticksHungry>((DEATH_HUNGER_TICKS/2)+(DEATH_HUNGER_TICKS/4)))
							mob.tell("You are starved! Eat something!");
						else
						if(ticksHungry>(DEATH_HUNGER_TICKS/2))
							mob.tell("You are famished! Eat something!");
						else
							mob.tell("You are hungry.");
					}
				}
			}
		}
	}

	private final static String[] CODES={
		"HITS","MANA","MOVE",
		"HUNGER","THIRST","FATIGUE"};
	public String[] getCodes(){return CODES;}
	private int getCodeNum(String code)
	{
		for(int i=0;i<CODES.length;i++)
			if(code.equalsIgnoreCase(CODES[i])) return i;
		return -1;
	}
	public boolean sameAs(EnvStats E){
		for(int i=0;i<CODES.length;i++)
			if(!E.getStat(CODES[i]).equals(getStat(CODES[i])))
			   return false;
		return true;
	}

	public void setStat(String code, String val)
	{
		switch(getCodeNum(code)){
		case 0: setHitPoints(Util.s_int(val)); break;
		case 1: setMana(Util.s_int(val)); break;
		case 2: setMovement(Util.s_int(val)); break;
		case 3: setHunger(Util.s_int(val)); break;
		case 4: setThirst(Util.s_int(val)); break;
		case 5: setFatigue(Util.s_int(val)); break;
		}
	}
	public String getStat(String code)
	{
		switch(getCodeNum(code)){
		case 0: return ""+getHitPoints();
		case 1: return ""+getMana();
		case 2: return ""+getMovement();
		case 3: return ""+getHunger();
		case 4: return ""+getThirst();
		case 5: return ""+getFatigue();
		default: return "";
		}
	}

	// create a new one of these
	public CharState cloneCharState()
	{
		try
		{
			return (CharState) this.clone();
		}
		catch(CloneNotSupportedException e)
		{
			return new DefaultCharState();
		}
	}
}
