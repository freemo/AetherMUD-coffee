package com.planet_ink.coffee_mud.Abilities.Ranger;
import com.planet_ink.coffee_mud.interfaces.*;

public class Ranger_Enemy3 extends Ranger_Enemy1
{
	public String ID() { return "Ranger_Enemy3"; }
	public String name(){ return "Favored Enemy 3";}
	public Environmental newInstance(){	Ranger_Enemy3 BOB=new Ranger_Enemy3();	BOB.setMiscText(text()); return BOB;}
}
