/*
 * @(#)GetApplicationsTag.java
 * Created: 21 Apr 2008
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

package com.googlecode.onevre.web.portlet.tags;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.ag.types.application.AGSharedApplicationDescription;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.web.servlet.tags.PortletTag;

/**
 * A tag to get the available AGSharedApplications implementations
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class GetApplicationsTag extends PortletTag {

    private Log log = LogFactory.getLog(this.getClass());

    private String var = null;

    /**
     * Sets the variable that will hold the list
     * @param var The name of the variable
     */
    public void setVar(String var) {
        this.var = var;
    }

    private HashMap<String, AGSharedApplicationDescription>
        searchForApplications(File dir,
            HashMap<String, AGSharedApplicationDescription> applications,
            String baseUrl)
            throws IOException {
        log.info("Searching " + dir.getAbsolutePath() + " for applications");
        File[] listing = dir.listFiles();
        for (int i = 0; i < listing.length; i++) {
            if (listing[i].isDirectory()) {
                applications = searchForApplications(listing[i], applications,
                        baseUrl);
            } else if (listing[i].getName().endsWith(".jar")) {
                log.debug("Checking if " + listing[i].getName() + " is an application");
                JarFile jar = new JarFile(listing[i]);
                JarEntry application = jar.getJarEntry("shared.app");
                if (application != null) {
                    HashMap<String, HashMap<String, String>> details =
                        ConfigFile.read(jar.getInputStream(application));
                    HashMap<String, String> description =
                        details.get("SharedApplication");
                    AGSharedApplicationDescription app =
                        new AGSharedApplicationDescription();
                    app.setName(description.get("name"));
                    app.setDescription(description.get("description"));
                    app.setMimeType(description.get("mimetype"));
                    app.setUri(baseUrl + dir.getName());
                    app.setPackageName(dir.getName());
                    applications.put(description.get("mimetype"), app);
                }
                jar.close();
            }
        }
        return applications;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws IOException {
        HashMap<String, AGSharedApplicationDescription> applications =
            new HashMap<String, AGSharedApplicationDescription>();
        File dir = new File(
            ((PageContext) getJspContext()).getServletContext().getRealPath(
                "/").concat("/sharedApplications"));
        String url = "";
        HttpServletRequest request = getRequest();
        url += request.getScheme() + "://";
        url += request.getServerName();
        url += ":" + request.getServerPort();
        url += request.getContextPath()
            + "/jsp/executeApplication.jsp?application=";
        applications = searchForApplications(dir, applications, url);
        log.debug("Applications = " + applications.toString());
        getJspContext().setAttribute(var,
                StringEscapeUtils.escapeXml(
                        XMLSerializer.serialize(applications)));
    }
}
