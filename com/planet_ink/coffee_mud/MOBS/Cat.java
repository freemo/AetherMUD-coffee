package com.planet_ink.coffee_mud.MOBS;

import java.util.*;
import com.planet_ink.coffee_mud.utils.*;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
public class Cat extends StdMOB
{

	public Cat()
	{
		super();
		Random randomizer = new Random(System.currentTimeMillis());

		Username="a cat";
		setDescription("It\\`s furry with four legs, and a long fluffy tail.");
		setDisplayText("A cat calmly watches you.");
		setAlignment(500);
		setMoney(0);
		baseEnvStats.setWeight(20 + Math.abs(randomizer.nextInt() % 55));
		setWimpHitPoint(2);

		addBehavior(CMClass.getBehavior("Follower"));
		addBehavior(CMClass.getBehavior("MudChat"));

		baseEnvStats().setDamage(4);

		baseCharStats().setStat(CharStats.INTELLIGENCE,1 + Math.abs(randomizer.nextInt() % 4));

		baseEnvStats().setAbility(0);
		baseEnvStats().setLevel(1);
		baseEnvStats().setArmor(10);

		baseCharStats().setMyRace(CMClass.getRace("Cat"));
		baseCharStats().getMyRace().setHeightWeight(baseEnvStats(),(char)baseCharStats().getStat(CharStats.GENDER));
		baseState.setHitPoints(Math.abs(randomizer.nextInt() % 6) + 2);

		recoverMaxState();
		resetToMaxState();
		recoverEnvStats();
		recoverCharStats();
	}
	public Environmental newInstance()
	{
		return new Cat();
	}
}
