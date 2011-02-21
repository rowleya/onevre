/*
 * @(#)ServicesList.java
 * Created: 25 May 2007
 * Version: 1.0
 * Copyright (c) 2005-2006, University of Manchester All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution. Neither the name of the University of
 * Manchester nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.googlecode.onevre.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http Servlet to Create a list of Services
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class ServicesList extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        if (request.getParameter("nodeService") != null) {
             System.err.println("returning nodeServcie " + request.getParameter("nodeService"));
             String url = "";
              if (request.getProtocol().toLowerCase().startsWith("https")) {
                  url = url.concat("https://");
              } else {
                  url = url.concat("http://");
              }
              url = url.concat(request.getServerName());
              url = url.concat(":" + request.getServerPort() + "/");
              url = url.concat("pag/applications/default");
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write("<message>" + url  + "</message>");
        } else {
            createList(request);
            response.setContentType("text/html");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write("<message>" + serviceList.toString() + "</message>");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        doGet(request, response);
    }

    private Hashtable<String, String> serviceList;

    /**
     * Creates a new ServiceList
     */
    public ServicesList() {
        serviceList = new Hashtable<String, String>();
    }

    private void createList(HttpServletRequest request) {
        String url = "";
        if (request.getProtocol().toLowerCase().startsWith("https")) {
            url = url.concat("https://");
        } else {
            url = url.concat("http://");
        }
        url = url.concat(request.getServerName());
        url = url.concat(":" + request.getServerPort() + "/");
        String dirUrl = this.getServletContext().getRealPath("/").concat("/applications");
        File dir = null;

        dir = new File(dirUrl);
        System.err.println(dirUrl);
        for (String direntry : dir.list()) {
            if (direntry.endsWith(".zip")) {
                System.err.println("listing " + direntry);
                serviceList.put(direntry.substring(0, direntry.lastIndexOf('.')),
                    url.concat("pag/applications/" + direntry));
            }
        }
    }

    /**
     * Return the Service List
     * @return the ServicesList
     */
    public Hashtable<String, String> getServicesList() {
        return serviceList;
    }

}
