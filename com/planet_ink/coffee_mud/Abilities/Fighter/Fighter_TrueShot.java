package com.planet_ink.coffee_mud.Abilities.Fighter;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Fighter_TrueShot extends StdAbility
{
	public String ID() { return "Fighter_TrueShot"; }
	public String name(){ return "True Shot";}
	public String displayText(){ return "";}
	protected int canAffectCode(){return CAN_MOBS;}
	protected int canTargetCode(){return 0;}
	public int quality(){return Ability.BENEFICIAL_SELF;}
	public int classificationCode(){return Ability.SKILL;}
	public boolean isAutoInvoked(){return true;}
	public boolean canBeUninvoked(){return false;}
	public Environmental newInstance(){	return new Fighter_TrueShot();}

	public void affectEnvStats(Environmental affected, EnvStats affectableStats)
	{
		if((affected==null)||(!(affected instanceof MOB))) return;
		Item w=((MOB)affected).fetchWieldedItem();
		if((w==null)||(!(w instanceof Weapon))) return;
		if((((Weapon)w).weaponClassification()==Weapon.CLASS_RANGED)
		||(((Weapon)w).weaponClassification()==Weapon.CLASS_THROWN))
		{
			int bonus=(int)Math.round(Util.mul(affectableStats.attackAdjustment(),(Util.div(profficiency(),200.0))));
			affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()+bonus);
		}
	}
	public void affect(Environmental myHost, Affect affect)
	{
		super.affect(myHost,affect);

		if((affected==null)||(!(affected instanceof MOB)))
			return;

		MOB mob=(MOB)affected;

		if((affect.amISource(mob))
		&&(affect.sourceMinor()==Affect.TYP_WEAPONATTACK)
		&&(Dice.rollPercentage()>95)
		&&(mob.isInCombat())
		&&(!mob.amDead())
		&&(affect.target() instanceof MOB))
			helpProfficiency(mob);
	}
}