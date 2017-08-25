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
package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Spell_Laughter extends Spell {

    private final static String localizedName = CMLib.lang().L("Laughter");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Laughter spell)");

    @Override
    public String ID() {
        return "Spell_Laughter";
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_PARALYZING;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);

        if (!super.tick(ticking, tickID))
            return false;
        ((MOB) affected).location().show((MOB) affected, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> laugh(s) uncontrollably, unable to move!"));
        return true;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();
        if (canBeUninvoked()) {
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> stop(s) laughing."));
            CMLib.commands().postStand(mob, true);
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff < 0)
            levelDiff = 0;
        if (levelDiff > 5)
            levelDiff = 5;

        // if they can't hear the sleep spell, it
        // won't happen
        if ((!auto) && (!CMLib.flags().canBeHeardSpeakingBy(mob, target))) {
            mob.tell(L("@x1 can't hear your words.", target.charStats().HeShe()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if (levelDiff < 0)
            levelDiff = 0;
        boolean success = proficiencyCheck(mob, -(levelDiff * 5), auto);

        if (success) {
            invoker = mob;
            final Room R = mob.location();
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> tell(s) <T-NAMESELF> a magical joke.^?"));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((R.okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                R.send(mob, msg);
                R.send(mob, msg2);
                if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                    int ticks = 8 - levelDiff;
                    if (ticks <= 0)
                        ticks = 1;
                    success = maliciousAffect(mob, target, asLevel, ticks, -1) != null;
                    if (success)
                        if (target.location() == R)
                            R.show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> begin(s) laughing uncontrollably, unable to move!!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> tell(s) <T-NAMESELF> a magical joke, but <T-NAME> do(es)n't think it is funny."));

        // return whether it worked
        return success;
    }
}
