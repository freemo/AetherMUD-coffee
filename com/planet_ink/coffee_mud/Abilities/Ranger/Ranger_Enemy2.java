package com.planet_ink.coffee_mud.Abilities.Ranger;
import com.planet_ink.coffee_mud.interfaces.*;

public class Ranger_Enemy2 extends Ranger_Enemy1
{
	public String ID() { return "Ranger_Enemy2"; }
	public String name(){ return "Favored Enemy 2";}
	public Environmental newInstance(){	Ranger_Enemy2 BOB=new Ranger_Enemy2();	BOB.setMiscText(text()); return BOB;}
	
}
