/*
 * #%L
 * Eureka! Clinical User Webapp
 * %%
 * Copyright (C) 2016 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.eurekaclinical.user.webapp.servlet.worker.useracct;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.user.client.EurekaClinicalUserClient;

import org.eurekaclinical.user.client.comm.User;
import org.eurekaclinical.user.client.comm.LocalUser;

import org.eurekaclinical.user.webapp.servlet.worker.ServletWorker;

/**
 *
 * @author miaoai
 */
public class ListUserAcctWorker implements ServletWorker {

    private final EurekaClinicalUserClient client;

    public ListUserAcctWorker(EurekaClinicalUserClient inClient) {
        this.client = inClient;
    }

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            User user = this.client.getMe();
            Date now = new Date();
            Date expiration = user instanceof LocalUser ? ((LocalUser) user).getPasswordExpiration() : null;
            String passwordExpiration;
            if (expiration != null && now.after(expiration)) {
                passwordExpiration = "Your password has expired. Please change it below.";
            } else {
                passwordExpiration = "";
            }
            req.setAttribute("passwordExpiration", passwordExpiration);

            req.setAttribute("user", user);
            req.getRequestDispatcher("/protected/acct.jsp").forward(req, resp);
        } catch (ClientException ex) {
            throw new ServletException(ex);
        }
    }
}
