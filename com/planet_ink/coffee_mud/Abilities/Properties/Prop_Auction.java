package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
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
public class Prop_Auction extends Property
{
	public String ID() { return "Prop_Auction"; }
	public String name(){ return "Auction Ticker";}
	protected int canAffectCode(){return 0;}
	public String accountForYourself(){ return "";	}
	public Environmental auctioning=null;
	public MOB highBidder=null;
	public int highBid=Integer.MIN_VALUE;
	public int bid=Integer.MIN_VALUE;
	public int state=-1;
	public long tickDown=0;
	public long auctionStart=0;

	public static final int STATE_START=0;
	public static final int STATE_RUNOUT=1;
	public static final int STATE_ONCE=2;
	public static final int STATE_TWICE=3;
	public static final int STATE_THREE=4;
	public static final int STATE_CLOSED=5;

	public void setAbilityCode(int code)
	{
		state=code;
		tickDown=15000/MudHost.TICK_TIME;
	}

	private MOB invoker=null;
	public MOB invoker(){return invoker;}
	public void setInvoker(MOB mob)
	{
		invoker=mob;
	}


	public boolean tick(Tickable ticking, int tickID)
	{
		if((--tickDown)<=0)
		{
			if((state==STATE_START)&&((System.currentTimeMillis()-auctionStart)<(5*15000)))
			{
				if(((System.currentTimeMillis()-auctionStart)>(3*15000))
				&&((highBidder==null)||(highBidder==invoker)))
					setAbilityCode(STATE_RUNOUT);
				else
					setAbilityCode(STATE_START);
				return true;
			}
			setAbilityCode(state+1);
			Vector V=new Vector();
			V.addElement("AUCTION");
			V.addElement("CHANNEL");
			MOB M=invoker();
			switch(state)
			{
			case STATE_RUNOUT:
				V.addElement("The auction for "+auctioning.name()+" is almost done. The current bid is "+bid+".");
				break;
			case STATE_ONCE:
				V.addElement(bid+" gold for "+auctioning.name()+" going ONCE!");
				break;
			case STATE_TWICE:
				V.addElement(bid+" gold for "+auctioning.name()+" going TWICE!");
				break;
			case STATE_THREE:
				V.addElement(auctioning.name()+" going for "+bid+" gold! Last chance!");
				break;
			case STATE_CLOSED:
				{
					if((highBidder!=null)&&(highBidder!=invoker()))
					{
						V.addElement(auctioning.name()+" SOLD to "+highBidder.name()+" for "+bid+" gold.");
						M.doCommand(V);
						if(MoneyUtils.totalMoney(highBidder)<bid)
						{
							highBidder.tell("You can no longer cover your bid.  Please contact "+M.name()+" about this matter immediately.");
							M.tell(highBidder.name()+" can not cover the bid any longer! Please contact "+highBidder.charStats().himher()+" immediately.");
						}
						else
						{
							MoneyUtils.subtractMoney(highBidder,highBidder,bid);
							MoneyUtils.giveMoney(M,M,bid);
							if((auctioning instanceof Item)
							   &&(Sense.isInTheGame(highBidder,true)))
							{
								((Item)auctioning).unWear();
								highBidder.location().bringItemHere((Item)auctioning,Item.REFUSE_PLAYER_DROP);
								CommonMsgs.get(highBidder,null,(Item)auctioning,false);
								M.tell(bid+" gold has been transferred to you as payment from "+highBidder.name()+".  The goods have also been transferred in exchange.");
								highBidder.tell(bid+" gold has been transferred to "+M.name()+".  You should have received the auctioned goods.  This auction is complete.");
							}
							else
							{
								M.tell(bid+" gold has been transferred to you as payment from "+highBidder.name()+".  Please contact "+highBidder.name()+" about receipt of "+auctioning.name()+".");
								highBidder.tell(bid+" gold has been transferred to "+M.name()+".  Please contact "+M.name()+" about receipt of "+auctioning.name()+".");
							}
						}
					}
					if(M!=null)
						M.doCommand(Util.parse("AUCTION CLOSE"));
					setInvoker(null);
				}
				return false;
			}
			M.doCommand(V);
		}
		return true;
	}

	public boolean invoke(MOB mob, Vector commands, Environmental target, boolean auto)
	{
		Vector V=new Vector();
		V.addElement("AUCTION");
		V.addElement("CHANNEL");
		if(target!=null)
		{
			setInvoker(mob);
			auctioning=target;
			bid=Util.s_int(Util.combine(commands,0));
			highBid=bid-1;
			auctionStart=System.currentTimeMillis();
			setAbilityCode(STATE_START);
			CMClass.ThreadEngine().startTickDown(this,MudHost.TICK_QUEST,1);
			V.addElement("New lot: "+auctioning.name()+".  The opening bid is "+bid+".");
		}
		else
		{
			if(state>0)	setAbilityCode(STATE_RUNOUT);
			int b=0;
			String sb="";
			if(commands!=null){ sb=Util.combine(commands,0); b=Util.s_int(sb);}
			if(sb.length()==0)
			{
				mob.tell("Up for auction: "+auctioning.name()+".  The current bid is "+bid+".");
				return true;
			}

			if(b>MoneyUtils.totalMoney(mob))
			{
				mob.tell("You don't have enough total money on hand to cover that bid.");
				return false;
			}

			if(b>highBid)
			{
				if((highBidder!=null)&&(highBidder!=mob))
				{
					highBidder.tell("You have been outbid for "+auctioning.name()+".");
					mob.tell("You have the high bid for "+auctioning.name()+".");
				}

				highBidder=mob;
				if(highBid<0) highBid=0;
				bid=highBid+1;
				highBid=b;
			}
			else
			if((b<bid)||(b==0))
			{
				mob.tell("Your bid of "+b+" is insufficient."+((bid>0)?" The current high bid is "+bid+".":""));
				return false;
			}
			else
			if((b==bid)&&(highBidder!=null))
			{
				mob.tell("You must bid higher than "+bid+" to have your bid accepted.");
				return false;
			}
			else
			if((b==highBid)&&(highBidder!=null))
			{
				if((highBidder!=null)&&(highBidder!=mob))
				{
					mob.tell("You have been outbid by proxy for "+auctioning.name()+".");
					highBidder.tell("Your high bid for "+auctioning.name()+" has been reached.");
				}
				bid=b;
			}
			else
			{
				bid=b;
				mob.tell("You have been outbid by proxy for "+auctioning.name()+".");
			}
			V.addElement("A new bid has been entered for "+auctioning.name()+". The current bid is "+bid+".");
		}
		if(invoker()!=null) invoker().doCommand(V);
		return true;
	}
}
