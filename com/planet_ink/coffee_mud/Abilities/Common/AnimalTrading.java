package com.planet_ink.coffee_mud.Abilities.Common;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class AnimalTrading extends CommonSkill
{
	public String ID() { return "AnimalTrading"; }
	public String name(){ return "Animal Trading";}
	private static final String[] triggerStrings = {"ANIMALTRADING","ANIMALTRADE","ANIMALSELL","ASELL"};
	public String[] triggerStrings(){return triggerStrings;}
	protected int canAffectCode(){return 0;}
	protected int canTargetCode(){return Ability.CAN_MOBS;}
	private static boolean mapped=false;

	public AnimalTrading()
	{
		super();
		if(!mapped){mapped=true;
					CMAble.addCharAbilityMapping("Archon",1,ID(),false);
		}
	}
	public Environmental newInstance(){	return new AnimalTrading();}

	private static MOB shopkeeper(Room here, MOB mob)
	{
		for(int i=0;i<here.numInhabitants();i++)
		{
			MOB thisMOB=here.fetchInhabitant(i);
			if((thisMOB!=null)
			&&(thisMOB instanceof ShopKeeper)
			&&(Sense.canBeSeenBy(thisMOB,mob)))
				return thisMOB;
		}
		return null;
	}
	
	public boolean invoke(MOB mob, Vector commands, Environmental givenTarget, boolean auto)
	{
		Environmental taming=null;
		Item cage=null;
		
		MOB shopkeeper=shopkeeper(mob.location(),mob);
		if(shopkeeper==null)
		{
			if(commands.size()<3)
			{
				mob.tell("Sell what to whom?");
				return false;
			}
			shopkeeper=mob.location().fetchInhabitant((String)commands.elementAt(commands.size()-1));
			if((shopkeeper!=null)&&(shopkeeper instanceof ShopKeeper)&&(Sense.canBeSeenBy(shopkeeper,mob)))
				commands.removeElementAt(commands.size()-1);
			else
			{
				mob.tell("You don't see anyone called '"+(String)commands.elementAt(commands.size()-1)+"' buying anything.");
				return false;
			}
		}
		
		String str=Util.combine(commands,0);
		MOB M=mob.location().fetchInhabitant(str);
		if(M!=null)
		{
			if(!Sense.canBeSeenBy(M,mob))
			{
				commonTell(mob,"You don't see anyone called '"+str+"' here.");
				return false;
			}
			if((!M.isMonster())||(M.charStats().getStat(CharStats.INTELLIGENCE)>1))
			{
				commonTell(mob,"You can't sell "+M.name()+".");
				return false;
			}
			if((Sense.canMove(M))&&(!Sense.isBound(M)))
			{
				commonTell(mob,M.name()+" doesn't seem willing to cooperate.");
				return false;
			}
			taming=M;
		}
		else
		if(mob.location()!=null)
		{
			for(int i=0;i<mob.location().numItems();i++)
			{
				Item I=mob.location().fetchItem(i);
				if((I!=null)
				&&(I instanceof Container)
				&&((((Container)I).containTypes()&Container.CONTAIN_CAGED)==Container.CONTAIN_CAGED))
				{ cage=I; break;}
			}
			if(cage==null)
			for(int i=0;i<mob.inventorySize();i++)
			{
				Item I=mob.fetchInventory(i);
				if((I!=null)
				&&(I instanceof Container)
				&&((((Container)I).containTypes()&Container.CONTAIN_CAGED)==Container.CONTAIN_CAGED))
				{ cage=I; break;}
			}
			if(commands.size()>0)
			{
				String last=(String)commands.lastElement();
				Environmental E=mob.location().fetchFromMOBRoomFavorsItems(mob,null,last,Item.WORN_REQ_ANY);
				if(E==null) 
				if((E!=null)
				&&(E instanceof Item)
				&&(E instanceof Container)
				&&((((Container)E).containTypes()&Container.CONTAIN_CAGED)==Container.CONTAIN_CAGED))
				{
					cage=(Item)E;
					commands.removeElement(last);
				}
			}
			if(cage==null)
			{
				commonTell(mob,"You don't see anyone called '"+str+"' here.");
				return false;
			}
			taming=mob.location().fetchFromMOBRoomFavorsItems(mob,cage,Util.combine(commands,0),Item.WORN_REQ_ANY);
			if((taming==null)||(!Sense.canBeSeenBy(taming,mob))||(!(taming instanceof CagedAnimal)))
			{
				commonTell(mob,"You don't see any creatures in "+cage.name()+" called '"+Util.combine(commands,0)+"'.");
				return false;
			}
			M=((CagedAnimal)taming).unCageMe();
		}
		
		if(!super.invoke(mob,commands,givenTarget,auto))
			return false;
		if(profficiencyCheck(0,auto))
		{
			FullMsg msg=new FullMsg(mob,shopkeeper,M,Affect.MSG_SELL,"<S-NAME> sell(s) <O-NAME> to <T-NAME>.");
			if(mob.location().okAffect(mob,msg))
			{
				mob.location().send(mob,msg);
				if(taming instanceof Item)
					((Item)taming).destroyThis();
			}
		}
		else
			beneficialWordsFizzle(mob,shopkeeper,"<S-NAME> <S-IS-ARE>n't able to strike a deal with <T-NAME>.");
		return true;
	}
}