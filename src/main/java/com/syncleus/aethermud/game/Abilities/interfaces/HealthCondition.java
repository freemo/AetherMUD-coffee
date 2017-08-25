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
package com.syncleus.aethermud.game.Abilities.interfaces;

/**
 * HealthCondition is an ability interface to denote those properties,
 * affects, or properties that mean something is wrong with you health-wise.
 */
public interface HealthCondition extends Ability {

    /**
     * Returns a basic description of the health problem, as it would be
     * observed by others.
     * @return a description, in text
     */
    public String getHealthConditionDesc();
}
