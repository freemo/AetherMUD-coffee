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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Behaviors.interfaces.LegalBehavior;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.Law;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Prayer_LowerLaw extends Prayer {
    private final static String localizedName = CMLib.lang().L("Lower Law");

    @Override
    public String ID() {
        return "Prayer_LowerLaw";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    public void possiblyAddLaw(Law L, Vector<String> V, String code) {
        if (L.basicCrimes().containsKey(code)) {
            final String name = L.basicCrimes().get(code)[Law.BIT_CRIMENAME];
            if (!V.contains(name))
                V.add(name);
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> @x1 for knowledge of the lower law here.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Area O = CMLib.law().getLegalObject(mob.location());
                final LegalBehavior B = CMLib.law().getLegalBehavior(mob.location());
                if ((B == null) || (O == null))
                    mob.tell(L("No lower law is established here."));
                else {
                    final Law L = B.legalInfo(O);
                    final Vector<String> crimes = new Vector<String>();
                    possiblyAddLaw(L, crimes, "TRESPASSING");
                    possiblyAddLaw(L, crimes, "ASSAULT");
                    possiblyAddLaw(L, crimes, "MURDER");
                    possiblyAddLaw(L, crimes, "NUDITY");
                    possiblyAddLaw(L, crimes, "ARMED");
                    possiblyAddLaw(L, crimes, "RESISTINGARREST");
                    possiblyAddLaw(L, crimes, "PROPERTYROB");
                    for (final String key : L.abilityCrimes().keySet()) {
                        if (key.startsWith("$"))
                            crimes.add(key.substring(1));
                    }
                    if (L.taxLaws().containsKey("TAXEVASION"))
                        crimes.add(((String[]) L.taxLaws().get("TAXEVASION"))[Law.BIT_CRIMENAME]);
                    for (int x = 0; x < L.bannedSubstances().size(); x++) {
                        final String name = L.bannedBits().get(x)[Law.BIT_CRIMENAME];
                        if (!crimes.contains(name))
                            crimes.add(name);
                    }
                    for (int x = 0; x < L.otherCrimes().size(); x++) {
                        final String name = L.otherBits().get(x)[Law.BIT_CRIMENAME];
                        if (!crimes.contains(name))
                            crimes.add(name);
                    }
                    mob.tell(L("The following lower crimes are divinely revealed to you: @x1.", CMLib.english().toEnglishStringList(crimes.toArray(new String[0]))));
                }
            }
        } else
            beneficialWordsFizzle(mob, null, L("<S-NAME> @x1, but nothing is revealed.", prayWord(mob)));

        return success;
    }
}
