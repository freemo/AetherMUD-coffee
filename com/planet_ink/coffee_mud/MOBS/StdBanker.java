package com.planet_ink.coffee_mud.MOBS;
import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

public class StdBanker extends StdShopKeeper implements Banker
{
	public String ID(){return "StdBanker";}

	protected double coinInterest=-0.008;
	protected double itemInterest=-0.001;
	protected static final Integer allDown=new Integer(new Long(Area.A_FULL_DAY*Host.TIME_TICK_DELAY*5).intValue());
	protected static Hashtable bankTimes=new Hashtable();

	public StdBanker()
	{
		super();
		Username="a banker";
		setDescription("He\\`s pleased to be of assistance.");
		setDisplayText("A banker is waiting to serve you.");
		setAlignment(1000);
		setMoney(0);
		whatISell=ShopKeeper.DEAL_BANKER;
		baseEnvStats.setWeight(150);
		setWimpHitPoint(0);

		baseCharStats().setStat(CharStats.INTELLIGENCE,16);
		baseCharStats().setStat(CharStats.CHARISMA,25);

		baseEnvStats().setArmor(0);

		baseState.setHitPoints(1000);

		recoverMaxState();
		resetToMaxState();
		recoverEnvStats();
		recoverCharStats();
	}

	public Environmental newInstance()
	{
		return new StdBanker();
	}

	public int whatIsSold(){return whatISell;}
	public void setWhatIsSold(int newSellCode){
		if(newSellCode!=ShopKeeper.DEAL_CLANBANKER)
			whatISell=ShopKeeper.DEAL_BANKER;
		else
			whatISell=ShopKeeper.DEAL_CLANBANKER;
	}
	public String prejudiceFactors(){return "";}
	public void setPrejudiceFactors(String factors){}

	public String bankChain(){return text();}
	public void setBankChain(String name){setMiscText(name);}

	public void addDepositInventory(String mob, Item thisThang)
	{
		String name=thisThang.ID();
		if(thisThang instanceof Coins) name="COINS";
		ExternalPlay.DBCreateData(mob,bankChain(),""+thisThang+Math.random(),name+";"+Generic.getPropertiesStr(thisThang,true));
	}
	
	protected Item makeItem(String data)
	{
		int x=data.indexOf(";");
		if(x<0) return null;
		Item I=null;
		if(data.substring(0,x).equals("COINS"))
			I=CMClass.getItem("StdCoins");
		else 
			I=CMClass.getItem(data.substring(0,x));
		if(I!=null)
		{
			Generic.setPropertiesStr(I,data.substring(x+1),true);
			I.recoverEnvStats();
			I.text();
			return I;
		}
		return null;
	}
	
	public void delDepositInventory(String mob, Item thisThang)
	{
		Vector V=getDepositInventory(mob);
		boolean money=thisThang instanceof Coins;
		for(int v=V.size()-1;v>=0;v--)
		{
			Vector V2=(Vector)V.elementAt(v);
			if(money&&((String)V2.elementAt(DATA_DATA)).startsWith("COINS;"))
			{
				ExternalPlay.DBDeleteData(((String)V2.elementAt(DATA_USERID)),((String)V2.elementAt(DATA_BANK)),((String)V2.elementAt(DATA_KEY)));
				break;
			}
				
			Item I=makeItem((String)V2.elementAt(DATA_DATA));
			if(I==null) continue;
			if(thisThang.sameAs(I))
			{
				ExternalPlay.DBDeleteData(((String)V2.elementAt(DATA_USERID)),((String)V2.elementAt(DATA_BANK)),((String)V2.elementAt(DATA_KEY)));
				break;
			}
		}
	};
	public void delAllDeposits(String mob)
	{
		ExternalPlay.DBDeleteData(mob,bankChain());
	};
	public int numberDeposited(String mob)
	{
		return getDepositInventory(mob).size();
	};
	public Vector getDepositedItems(String mob)
	{
		Vector V=getDepositInventory(mob);
		Vector mine=new Vector();
		for(int v=0;v<V.size();v++)
		{
			Vector V2=(Vector)V.elementAt(v);
			Item I=makeItem((String)V2.elementAt(DATA_DATA));
			if(I!=null)	mine.addElement(I);
		}
		return mine;
	}
	public Vector getDepositInventory(String mob)
	{
		return ExternalPlay.DBReadData(mob,bankChain());
	};
	public Vector getAccountNames()
	{
		Vector V=ExternalPlay.DBReadData(bankChain());
		HashSet h=new HashSet();
		Vector mine=new Vector();
		for(int v=0;v<V.size();v++)
		{
			Vector V2=(Vector)V.elementAt(v);
			if(!h.contains((String)V2.elementAt(DATA_USERID)))
			{
				h.add((String)V2.elementAt(DATA_USERID));
				mine.addElement((String)V2.elementAt(DATA_USERID));
			}
		}
		return mine;
	}
	
	public Item findDepositInventory(String mob, String likeThis)
	{
		Vector V=getDepositInventory(mob);
		boolean money=Util.s_int(likeThis)>0;
		for(int v=0;v<V.size();v++)
		{
			Vector V2=(Vector)V.elementAt(v);
			if(money&&((String)V2.elementAt(DATA_DATA)).startsWith("COINS;"))
				return makeItem((String)V2.elementAt(DATA_DATA));
			
			Item I=makeItem((String)V2.elementAt(DATA_DATA));
			if(I==null) continue;
			if(CoffeeUtensils.containsString(I.Name(),likeThis))
				return I;
		}
		return null;
	};



	public void setCoinInterest(double interest){coinInterest=interest;};
	public void setItemInterest(double interest){itemInterest=interest;};
	public double getCoinInterest(){return coinInterest;};
	public double getItemInterest(){return itemInterest;};

	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		try{
		if(tickID==Host.MOB_TICK)
		{
			boolean proceed=false;
			// handle interest by watching the days go by...
			// put stuff up for sale if the account runs out
			synchronized(bankChain())
			{
				Long L=(Long)bankTimes.get(bankChain());
				if((L==null)||(L.longValue()<System.currentTimeMillis()))
				{
					L=new Long(System.currentTimeMillis()+allDown.intValue());
					proceed=true;
					bankTimes.remove(bankChain());
					bankTimes.put(bankChain(),L);
				}
				if(proceed)
				{
					Vector V=ExternalPlay.DBReadData(bankChain());
					Vector userNames=new Vector();
					for(int v=0;v<V.size();v++)
					{
						Vector V2=(Vector)V.elementAt(v);
						String name=(String)V2.elementAt(0);
						if(!userNames.contains(name))
						{
							if(!ExternalPlay.DBUserSearch(null,name))
							{
								if((Clans.getClan(name))==null)
									delAllDeposits(name);
								else
									userNames.addElement(name);
							}
							else
								userNames.addElement(name);
						}
					}
					for(int u=0;u<userNames.size();u++)
					{
						String name=(String)userNames.elementAt(u);
						Coins coinItem=null;
						int totalValue=0;
						V=getDepositedItems(name);
						for(int v=0;v<V.size();v++)
						{
							Item I=(Item)V.elementAt(v);
							if(I instanceof Coins)
								coinItem=(Coins)I;
							else
							if(itemInterest!=0.0)
								totalValue+=I.value();
						}
						int newBalance=0;
						if(coinItem!=null) newBalance=coinItem.numberOfCoins();
						newBalance+=(int)Math.round(Util.mul(newBalance,coinInterest));
						if(totalValue>0)
							newBalance+=(int)Math.round(Util.mul(totalValue,itemInterest));
						if(newBalance<0)
						{
							for(int v=0;v<V.size();v++)
							{
								Item I=(Item)V.elementAt(v);
								if(!(I instanceof Coins))
									addStoreInventory(I);
							}
							delAllDeposits(name);
						}
						else
						if((coinItem==null)||(newBalance!=coinItem.numberOfCoins()))
						{
							if(coinItem!=null)
								delDepositInventory(name,coinItem);
							coinItem=(Coins)CMClass.getItem("StdCoins");
							coinItem.setNumberOfCoins(newBalance);
							coinItem.recoverEnvStats();
							addDepositInventory(name,coinItem);
						}
					}
				}
			}
		}
		}catch(Exception e){Log.errOut("StdBanker",e);}
		return true;
	}

	protected int getBalance(MOB mob)
	{
		Item old=null;
		if(whatISell==ShopKeeper.DEAL_CLANBANKER)
			old=findDepositInventory(mob.getClanID(),""+Integer.MAX_VALUE);
		else
			old=findDepositInventory(mob.Name(),""+Integer.MAX_VALUE);
		if((old!=null)&&(old instanceof Coins))
			return ((Coins)old).numberOfCoins();
		return 0;
	}

	protected int minBalance(MOB mob)
	{
		Vector V=null;
		if(whatISell==ShopKeeper.DEAL_CLANBANKER)
			V=getDepositedItems(mob.getClanID());
		else
			V=getDepositedItems(mob.Name());
		int min=0;
		for(int v=0;v<V.size();v++)
		{
			Item I=(Item)V.elementAt(v);
			if(I instanceof Coins) continue;
			min+=(I.value()/2);
		}
		return min;
	}

	public void affect(Environmental myHost, Affect affect)
	{
		MOB mob=affect.source();
		if(affect.amITarget(this))
		{
			switch(affect.targetMinor())
			{
			case Affect.TYP_GIVE:
			case Affect.TYP_DEPOSIT:
				{
					if(affect.tool() instanceof Container)
						((Container)affect.tool()).emptyPlease();
					FullMsg msg=new FullMsg(affect.source(),affect.tool(),null,Affect.MSG_DROP,null);
					location().send(this,msg);
					msg=new FullMsg((MOB)affect.target(),affect.tool(),null,Affect.MSG_GET,null);
					location().send(this,msg);
					if(affect.tool() instanceof Coins)
					{
						Coins older=(Coins)affect.tool();
						Coins item=(Coins)CMClass.getItem("StdCoins");
						int newNum=older.numberOfCoins();
						Item old=null;
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
							old=findDepositInventory(affect.source().getClanID(),""+Integer.MAX_VALUE);
						else
							old=findDepositInventory(affect.source().Name(),""+Integer.MAX_VALUE);
						if((old!=null)&&(old instanceof Coins))
							newNum+=((Coins)old).numberOfCoins();
						item.setNumberOfCoins(newNum);
						if(old!=null)
						{
							if(whatISell==ShopKeeper.DEAL_CLANBANKER)
								delDepositInventory(affect.source().getClanID(),old);
							else
								delDepositInventory(affect.source().Name(),old);
						}
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
							addDepositInventory(affect.source().getClanID(),item);
						else
							addDepositInventory(affect.source().Name(),item);
						if(affect.targetMinor()==Affect.TYP_GIVE)
						{
							setMoney(getMoney()-((Coins)affect.tool()).numberOfCoins());
							if(getMoney()<0) setMoney(0);
							recoverEnvStats();
						}
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
						    ExternalPlay.quickSay(this,mob,"Ok, Clan "+mob.getClanID()+" now has a balance of "+getBalance(affect.source())+" gold coins.",true,false);
						else
						    ExternalPlay.quickSay(this,mob,"Ok, your new balance is "+getBalance(affect.source())+" gold coins.",true,false);
					}
					else
					{
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
							addDepositInventory(affect.source().getClanID(),(Item)affect.tool());
						else
							addDepositInventory(affect.source().Name(),(Item)affect.tool());
					    ExternalPlay.quickSay(this,mob,"Thank you, "+affect.tool().name()+" is safe with us.",true,false);
						((Item)affect.tool()).destroy();
					}
				}
				return;
			case Affect.TYP_WITHDRAW:
				{
					Item old=(Item)affect.tool();
					if(old instanceof Coins)
					{
						Item item=null;
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
							item=findDepositInventory(affect.source().getClanID(),""+Integer.MAX_VALUE);
						else
							item=findDepositInventory(affect.source().Name(),""+Integer.MAX_VALUE);
						if((item!=null)&&(item instanceof Coins))
						{
							Coins coins=(Coins)item;
							coins.setNumberOfCoins(coins.numberOfCoins()-((Coins)old).numberOfCoins());
							coins.recoverEnvStats();
							if(whatISell==ShopKeeper.DEAL_CLANBANKER)
								delDepositInventory(affect.source().getClanID(),item);
							else
								delDepositInventory(affect.source().Name(),item);
							com.planet_ink.coffee_mud.utils.Money.giveMoney(mob,affect.source(),((Coins)old).numberOfCoins());
							if(coins.numberOfCoins()<=0)
							{
								if(whatISell==ShopKeeper.DEAL_CLANBANKER)
									ExternalPlay.quickSay(this,mob,"I have closed the account for Clan "+mob.getClanID()+". Thanks for your business.",true,false);
								else
									ExternalPlay.quickSay(this,mob,"I have closed your account. Thanks for your business.",true,false);
								return;
							}
							else
							{
								if(whatISell==ShopKeeper.DEAL_CLANBANKER)
								{
									addDepositInventory(affect.source().getClanID(),item);
								    ExternalPlay.quickSay(this,mob,"Ok, Clan "+affect.source().getClanID()+" now has a balance of "+((Coins)item).numberOfCoins()+" gold coins.",true,false);
								}
								else
								{
									addDepositInventory(affect.source().Name(),item);
								    ExternalPlay.quickSay(this,mob,"Ok, your new balance is "+((Coins)item).numberOfCoins()+" gold coins.",true,false);
								}
							}
						}
						else
						    ExternalPlay.quickSay(this,mob,"But, your balance is "+((Coins)item).numberOfCoins()+" gold coins.",true,false);
					}
					else
					{
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
							delDepositInventory(affect.source().getClanID(),old);
						else
							delDepositInventory(affect.source().Name(),old);
					    ExternalPlay.quickSay(this,mob,"Thank you for your trust.",true,false);
						if(location()!=null)
							location().addItemRefuse(old,Item.REFUSE_PLAYER_DROP);
						FullMsg msg=new FullMsg(mob,old,this,Affect.MSG_GET,null);
						if(location().okAffect(mob,msg))
							location().send(mob,msg);
					}

				}
				return;
			case Affect.TYP_VALUE:
			case Affect.TYP_SELL:
			case Affect.TYP_VIEW:
				super.affect(myHost,affect);
				return;
			case Affect.TYP_BUY:
				super.affect(myHost,affect);
				return;
			case Affect.TYP_LIST:
			{
				super.affect(myHost,affect);
				Vector V=null;
				if(whatISell==ShopKeeper.DEAL_CLANBANKER)
					V=getDepositedItems(mob.getClanID());
				else
					V=getDepositedItems(mob.Name());
				StringBuffer msg=new StringBuffer("\n\r");
				String c="^x[Item                              ] ";
				msg.append(c+c+"^.^N\n\r");
				int colNum=0;
				Coins coins=null;
				boolean otherThanCoins=false;
				for(int i=0;i<V.size();i++)
				{
					Item I=(Item)V.elementAt(i);
					if(I instanceof Coins)
					{
						coins=(Coins)I;
						continue;
					}
					otherThanCoins=true;
					String col=null;
					col="["+Util.padRight(I.name(),34)+"] ";
					if((++colNum)>2)
					{
						msg.append("\n\r");
						colNum=1;
					}
					msg.append(col);
				}
				if(!otherThanCoins)
					msg=new StringBuffer("\n\r^N");
				else
					msg.append("\n\r\n\r");
				if(coins!=null)
				{
					if(whatISell==ShopKeeper.DEAL_CLANBANKER)
						msg.append("Clan "+mob.getClanID()+" has a balance of ^H"+coins.numberOfCoins()+"^? gold coins.");
					else
						msg.append("Your balance with us is ^H"+coins.numberOfCoins()+"^? gold coins.");
				}
				if(coinInterest!=0.0)
				{
					double cci=Util.mul(Math.abs(coinInterest),100.0);
					String ci=((coinInterest>0.0)?"pay ":"charge ")+cci+"% interest ";
					msg.append("\n\rWe "+ci+"weekly on money deposited here.");
				}
				if(itemInterest!=0.0)
				{
					double cci=Util.mul(Math.abs(itemInterest),100.0);
					String ci=((itemInterest>0.0)?"pay ":"charge ")+cci+"% interest ";
					msg.append("\n\rWe "+ci+"weekly on items kept with us.");
				}
				if(bankChain().length()>0)
					msg.append("\n\rI am a banker for "+bankChain()+".");
				ExternalPlay.quickSay(this,mob,msg.toString()+"^T",true,false);
				return;
			}
			default:
				break;
			}
		}
		else
		if(affect.sourceMinor()==Affect.TYP_RETIRE)
			delAllDeposits(affect.source().Name());
		super.affect(myHost,affect);
	}

	public boolean okAffect(Environmental myHost, Affect affect)
	{
		MOB mob=affect.source();
		if(affect.amITarget(this))
		{
			switch(affect.targetMinor())
			{
			case Affect.TYP_GIVE:
			case Affect.TYP_DEPOSIT:
				{
					if(affect.tool()==null) return false;
					if(affect.tool() instanceof Coins)
						return true;
					if((whatISell==ShopKeeper.DEAL_CLANBANKER)
					&&((affect.source().getClanID().length()==0)
					  ||(Clans.getClan(affect.source().getClanID())==null)))
					{
						ExternalPlay.quickSay(this,mob,"I'm sorry, I only do business with Clans, and you aren't part of one.",true,false);
						return false;
					}
					if(!(affect.tool() instanceof Item))
					{
						mob.tell(mob.charStats().HeShe()+" doesn't look interested.");
						return false;
					}
					int balance=getBalance(mob);
					int minbalance=minBalance(mob)+(((Item)affect.tool()).value()/2);
					if(balance<minbalance)
					{
						if(whatISell==ShopKeeper.DEAL_CLANBANKER)
							ExternalPlay.quickSay(this,mob,"Clan "+affect.source().getClanID()+" will need a total balance of "+minbalance+" for me to hold that.",true,false);
						else
							ExternalPlay.quickSay(this,mob,"You'll need a total balance of "+minbalance+" for me to hold that.",true,false);
						return false;
					}
				}
				return true;
			case Affect.TYP_WITHDRAW:
				{
					String thename=affect.source().Name();
					if(whatISell==ShopKeeper.DEAL_CLANBANKER)
					{
						thename=affect.source().getClanID();
						Clan C=Clans.getClan(affect.source().getClanID());
						if((affect.source().getClanID().length()==0)
						  ||(C==null))
						{
							ExternalPlay.quickSay(this,mob,"I'm sorry, I only do business with Clans, and you aren't part of one.",true,false);
							return false;
						}
						
						if(C.allowedToDoThis(affect.source(),Clan.FUNC_CLANWITHDRAW)>=0)
						{
							ExternalPlay.quickSay(this,mob,"I'm sorry, you aren't authorized by your clan to do that.",true,false);
							return false;
						}
					}
					if((affect.tool()==null)||(!(affect.tool() instanceof Item)))
					{
						ExternalPlay.quickSay(this,mob,"What do you want? I'm busy!",true,false);
						return false;
					}
					if((!(affect.tool() instanceof Coins))
					&&(findDepositInventory(thename,affect.tool().Name())==null))
					{
						ExternalPlay.quickSay(this,mob,"You want WHAT?",true,false);
						return false;
					}
					int balance=getBalance(affect.source());
					int minbalance=minBalance(mob);
					if(affect.tool() instanceof Coins)
					{
						if(((Coins)affect.tool()).numberOfCoins()>balance)
						{
							if(whatISell==ShopKeeper.DEAL_CLANBANKER)
								ExternalPlay.quickSay(this,mob,"I'm sorry, Clan "+thename+" has only "+balance+" gold coins in its account.",true,false);
							else
								ExternalPlay.quickSay(this,mob,"I'm sorry, you have only "+balance+" gold coins in your account.",true,false);
							return false;
						}
						if(minbalance==0) return true;
						if(((Coins)affect.tool()).numberOfCoins()>(balance-minbalance))
						{
							if((balance-minbalance)>0)
								ExternalPlay.quickSay(this,mob,"I'm sorry, you may only withdraw "+(balance-minbalance)+" gold coins at this time.",true,false);
							else
								ExternalPlay.quickSay(this,mob,"I am holding other items in trust, so you may not withdraw funds at this time.",true,false);
							return false;
						}
					}
				}
				return true;
			case Affect.TYP_VALUE:
			case Affect.TYP_SELL:
			case Affect.TYP_VIEW:
				return super.okAffect(myHost,affect);
			case Affect.TYP_BUY:
				return super.okAffect(myHost,affect);
			case Affect.TYP_LIST:
			{
				String thename=affect.source().Name();
				if(whatISell==ShopKeeper.DEAL_CLANBANKER)
				{
					thename=affect.source().getClanID();
					Clan C=Clans.getClan(affect.source().getClanID());
					if((affect.source().getClanID().length()==0)
					  ||(C==null))
					{
						ExternalPlay.quickSay(this,mob,"I'm sorry, I only do business with Clans, and you aren't part of one.",true,false);
						return false;
					}
					if(C.allowedToDoThis(affect.source(),Clan.FUNC_CLANDEPOSITLIST)>=0)
					{
						ExternalPlay.quickSay(this,mob,"I'm sorry, you aren't authorized by your clan to do that.",true,false);
						return false;
					}
				}
				if(numberDeposited(thename)==0)
				{
					StringBuffer msg=new StringBuffer("");
					if(whatISell==ShopKeeper.DEAL_CLANBANKER)
						msg.append("The Clan "+thename+" does not have an account with us, I'm afraid.");
					else
						msg.append("You don't have an account with us, I'm afraid.");
					if(coinInterest!=0.0)
					{
						double cci=Util.mul(Math.abs(coinInterest),100.0);
						String ci=((coinInterest>0.0)?"pay ":"charge ")+cci+"% interest ";
						msg.append("\n\rWe "+ci+"weekly on money deposited here.");
					}
					if(itemInterest!=0.0)
					{
						double cci=Util.mul(Math.abs(itemInterest),100.0);
						String ci=((itemInterest>0.0)?"pay ":"charge ")+cci+"% interest ";
						msg.append("\n\rWe "+ci+"weekly on items kept with us.");
					}
					if(bankChain().length()>0)
						msg.append("\n\rI am a banker for "+bankChain()+".");
					ExternalPlay.quickSay(this,mob,msg.toString()+"^T",true,false);
					return false;
				}
				else
					return true;
			}
			default:
				break;
			}
		}
		return super.okAffect(myHost,affect);
	}
}
