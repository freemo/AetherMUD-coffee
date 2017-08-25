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
package com.planet_ink.game.core.intermud.i3.persist;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * com.planet_ink.game.core.intermud.i3.persist.PersistenceException
 * Copyright (c) 1996 George Reese
 * This source code may not be modified, copied,
 * redistributed, or used in any fashion without the
 * express written consent of George Reese.
 * <p>
 * A PersistenceException occurs whenever some error
 * condition interrupts a persistence operation.
 * This class maintains a chain of exceptions so
 * that an easy trace can be done to find out what
 * caused the problem.
 */

/**
 * An exception that gets thrown on persistence operations.
 * If it was triggered by a specific exception, that
 * exception will be added to the exception chain so
 * any class catching this exception can get detailed
 * information on what went wrong.
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public class PersistenceException extends Exception {
    public static final long serialVersionUID = 0;
    private final Exception prior;

    public PersistenceException() {
        this("No reason given.");
    }

    /**
     * Constructs a new PersistenceException with the
     * specified exception explanation.
     * @param reason the reason for the exception
     */
    public PersistenceException(String reason) {
        this(reason, null);
    }

    /**
     * Constructs a new PersistenceException that occured
     * because another exception was encountered during a
     * persistence operation such as a save or restore.
     * @param e the exception causing this exception to be created
     */
    public PersistenceException(Exception e) {
        this("A persistence exception occurred: " + e.getMessage(), e);
    }

    /**
     * Constructs a new PersistenceException based on a previous
     * exception during a persistence operation with the specified
     * exception explanation.
     * @param reason the explanation for the exception
     * @param e the exception causing this exception to be created
     */
    public PersistenceException(String reason, Exception e) {
        super(reason);
        prior = e;
    }

    /**
     * @return the exception which caused this one to be created
     */
    public Exception getPriorException() {
        return prior;
    }

    /**
     * @return the full chain of exceptions leading to this one
     */
    public Enumeration getExceptionChain() {
        return new PersistenceExceptionEnumeration(this);
    }
}

@SuppressWarnings("rawtypes")
final class PersistenceExceptionEnumeration implements Enumeration {
    private Exception exception;

    public PersistenceExceptionEnumeration() {
        this(null);
    }

    public PersistenceExceptionEnumeration(Exception e) {
        exception = e;
    }

    @Override
    public boolean hasMoreElements() {

        if (exception == null)
            return false;
        return true;
    }

    @Override
    public Object nextElement() {
        Exception e;

        if (exception == null)
            throw new NoSuchElementException();
        e = exception;
        if (e instanceof PersistenceException)
            exception = ((PersistenceException) e).getPriorException();
        else exception = null;
        return e;
    }
}

