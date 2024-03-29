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
package com.syncleus.aethermud.game.core.intermud.i3.server;

/**
 * com.syncleus.aethermud.game.core.intermud.i3.server.ServerSecurityException
 * Copyright (c) 1996 George Reese
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * An exception for attempts to violate server security
 */

/**
 * This exception gets thrown by the server when some
 * class tries to perform an operation it should not
 * be allowed to perform.
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */
public class ServerSecurityException extends Exception {
    public static final long serialVersionUID = 0;

    /**
     * Constructs a new security excetption with a generic
     * message.
     */
    public ServerSecurityException() {
        this("A general security exception occurred.");
    }

    /**
     * Constructs a new security exception with the
     * specified error message,
     * @param err the error message
     */
    public ServerSecurityException(String err) {
        super(err);
    }
}
