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
package com.syncleus.aethermud.web.servlets;

import com.syncleus.aethermud.web.http.HTTPException;
import com.syncleus.aethermud.web.http.HTTPMethod;
import com.syncleus.aethermud.web.http.HTTPStatus;
import com.syncleus.aethermud.web.http.MIMEType;
import com.syncleus.aethermud.web.interfaces.SimpleServlet;
import com.syncleus.aethermud.web.interfaces.SimpleServletRequest;
import com.syncleus.aethermud.web.interfaces.SimpleServletResponse;

import java.io.*;


/**
 * Dumps the web server log into a page 
 * @author Bo Zimmerman
 *
 */
public class LogDumperServlet implements SimpleServlet {

    @Override
    public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
        try {
            response.setMimeType(MIMEType.All.html.getType());
            final File pageFile = new File("web.log");
            if ((!pageFile.exists()) || (!pageFile.canRead()) || (pageFile.length() > Integer.MAX_VALUE))
                throw HTTPException.standardException(HTTPStatus.S404_NOT_FOUND);
            final byte[] fileBuf = new byte[(int) pageFile.length()];
            BufferedInputStream bs = null;
            try {
                bs = new BufferedInputStream(new FileInputStream(pageFile));
                bs.read(fileBuf);
                response.getOutputStream().write("<html><body><pre>".getBytes());
                response.getOutputStream().write(fileBuf);
                response.getOutputStream().write("</pre></body></html>".getBytes());
            } catch (final FileNotFoundException e) {
                request.getLogger().throwing("", "", e);
                // not quite sure how we could get here.
                throw HTTPException.standardException(HTTPStatus.S404_NOT_FOUND);
            } catch (final IOException e) {
                request.getLogger().throwing("", "", e);
                throw HTTPException.standardException(HTTPStatus.S404_NOT_FOUND);
            } finally {
                if (bs != null) {
                    try {
                        bs.close();
                    } catch (final Exception e) {
                    } // java really needs an " i don't care " syntax for exception handling
                }
            }
        } catch (final HTTPException e) {
            try {
                response.getOutputStream().write(e.generateOutput(request).flushToBuffer().array());
            } catch (final Exception e1) {
            }
        }
    }

    @Override
    public void doPost(SimpleServletRequest request, SimpleServletResponse response) {
        response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Override
    public void init() {
    }

    @Override
    public void service(HTTPMethod method, SimpleServletRequest request, SimpleServletResponse response) {
        if (method != HTTPMethod.GET)
            response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
    }

}
