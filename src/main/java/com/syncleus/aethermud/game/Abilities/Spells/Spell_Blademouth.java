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
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Spell_Blademouth extends Spell {

    private final static String localizedName = CMLib.lang().L("Blademouth");
    private final static String localizedStaticDisplay = CMLib.lang().L("(blades in your mouth)");
    public Vector<String> limbsToRemove = new Vector<String>();
    protected boolean noRecurse = false;

    @Override
    public String ID() {
        return "Spell_Blademouth";
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
        return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
            && (!noRecurse)
            && (affected instanceof MOB)
            && (msg.amISource((MOB) affected))
            && (msg.source().location() != null)
            && (msg.source().charStats().getMyRace().bodyMask()[Race.BODY_MOUTH] >= 0)) {
            noRecurse = true;
            final MOB invoker = (invoker() != null) ? invoker() : msg.source();
            try {
                CMLib.combat().postDamage(invoker, msg.source(), this, msg.source().maxState().getHitPoints() / 20, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, Weapon.TYPE_SLASHING, L("The blades in <T-YOUPOSS> mouth <DAMAGE> <T-HIM-HER>!"));
            } finally {
                noRecurse = false;
            }
        }
        super.executeMsg(host, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (((MOB) target).charStats().getMyRace().bodyMask()[Race.BODY_MOUTH] <= 0)
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (target.charStats().getMyRace().bodyMask()[Race.BODY_MOUTH] <= 0) {
            if (!auto)
                mob.tell(L("There is no mouth on @x1 to fill with blades!", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "!" : "^S<S-NAME> invoke(s) a sharp spell upon <T-NAMESELF>"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                super.maliciousAffect(mob, target, asLevel, 0, -1);
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> incant(s) sharply at <T-NAMESELF>, but flub(s) the spell."));

        // return whether it worked
        return success;
    }
}
