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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Fighter_FieldTactics extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Field Tactics");
    private static final Integer[] landClasses = {Integer.valueOf(-1)};
    protected boolean activated = false;
    protected boolean hidden = false;
    protected long sitTime = 0;

    @Override
    public String ID() {
        return "Fighter_FieldTactics";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return hidden ? "(Hidden)" : "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    public Integer[] landClasses() {
        return landClasses;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected != null) && (affected instanceof MOB) && (activated)) {
            if (hiding(affected)) {
                if (!hidden) {
                    hidden = true;
                    sitTime = System.currentTimeMillis();
                    affected.recoverPhyStats();
                }
            } else if (hidden) {
                hidden = false;
                sitTime = System.currentTimeMillis();
                affected.recoverPhyStats();
            }
        }
        return true;
    }

    public boolean hiding(Environmental mob) {
        if (!(mob instanceof MOB))
            return false;
        return CMLib.flags().isSitting((MOB) mob) && (((MOB) mob).riding() == null);
    }

    public boolean hiding(MOB mob) {
        return CMLib.flags().isSitting(mob) && (mob.riding() == null);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected instanceof MOB)
            && (activated)
            && (msg.amISource((MOB) affected))
            && (!msg.amITarget(affected))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.tool() instanceof Weapon)
            && (msg.value() > 0)) {
            if (CMLib.dice().rollPercentage() < 5)
                helpProficiency((MOB) affected, 0);
            msg.setValue(msg.value() + (int) Math.round(CMath.mul(msg.value(), CMath.div(proficiency(), 400.0 - (20.0 * getXLEVELLevel(msg.source()))))));
        } else if ((hidden) && (!hiding(affected))) {
            hidden = false;
            sitTime = System.currentTimeMillis();
            affected.recoverPhyStats();
        } else if ((msg.source() == affected)
            && (hidden)
            && ((msg.sourceMajor(CMMsg.MASK_SOUND)
            || (msg.sourceMinor() == CMMsg.TYP_SPEAK)
            || (msg.sourceMinor() == CMMsg.TYP_ENTER)
            || (msg.sourceMinor() == CMMsg.TYP_LEAVE)
            || (msg.sourceMinor() == CMMsg.TYP_RECALL)))
            && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
            && (msg.sourceMinor() != CMMsg.TYP_LOOK)
            && (msg.sourceMinor() != CMMsg.TYP_EXAMINE)
            && (msg.sourceMajor() > 0)) {
            hidden = false;
            sitTime = System.currentTimeMillis();
            affected.recoverPhyStats();
        }
        return super.okMessage(myHost, msg);
    }

    public boolean oneOf(int dom) {
        for (int i = 0; i < landClasses().length; i++) {
            if ((dom == landClasses()[i].intValue())
                || (landClasses()[i].intValue() < 0))
                return true;
        }
        return false;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((affected instanceof MOB)
            && (((MOB) affected).location() != null)
            && (oneOf(((MOB) affected).location().domainType()))) {
            if ((hidden) && ((System.currentTimeMillis() - sitTime) > (60 * 2 * 1000)))
                affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
            activated = true;
            final float xlvl = getXLEVELLevel(invoker());
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + (int) Math.round((15.0 + xlvl) * (CMath.div(proficiency(), 100.0))));
            affectableStats.setArmor(affectableStats.armor() - (int) Math.round((15.0 + xlvl) * (CMath.div(proficiency(), 100.0))));
        } else {
            activated = false;
            hidden = false;
        }
    }
}
