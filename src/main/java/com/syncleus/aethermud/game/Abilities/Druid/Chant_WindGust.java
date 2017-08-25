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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.Climate;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Chant_WindGust extends Chant {
    private final static String localizedStaticDisplay = CMLib.lang().L("(Blown Down)");
    public boolean doneTicking = false;

    @Override
    public String ID() {
        return "Chant_WindGust";
    }

    @Override
    public String name() {
        return renderedMundane ? "wind gust" : "Wind Gust";
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
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(4);
    }

    @Override
    public long flags() {
        return Ability.FLAG_MOVING;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (!doneTicking)
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SITTING);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((doneTicking) && (msg.amISource(mob)))
            unInvoke();
        else if (msg.amISource(mob) && (msg.sourceMinor() == CMMsg.TYP_STAND))
            return false;
        return true;
    }

    @Override
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            doneTicking = true;
        super.unInvoke();
        if (canBeUninvoked()) {
            if ((mob.location() != null) && (!mob.amDead())) {
                final CMMsg msg = CMClass.getMsg(mob, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> regain(s) <S-HIS-HER> feet."));
                if (mob.location().okMessage(mob, msg)) {
                    mob.location().send(mob, msg);
                    CMLib.commands().postStand(mob, true);
                }
            } else
                mob.tell(L("You regain your feet."));
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if ((h == null) || (h.size() == 0)) {
            mob.tell(L("There doesn't appear to be anyone here worth blowing around."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), L(auto ? "^JA horrendous wind gust blows through here.^?" : "^S<S-NAME> chant(s) at <S-HIS-HER> enemies.^?") + CMLib.protocol().msp("wind.wav", 40))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("<T-NAME> get(s) blown back!"));
                    if ((mob.location().okMessage(mob, msg)) && (target.fetchEffect(this.ID()) == null)) {
                        if ((msg.value() <= 0) && (target.location() == mob.location())) {
                            int howLong = 2;
                            if ((mob.location().getArea().getClimateObj().weatherType(mob.location()) == Climate.WEATHER_WINDY)
                                || (mob.location().getArea().getClimateObj().weatherType(mob.location()) == Climate.WEATHER_DUSTSTORM)
                                || (mob.location().getArea().getClimateObj().weatherType(mob.location()) == Climate.WEATHER_THUNDERSTORM))
                                howLong = 4;

                            final MOB victim = target.getVictim();
                            if ((victim != null) && (target.rangeToTarget() >= 0))
                                target.setRangeToTarget(target.rangeToTarget() + (howLong / 2));
                            if (target.rangeToTarget() > target.location().maxRange())
                                target.setRangeToTarget(target.location().maxRange());
                            mob.location().send(mob, msg);
                            if ((!CMLib.flags().isInFlight(target))
                                && (CMLib.dice().rollPercentage() > (((target.charStats().getStat(CharStats.STAT_DEXTERITY) * 2) + target.phyStats().level())) - (5 * howLong))
                                && (target.charStats().getBodyPart(Race.BODY_LEG) > 0)) {
                                mob.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> fall(s) down!"));
                                doneTicking = false;
                                success = maliciousAffect(mob, target, asLevel, howLong, -1) != null;
                            }
                            if (target.getVictim() != null)
                                target.getVictim().setRangeToTarget(target.rangeToTarget());
                            if (mob.getVictim() == null) mob.setVictim(null); // correct range
                            if (target.getVictim() == null) target.setVictim(null); // correct range
                        }
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> chant(s), but nothing happens."));

        // return whether it worked
        return success;
    }
}
