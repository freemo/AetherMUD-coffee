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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.*;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_MagicBullets extends Spell {

    private final static String localizedName = CMLib.lang().L("Magic Bullets");
    protected volatile boolean norecurse = false;

    @Override
    public String ID() {
        return "Spell_MagicBullets";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public void affectPhyStats(Physical host, PhyStats affectableStats) {
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_BONUS);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        final MOB mob;
        if ((affected instanceof Item) && (((Item) affected).owner() instanceof MOB)) {
            mob = (MOB) ((Item) affected).owner();
        } else
            return;

        if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.value() > 0)
            && (msg.target() instanceof MOB)
            && (!((MOB) msg.target()).amDead())
            && (msg.tool() != this)
            && (msg.source().location() != null)) {
            if ((msg.tool() == affected)
                && (!msg.amITarget(mob))
                && (msg.amISource(mob))
                && (!(msg.tool() instanceof Wand))) {
                final int damage = msg.value() + CMath.s_int(text());
                final String str = L("^F^<FIGHT^><S-YOUPOSS> magic bullet <DAMAGE> <T-NAME>!^</FIGHT^>^?");
                synchronized (this) {
                    if (!norecurse) {
                        norecurse = true;
                        try {
                            CMLib.combat().postDamage(msg.source(), (MOB) msg.target(), affected, Math.round(damage),
                                CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_WEAPONATTACK, Weapon.TYPE_BASHING, str);
                        } finally {
                            norecurse = false;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = super.getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;
        if ((!(target instanceof Ammunition)) || (!((Ammunition) target).ammunitionType().equals("bullets"))) {
            mob.tell(mob, target, null, L("You can only enchant sling bullets with this spell, which <T-NAME> is not."));
            return false;
        }

        if (target.fetchEffect("Spell_MagicBullets") != null) {
            mob.tell(mob, target, null, L("<T-NAME> is already altered."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("^S<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(mob, target, null, CMMsg.MSG_OK_VISUAL, L("<T-NAME> chang(es) shape, becoming more deadly"));
                Ability A = CMClass.getAbility(ID());
                if (A != null) {
                    A.setMiscText("" + super.getXLEVELLevel(invoker()));
                    target.addNonUninvokableEffect(A);
                }
                target.recoverPhyStats();
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly, and looking very frustrated."));
        // return whether it worked
        return success;
    }
}
