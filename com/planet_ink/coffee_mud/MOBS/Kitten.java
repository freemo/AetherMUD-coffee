package com.planet_ink.coffee_mud.MOBS;

import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
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
public class Kitten extends StdMOB
{
	public String ID(){return "Kitten";}
	public Kitten()
	{
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		Username="a kitten";
		setDescription("It\\`s small, cute, and furry with four legs, and a long fluffy tail.");
		setDisplayText("A kitten watches you.");
		setAlignment(500);
		setMoney(0);
		baseEnvStats.setWeight(20 + Math.abs(randomizer.nextInt() % 55));
		setWimpHitPoint(2);

		addBehavior(CMClass.getBehavior("Follower"));
		addBehavior(CMClass.getBehavior("MudChat"));

		baseEnvStats().setDamage(4);

		baseCharStats().setStat(CharStats.INTELLIGENCE,1);

		baseEnvStats().setAbility(0);
		baseEnvStats().setLevel(1);
		baseEnvStats().setArmor(10);

		baseCharStats().setMyRace(CMClass.getRace("Kitten"));
		baseCharStats().getMyRace().startRacing(this,false);
		baseState.setHitPoints(Dice.roll(baseEnvStats().level(),20,baseEnvStats().level()));

		recoverMaxState();
		resetToMaxState();
		recoverEnvStats();
		recoverCharStats();
	}

}
