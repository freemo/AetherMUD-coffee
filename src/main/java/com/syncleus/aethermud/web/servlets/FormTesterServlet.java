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

import com.syncleus.aethermud.web.http.HTTPMethod;
import com.syncleus.aethermud.web.http.HTTPStatus;
import com.syncleus.aethermud.web.http.MIMEType;
import com.syncleus.aethermud.web.interfaces.SimpleServlet;
import com.syncleus.aethermud.web.interfaces.SimpleServletRequest;
import com.syncleus.aethermud.web.interfaces.SimpleServletResponse;

import java.io.IOException;


/**
 * Purely for testing POST of form or urlencoded data
 * @author Bo Zimmerman
 *
 */
public class FormTesterServlet implements SimpleServlet {

    @Override
    public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
        response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Override
    public void doPost(SimpleServletRequest request, SimpleServletResponse response) {
        try {
            response.setMimeType(MIMEType.All.html.getType());
            response.getOutputStream().write("<html><body><h1>Form Field Values</h1><br>".getBytes());
            for (final String cookieName : request.getCookieNames())
                response.getOutputStream().write(("Cookie \"" + cookieName + "\": " + request.getCookie(cookieName) + "<br>").getBytes());
            for (final String field : request.getUrlParameters())
                response.getOutputStream().write(("Url Field \"" + field + "\": " + request.getUrlParameter(field) + "<br>").getBytes());
            response.getOutputStream().write("</body></html>".getBytes());
        } catch (final IOException e) {
            response.setStatusCode(500);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void service(HTTPMethod method, SimpleServletRequest request, SimpleServletResponse response) {
        if (method != HTTPMethod.POST)
            response.setStatusCode(HTTPStatus.S405_METHOD_NOT_ALLOWED.getStatusCode());
    }

}
