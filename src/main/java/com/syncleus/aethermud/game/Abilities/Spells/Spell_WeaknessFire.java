/**
 * Copyright 2017 Syncleus, Inc.
 * with portions copyright 2004-2017 Bo Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.Climate;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Spell_WeaknessFire extends Spell {

    private final static String localizedName = CMLib.lang().L("Weakness to Fire");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Weakness to Fire)");

    @Override
    public String ID() {
        return "Spell_WeaknessFire";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.sourceMinor() == CMMsg.TYP_FIRE)) {
            final int recovery = (int) Math.round(CMath.mul((msg.value()), 1.5));
            msg.setValue(msg.value() + recovery);
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (tickID != Tickable.TICKID_MOB)
            return false;
        if ((affecting() != null) && (affecting() instanceof MOB)) {
            final MOB M = (MOB) affecting();
            final Room room = M.location();
            final MOB invoker = (invoker() != null) ? invoker() : M;
            if (room != null) {
                if ((room.getArea().getClimateObj().weatherType(room) == Climate.WEATHER_HEAT_WAVE)
                    && (CMLib.dice().rollPercentage() > M.charStats().getSave(CharStats.STAT_SAVE_FIRE))) {
                    final int damage = CMLib.dice().roll(1, 8, 0);
                    CMLib.combat().postDamage(invoker, M, null, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("The scorching heat <DAMAGE> <T-NAME>!"));
                } else if ((room.getArea().getClimateObj().weatherType(room) == Climate.WEATHER_DUSTSTORM)
                    && (CMLib.dice().rollPercentage() > M.charStats().getSave(CharStats.STAT_SAVE_FIRE))) {
                    final int damage = CMLib.dice().roll(1, 16, 0);
                    CMLib.combat().postDamage(invoker, M, null, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("The burning hot dust <DAMAGE> <T-NAME>!"));
                } else if ((room.getArea().getClimateObj().weatherType(room) == Climate.WEATHER_DROUGHT)
                    && (CMLib.dice().rollPercentage() > M.charStats().getSave(CharStats.STAT_SAVE_FIRE))) {
                    final int damage = CMLib.dice().roll(1, 8, 0);
                    CMLib.combat().postDamage(invoker, M, null, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("The burning dry heat <DAMAGE> <T-NAME>!"));
                } else
                    return true;
                CMLib.combat().postRevengeAttack(M, invoker);
            }
        }
        return true;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("Your fire weakness is now gone."));

        super.unInvoke();

    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectedStats) {
        super.affectCharStats(affectedMOB, affectedStats);
        affectedStats.setStat(CharStats.STAT_SAVE_FIRE, affectedStats.getStat(CharStats.STAT_SAVE_FIRE) - 100);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("A shimmering flammable field appears around <T-NAMESELF>.") : L("^S<S-NAME> invoke(s) a flammable field around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0)
                    success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
            }
        } else
            maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to invoke weakness to fire, but fail(s)."));

        return success;
    }
}
