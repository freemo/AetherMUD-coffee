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
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.Climate;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Chant_EelShock extends Chant {
    private final static String localizedName = CMLib.lang().L("Eel Shock");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Stunned)");

    @Override
    public String ID() {
        return "Chant_EelShock";
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
    public int maxRange() {
        return 3;
    }

    @Override
    public int minRange() {
        return 0;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
    }

    @Override
    public long flags() {
        return Ability.FLAG_AIRBASED;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            super.unInvoke();
        if (canBeUninvoked())
            if ((mob.location() != null) && (!mob.amDead()))
                mob.tell(L("<S-YOUPOSS> are no longer stunned."));
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(PhyStats.IS_SITTING);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if ((msg.amISource(mob))
            && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
            && (msg.sourceMajor() > 0)) {
            mob.tell(L("You are stunned."));
            return false;
        }
        return super.okMessage(myHost, msg);
    }

    private boolean roomWet(Room location) {
        if (CMLib.flags().isWateryRoom(location) || location.domainType() == Room.DOMAIN_OUTDOORS_SWAMP)
            return true;

        final Area currentArea = location.getArea();
        if (currentArea.getClimateObj().weatherType(location) == Climate.WEATHER_RAIN ||
            currentArea.getClimateObj().weatherType(location) == Climate.WEATHER_THUNDERSTORM)
            return true;
        return false;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Set<MOB> h = CMLib.combat().properTargets(this, mob, false);
            if (h == null)
                return Ability.QUALITY_INDIFFERENT;
            final Room location = mob.location();
            if (location != null) {
                if (!roomWet(location))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = CMLib.combat().properTargets(this, mob, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth shocking."));
            return false;
        }

        final Room location = mob.location();

        if (!roomWet(location)) {
            mob.tell(L("It's too dry to invoke this chant."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), L("^S<S-NAME> chant(s) and electrical sparks dance across <S-HIS-HER> skin.^?"))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastMask(mob, target, auto) | CMMsg.TYP_ELECTRIC, L("<T-NAME> is stunned."));
                    if (mob.location().okMessage(mob, msg)) {
                        mob.location().send(mob, msg);
                        if (msg.value() <= 0)
                            maliciousAffect(mob, target, asLevel, 3 + super.getXLEVELLevel(mob) + (2 * super.getX1Level(mob)), -1);
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> sees tiny sparks dance across <S-HIS-HER> skin, but nothing more happens."));
        // return whether it worked
        return success;
    }
}
