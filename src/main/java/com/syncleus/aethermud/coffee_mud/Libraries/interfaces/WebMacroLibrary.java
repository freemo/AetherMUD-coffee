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
package com.planet_ink.coffee_mud.Libraries.interfaces;

import com.planet_ink.coffee_mud.core.exceptions.HTTPRedirectException;
import com.planet_ink.coffee_web.interfaces.HTTPOutputConverter;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;

import java.util.Map;

public interface WebMacroLibrary extends CMLibrary, HTTPOutputConverter {
    public byte[] virtualPageFilter(byte[] data) throws HTTPRedirectException;

    public String virtualPageFilter(String s) throws HTTPRedirectException;

    public StringBuffer virtualPageFilter(StringBuffer s) throws HTTPRedirectException;

    public StringBuffer virtualPageFilter(HTTPRequest request, Map<String, Object> objects, long[] processStartTime, String[] lastFoundMacro, StringBuffer s) throws HTTPRedirectException;

    public String clearWebMacros(StringBuffer s);

    public String parseFoundMacro(StringBuffer s, int i, boolean lookOnly);

    public String clearWebMacros(String s);

    public String copyYahooGroupMsgs(String user, String password, String url, int numTimes, int[] skipList, String journal);
}
