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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Prayer_DivineFavor extends Prayer {
    private final static String localizedName = CMLib.lang().L("Divine Favor");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Divine Favor)");
    protected boolean struckDownToday = false;

    @Override
    public String ID() {
        return "Prayer_DivineFavor";
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
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
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
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your fall out of divine favor."));
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((msg.source() == affected)
            && (msg.sourceMinor() == CMMsg.TYP_DEATH))
            unInvoke();
        if ((msg.source() == affected)
            && (msg.sourceMinor() == CMMsg.TYP_EXPCHANGE)
            && (msg.source().getWorshipCharID().length() > 0)) {
            if (msg.value() < 0)
                msg.setValue((int) Math.round(CMath.mul(msg.value(), 0.9)));
            else
                msg.setValue((int) Math.round(CMath.mul(msg.value(), 1.1)));
        }
        return super.okMessage(host, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected instanceof MOB)
            && (((MOB) affected).isInCombat())
            && (!struckDownToday)
            && (CMLib.dice().roll(1, 1000, 0) == 1)
            && (((MOB) affected).getWorshipCharID().length() > 0)
            && (!((MOB) affected).getVictim().getWorshipCharID().equals(((MOB) affected).getWorshipCharID()))) {
            final MOB deityM = CMLib.map().getDeity(((MOB) affected).getWorshipCharID());
            if (deityM != null) {
                struckDownToday = true;
                ((MOB) affected).location().showOthers(deityM, ((MOB) affected).getVictim(), null, CMMsg.MSG_OK_ACTION, L("@x1 strike(s) down <T-NAME> with all of <S-HIS-HER> divine fury!", deityM.name()));
                CMLib.combat().postDeath(deityM, ((MOB) affected).getVictim(), null);
            }
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Physical target = mob;
        if ((auto) && (givenTarget != null))
            target = givenTarget;
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(mob, target, null, L("<T-NAME> <T-IS-ARE> already affected by @x1.", name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> become(s) divinely favored.") : L("^S<S-NAME> @x1 for divine favor.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> @x1, but there's no answer.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
