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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMStrings;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_SummonSeed extends Chant {
    public static final Integer[] NON_SEEDS = {Integer.valueOf(RawMaterial.RESOURCE_ASH),
        Integer.valueOf(RawMaterial.RESOURCE_SOAP),
        Integer.valueOf(RawMaterial.RESOURCE_CHEESE),
        Integer.valueOf(RawMaterial.RESOURCE_BREAD),
        Integer.valueOf(RawMaterial.RESOURCE_CRACKER),
    };
    private final static String localizedName = CMLib.lang().L("Summon Seeds");

    @Override
    public String ID() {
        return "Chant_SummonSeed";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final String s = CMParms.combine(commands, 0);
        final StringBuffer buf = new StringBuffer(L("Seed types known:\n\r"));
        int material = 0;
        String foundShortName = null;
        int col = 0;
        final List<Integer> codes = RawMaterial.CODES.COMPOSE_RESOURCES(RawMaterial.MATERIAL_VEGETATION);
        for (final Integer code : codes) {
            if (!CMParms.contains(Chant_SummonSeed.NON_SEEDS, code)) {
                final String str = RawMaterial.CODES.NAME(code.intValue());
                if (str.toUpperCase().equalsIgnoreCase(s)) {
                    material = code.intValue();
                    foundShortName = CMStrings.capitalizeAndLower(str);
                    break;
                }
                if (col == 4) {
                    buf.append("\n\r");
                    col = 0;
                }
                col++;
                buf.append(CMStrings.padRight(CMStrings.capitalizeAndLower(str), 15));
            }
        }
        if (s.equalsIgnoreCase("list")) {
            mob.tell(buf.toString() + "\n\r\n\r");
            return true;
        }
        if (s.length() == 0) {
            mob.tell(L("Summon what kind of seed?  Try LIST as a parameter..."));
            return false;
        }
        if (foundShortName == null) {
            mob.tell(L("'@x1' is an unknown type of vegetation.", s));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to <S-HIS-HER> hands.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (int i = 2; i < (2 + (adjustedLevel(mob, asLevel) / 4)); i++) {
                    final Item newItem = CMClass.getBasicItem("GenResource");
                    String name = foundShortName.toLowerCase();
                    if (name.endsWith("ies"))
                        name = name.substring(0, name.length() - 3) + "y";
                    if (name.endsWith("s"))
                        name = name.substring(0, name.length() - 1);
                    newItem.setName(CMLib.english().startWithAorAn(name + " seed"));
                    newItem.setDisplayText(L("@x1 is here.", newItem.name()));
                    newItem.setDescription("");
                    newItem.setMaterial(material);
                    newItem.basePhyStats().setWeight(0);
                    newItem.recoverPhyStats();
                    newItem.setMiscText(newItem.text());
                    mob.addItem(newItem);
                }
                mob.location().showHappens(CMMsg.MSG_OK_ACTION, L("Some seeds appear!"));
                mob.location().recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) to <S-HIS-HER> hands, but nothing happens."));

        // return whether it worked
        return success;
    }
}
