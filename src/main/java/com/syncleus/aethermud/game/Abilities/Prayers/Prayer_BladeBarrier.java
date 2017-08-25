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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_BladeBarrier extends Prayer {
    private final static String localizedName = CMLib.lang().L("Blade Barrier");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Blade Barrier)");
    protected long oncePerTickTime = 0;

    @Override
    public String ID() {
        return "Prayer_BladeBarrier";
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
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CREATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    protected String startStr() {
        return "A barrier of blades begin to spin around <T-NAME>!^?";
    }

    protected void doDamage(MOB srcM, MOB targetM, int damage) {
        CMLib.combat().postDamage(srcM, targetM, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, Weapon.TYPE_SLASHING, L("The blade barrier around <S-NAME> slices and <DAMAGE> <T-NAME>."));
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> @x1 disappears.", name().toLowerCase()));
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (!(affected instanceof MOB))
            return;
        if ((msg.target() == affected) && (!msg.amISource((MOB) affected))) {
            if ((CMLib.dice().rollPercentage() > 60 + msg.source().charStats().getStat(CharStats.STAT_DEXTERITY))
                && (msg.source().rangeToTarget() == 0)
                && (oncePerTickTime != ((MOB) affected).lastTickedDateTime())
                && ((msg.targetMajor(CMMsg.MASK_HANDS))
                || (msg.targetMajor(CMMsg.MASK_MOVE)))) {
                final MOB meM = (MOB) msg.target();
                final int damage = CMLib.dice().roll(1, (int) Math.round(adjustedLevel(meM, 0) / 5.0), 1);
                final StringBuffer hitWord = new StringBuffer(CMLib.combat().standardHitWord(-1, damage));
                if (hitWord.charAt(hitWord.length() - 1) == ')')
                    hitWord.deleteCharAt(hitWord.length() - 1);
                if (hitWord.charAt(hitWord.length() - 2) == '(')
                    hitWord.deleteCharAt(hitWord.length() - 2);
                if (hitWord.charAt(hitWord.length() - 3) == '(')
                    hitWord.deleteCharAt(hitWord.length() - 3);
                this.doDamage(meM, msg.source(), damage);
                oncePerTickTime = ((MOB) msg.target()).lastTickedDateTime();
            }
        }
        return;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setArmor(affectableStats.armor() - 1 - (adjustedLevel(invoker(), 0) / 10));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> already <S-HAS-HAVE> @x1.", name().toLowerCase()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "" : "^S<S-NAME> " + prayWord(mob) + " for divine protection!  ") + startStr());
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 for divine protection, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
