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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Arrays;


public class Prop_Familiar extends Property {
    protected String displayText = L("Familiarity with an animal");
    protected MOB familiarTo = null;
    protected MOB familiarWith = null;
    protected boolean imthedaddy = false;
    protected Familiar familiarType = Familiar.DOG;
    protected int[] lastBreathablesSet = null;
    protected int[] newBreathablesSet = null;

    @Override
    public String ID() {
        return "Prop_Familiar";
    }

    @Override
    public String name() {
        return "Find Familiar Property";
    }

    @Override
    public String displayText() {
        return displayText;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "is a familiar MOB";
    }

    @Override
    public long flags() {
        return Ability.FLAG_ADJUSTER;
    }

    public boolean removeMeFromFamiliarTo() {
        if (familiarTo != null) {
            final Ability A = familiarTo.fetchEffect(ID());
            if (A != null) {
                familiarTo.delEffect(A);
                /*if(!familiarTo.amDead())
				{
					CMLib.leveler().postExperience(familiarTo,null,null,-50,false);
					familiarTo.tell(L("You`ve just lost 50 experience points for losing your familiar"));
				}*/
                familiarTo.recoverCharStats();
                familiarTo.recoverPhyStats();
            }
        }
        if (familiarWith != null) {
            final Ability A = familiarWith.fetchEffect(ID());
            if (A != null) {
                familiarWith.delEffect(A);
                familiarWith.recoverCharStats();
                familiarWith.recoverPhyStats();
            }
            if (familiarWith.amDead())
                familiarWith.setLocation(null);
            familiarWith.destroy();
            familiarWith.setLocation(null);
        }
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB) {
            if (!(affected instanceof MOB))
                return removeMeFromFamiliarTo();
            if (((familiarTo != null) && (familiarTo.amDestroyed()))
                || ((familiarWith != null) && (familiarWith.amDestroyed()))) {
                familiarTo = null;
                familiarWith = null;
                imthedaddy = false;
            }
            final MOB familiar = (MOB) affected;
            if (familiar.amDead())
                return removeMeFromFamiliarTo();
            if ((!imthedaddy)
                && (familiarTo == null)
                && (familiarWith == null)
                && (CMLib.flags().isInTheGame((MOB) affected, true))
                && (((MOB) affected).amFollowing() != null)
                && (CMLib.flags().isInTheGame(((MOB) affected).amFollowing(), true))) {
                final MOB following = ((MOB) affected).amFollowing();
                familiarWith = (MOB) affected;
                familiarTo = following;
                final Prop_Familiar F = (Prop_Familiar) copyOf();
                F.setSavable(false);
                F.imthedaddy = true;
                F.familiarWith = (MOB) affected;
                F.familiarTo = following; // yes, points to self
                following.delEffect(following.fetchEffect(F.ID()));
                following.addEffect(F);
                following.recoverCharStats();
                following.recoverPhyStats();
            }
            if ((familiarWith != null)
                && (familiarTo != null)
                && ((familiarWith.amFollowing() == null)
                || (familiarWith.amFollowing() != familiarTo))
                && (CMLib.flags().isInTheGame(familiarWith, true)))
                removeMeFromFamiliarTo();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        final MOB familiarWith = this.familiarWith;
        final MOB familiarTo = this.familiarTo;
        if ((familiarWith != null)
            && (familiarTo != null)
            && (familiarWith.location() == familiarTo.location())) {
            switch (familiarType) {
                case DOG:
                    affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_HIDDEN);
                    break;
                case TURTLE:
                    break;
                case CAT:
                    break;
                case BAT:
                    if (((affectableStats.sensesMask() & PhyStats.CAN_NOT_SEE) > 0) && (affected instanceof MOB))
                        affectableStats.setSensesMask(affectableStats.sensesMask() - PhyStats.CAN_NOT_SEE);
                    break;
                case RAT:
                    break;
                case SNAKE:
                    break;
                case OWL:
                    affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_INFRARED);
                    break;
                case RABBIT:
                    break;
                case RAVEN:
                    break;
                case BOA_CONSTRICTOR:
                    break;
                case IGUANA:
                    break;
                case PARROT:
                    if ((affected == familiarWith) && (!familiarWith.isInCombat())) {
                        affectableStats.addAmbiance(L("on @x1`s shoulder", familiarTo.name()));
                        affectableStats.setDisposition(CMath.unsetb(affectableStats.disposition(), PhyStats.IS_FLYING));
                    }
                    break;
                case SEATURTLE:
                    break;
                case SPIDERMONKEY:
                    break;
            }
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (familiarWith != null)
            && (familiarType == Familiar.RABBIT)
            && ((msg.amITarget(familiarWith)) || (msg.amITarget(familiarTo)))
            && (familiarWith.location() == familiarTo.location())
            ) {
            final MOB target = (MOB) msg.target();
            if ((!target.isInCombat())
                && (msg.source().location() == target.location())
                && (msg.source().getVictim() != target)) {
                msg.source().tell(L("You are too much in awe of @x1", target.name(msg.source())));
                if (familiarWith.getVictim() == msg.source())
                    familiarWith.makePeace(true);
                if (familiarTo.getVictim() == msg.source())
                    familiarTo.makePeace(true);
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
            && ((msg.source() == familiarWith) || (msg.source() == familiarTo)))
            removeMeFromFamiliarTo();
        super.executeMsg(host, msg);
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        if (CMath.isInteger(newText))
            familiarType = Familiar.values()[CMath.s_int(newText)];
        else {
            if (newText.trim().length() > 2) {
                newText = newText.trim();
                for (int i = 0; i < Familiar.values().length; i++) {
                    if (Familiar.values()[i].name().equalsIgnoreCase(newText)) {
                        familiarType = Familiar.values()[i];
                        break;
                    }
                    if (Familiar.values()[i].name.equalsIgnoreCase(newText)) {
                        familiarType = Familiar.values()[i];
                        break;
                    }
                }
            }
        }
        displayText = L("(Familiarity with the @x1)", familiarType.name);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        if ((familiarWith != null)
            && (familiarTo != null)
            && (familiarWith.location() == familiarTo.location()))
            switch (familiarType) {
                case DOG:
                    affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) + 1);
                    break;
                case TURTLE: {
                    final int[] breatheables = affectableStats.getBreathables();
                    if (breatheables.length == 0)
                        return;
                    if ((lastBreathablesSet != breatheables) || (newBreathablesSet == null)) {
                        newBreathablesSet = Arrays.copyOf(affectableStats.getBreathables(), affectableStats.getBreathables().length + 2);
                        newBreathablesSet[newBreathablesSet.length - 1] = RawMaterial.RESOURCE_SALTWATER;
                        newBreathablesSet[newBreathablesSet.length - 2] = RawMaterial.RESOURCE_FRESHWATER;
                        Arrays.sort(newBreathablesSet);
                        lastBreathablesSet = breatheables;
                    }
                    affectableStats.setBreathables(newBreathablesSet);
                    affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) + 1);
                    break;
                }
                case CAT:
                    affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 1);
                    if (affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) < 500)
                        affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS, affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
                    break;
                case BAT:
                    affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 1);
                    break;
                case RAT:
                    if (affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) < 500)
                        affectableStats.setStat(CharStats.STAT_SAVE_DISEASE, affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 100);
                    affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) + 1);
                    break;
                case SNAKE:
                    if (affectableStats.getStat(CharStats.STAT_SAVE_POISON) < 500)
                        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
                    affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) + 1);
                    break;
                case OWL:
                    affectableStats.setStat(CharStats.STAT_WISDOM, affectableStats.getStat(CharStats.STAT_WISDOM) + 1);
                    break;
                case RABBIT:
                    affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 1);
                    break;
                case RAVEN:
                    if (affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) < 500)
                        affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD, affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) + 100);
                    affectableStats.setStat(CharStats.STAT_INTELLIGENCE, affectableStats.getStat(CharStats.STAT_INTELLIGENCE) + 1);
                    break;
                case BOA_CONSTRICTOR:
                    affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) + 1);
                    break;
                case IGUANA:
                    if (affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) < 500)
                        affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD, affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) + 100);
                    affectableStats.setStat(CharStats.STAT_INTELLIGENCE, affectableStats.getStat(CharStats.STAT_INTELLIGENCE) + 1);
                    break;
                case PARROT:
                    affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 1);
                    break;
                case SEATURTLE:
                    final int[] breatheables = affectableStats.getBreathables();
                    if (breatheables.length == 0)
                        return;
                    if ((lastBreathablesSet != breatheables) || (newBreathablesSet == null)) {
                        newBreathablesSet = Arrays.copyOf(affectableStats.getBreathables(), affectableStats.getBreathables().length + 2);
                        newBreathablesSet[newBreathablesSet.length - 1] = RawMaterial.RESOURCE_SALTWATER;
                        newBreathablesSet[newBreathablesSet.length - 2] = RawMaterial.RESOURCE_FRESHWATER;
                        Arrays.sort(newBreathablesSet);
                        lastBreathablesSet = breatheables;
                    }
                    affectableStats.setBreathables(newBreathablesSet);
                    affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) + 1);
                    break;
                case SPIDERMONKEY:
                    affectableStats.setStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 1);
                    if (affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) < 500)
                        affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS, affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
                    break;
                default:
                    break;
            }
    }

    protected enum Familiar {
        DOG("dog"),
        TURTLE("turtle"),
        CAT("cat"),
        BAT("bat"),
        RAT("rat"),
        SNAKE("snake"),
        OWL("owl"),
        RABBIT("rabbit"),
        RAVEN("raven"),
        PARROT("parrot"),
        SPIDERMONKEY("spidermonkey"),
        BOA_CONSTRICTOR("boa constrictor"),
        IGUANA("iguana"),
        SEATURTLE("sea turtle");
        public String name;

        private Familiar(String name) {
            this.name = name;
        }
    }
}
