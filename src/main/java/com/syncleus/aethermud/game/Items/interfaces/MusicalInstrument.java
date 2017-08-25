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
package com.planet_ink.game.Items.interfaces;

/**
 * Musical instruments are typically used by Bard types
 * to make their magic.  Having hard coded types allows
 * skills and such to do thing based on these types.
 * @author Bo Zimmerman
 */
public interface MusicalInstrument extends Item {
    /**
     * Returns the instrument type of this instrument
     * @see InstrumentType
     * @see MusicalInstrument#setInstrumentType(InstrumentType)
     * @see MusicalInstrument#setInstrumentType(String)
     * @see MusicalInstrument#setInstrumentType(int)
     * @return the instrument type enum object of this instrument
     */
    public InstrumentType getInstrumentType();

    /**
     * Sets the instrument type of this instrument
     * @see InstrumentType
     * @see MusicalInstrument#setInstrumentType(InstrumentType)
     * @see MusicalInstrument#setInstrumentType(String)
     * @see MusicalInstrument#setInstrumentType(int)
     * @param typeOrdinal the instrument ordinal of this instrument
     */
    public void setInstrumentType(int typeOrdinal);

    /**
     * Returns the instrument type name of this instrument
     * @see InstrumentType
     * @see MusicalInstrument#setInstrumentType(InstrumentType)
     * @see MusicalInstrument#setInstrumentType(String)
     * @see MusicalInstrument#setInstrumentType(int)
     * @return the instrument type name of this instrument
     */
    public String getInstrumentTypeName();

    /**
     * Sets the instrument type of this instrument
     * @see InstrumentType
     * @see MusicalInstrument#setInstrumentType(InstrumentType)
     * @see MusicalInstrument#setInstrumentType(String)
     * @see MusicalInstrument#setInstrumentType(int)
     * @param type the instrument type enum object of this instrument
     */
    public void setInstrumentType(InstrumentType type);

    /**
     * Sets the instrument type of this instrument
     * @see InstrumentType
     * @see MusicalInstrument#setInstrumentType(InstrumentType)
     * @see MusicalInstrument#setInstrumentType(String)
     * @see MusicalInstrument#setInstrumentType(int)
     * @param typeName the instrument type name for this instrument
     */
    public void setInstrumentType(String typeName);

    /**
     * The enum of instrument types.  These are general
     * categories of instruments, so hopefully nothing is
     * missing.  They are used by skills to identify an
     * instrument category.
     * @author Bo Zimmerman
     *
     */
    public enum InstrumentType {
        CLARINETS,
        CYMBALS,
        DRUMS,
        FLUTES,
        GUITARS,
        HARMONICAS,
        HARPS,
        HORNS,
        OBOES,
        ORGANS,
        PIANOS,
        TROMBONES,
        TRUMPETS,
        TUBAS,
        VIOLINS,
        WOODS,
        XYLOPHONES,
        OTHER_INSTRUMENT_TYPE;
        private static String[] valueNames = null;

        public static String[] valueNames() {
            if (valueNames != null) {
                return valueNames;
            }
            valueNames = new String[InstrumentType.values().length];
            int i = 0;
            for (InstrumentType type : InstrumentType.values())
                valueNames[i++] = type.name();
            return valueNames;
        }
    }
}
