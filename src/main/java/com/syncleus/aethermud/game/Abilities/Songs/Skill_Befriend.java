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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Common.interfaces.Faction;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Skill_Befriend extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Befriend");
    private static final String[] triggerStrings = I(new String[]{"BEFRIEND"});

    @Override
    public String ID() {
        return "Skill_Befriend";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_INFLUENTIAL;
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(affected)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_VALUE:
                case CMMsg.TYP_SELL:
                case CMMsg.TYP_BUY:
                case CMMsg.TYP_LIST:
                    if (affected instanceof MOB) {
                        MOB M = (MOB) affected;
                        final MOB srcM = msg.source();
                        if (srcM == M.amFollowing()) {
                            srcM.recoverCharStats();
                            srcM.charStats().setStat(CharStats.STAT_CHARISMA, srcM.charStats().getStat(CharStats.STAT_CHARISMA) + 10);
                            msg.addTrailerRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    srcM.recoverCharStats();
                                }

                            });
                        } else if ((text().length() > 0) && (text().startsWith(srcM.Name()))) {
                            String name = text();
                            int amt = 10;
                            int x = text().indexOf('=');
                            if (x > 0) {
                                name = text().substring(0, x).trim();
                                amt = CMath.s_int(text().substring(x + 1).trim());
                            }
                            if (name.equals(srcM.Name())) {
                                srcM.recoverCharStats();
                                srcM.charStats().setStat(CharStats.STAT_CHARISMA, msg.source().charStats().getStat(CharStats.STAT_CHARISMA) + amt);
                                msg.addTrailerRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        srcM.recoverCharStats();
                                    }
                                });
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (ticking instanceof MOB) {
            final MOB mob = (MOB) ticking;
            if ((mob.amFollowing() == null) || (mob.amFollowing().isMonster()) || (!CMLib.flags().isInTheGame(mob.amFollowing(), true))) {
                if (mob.getStartRoom() == null)
                    mob.destroy();
                else if (mob.getStartRoom() != mob.location())
                    CMLib.tracking().wanderAway(mob, false, true);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 1) {
            mob.tell(L("You must specify someone to befriend!"));
            return false;
        }
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (target == mob) {
            mob.tell(L("You are already your own friend."));
            return false;
        }
        if (target.phyStats().level() > mob.phyStats().level() + (mob.phyStats().level() / 10)) {
            mob.tell(L("@x1 is a bit too powerful to befriend.", target.charStats().HeShe()));
            return false;
        }
        if (!CMLib.flags().isMobile(target)) {
            mob.tell(L("You can only befriend fellow travellers."));
            return false;
        }

        if (CMLib.coffeeShops().getShopKeeper(target) != null) {
            mob.tell(L("You cann't befriend a merchant."));
            return false;
        }

        if (!target.isMonster()) {
            mob.tell(L("You need to ask @x1", target.charStats().himher()));
            return false;
        }

        if (target.amFollowing() != null) {
            mob.tell(target, null, null, L("<S-NAME> is already someone elses friend."));
            return false;
        }

        if (!target.charStats().getMyRace().racialCategory().equals(mob.charStats().getMyRace().racialCategory())) {
            mob.tell(target, null, null, L("<S-NAME> is not a fellow @x1.", mob.charStats().getMyRace().racialCategory()));
            return false;
        }

        final Faction F = CMLib.factions().getFaction(CMLib.factions().AlignID());
        if (F != null) {
            final int his = target.fetchFaction(F.factionID());
            final int mine = target.fetchFaction(F.factionID());
            if (F.fetchRange(his) != F.fetchRange(mine)) {
                mob.tell(target, null, null, L("<S-NAME> is not @x1, like yourself.", F.fetchRangeName(mine)));
                return false;
            }
        }

        if ((!auto) && (!CMLib.flags().canSpeak(mob))) {
            mob.tell(L("You can't speak!"));
            return false;
        }

        // if they can't hear the sleep spell, it
        // won't happen
        if ((!auto) && (!CMLib.flags().canBeHeardSpeakingBy(mob, target))) {
            mob.tell(L("@x1 can't hear your words.", target.charStats().HeShe()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = mob.phyStats().level() - target.phyStats().level();
        if (levelDiff > 0)
            levelDiff = (-(levelDiff * levelDiff)) / (1 + super.getXLEVELLevel(mob));
        else
            levelDiff = (levelDiff * (-levelDiff)) / (1 + super.getXLEVELLevel(mob));

        final boolean success = proficiencyCheck(mob, levelDiff, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISYMOVEMENT | (auto ? CMMsg.MASK_ALWAYS : 0), L("<S-NAME> befriend(s) <T-NAME>."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                CMLib.commands().postFollow(target, mob, false);
                CMLib.combat().makePeaceInGroup(mob);
                if (target.amFollowing() != mob)
                    mob.tell(L("@x1 seems unwilling to be your friend.", target.name(mob)));
                else {
                    Ability A = super.beneficialAffect(mob, target, asLevel, Ability.TICKS_FOREVER);
                    if (A != null) {
                        A.makeNonUninvokable();
                        A.makeLongLasting();
                        A.setSavable(true);
                    }
                }
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to befriend <T-NAMESELF>, but fail(s)."));

        return success;
    }

}
