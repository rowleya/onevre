/*
 * @(#)GetServicesTag.java
 * Created: 10 Sep 2007
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

import com.googlecode.onevre.ag.types.service.AGBridgeConnectorDescription;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.web.servlet.tags.PortletTag;

/**
 * A tag to get the available AGService implementations
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class GetBridgesTag extends PortletTag {

    private Log log = LogFactory.getLog(this.getClass());

    private String var = null;

    /**
     * Sets the variable that will hold the list
     * @param var The name of the variable
     */
    public void setVar(String var) {
        this.var = var;
    }

    private HashMap<String, AGBridgeConnectorDescription>
        searchForBridges(File dir,
            HashMap<String, AGBridgeConnectorDescription> bridges,
            String baseUrl)
            throws IOException {
        log.info("Searching " + dir.getAbsolutePath() + " for bridges");
        File[] listing = dir.listFiles();
        for (int i = 0; i < listing.length; i++) {
            if (listing[i].isDirectory()) {
                bridges = searchForBridges(listing[i], bridges, baseUrl);
            } else if (listing[i].getName().endsWith(".jar")) {
                JarFile jar = new JarFile(listing[i]);
                JarEntry service = jar.getJarEntry("bridge.dsc");
                if (service != null) {
                    HashMap<String, HashMap<String, String>> details =
                        ConfigFile.read(jar.getInputStream(service));
                    HashMap<String, String> description =
                        details.get("BridgeDescription");
                    AGBridgeConnectorDescription svc =
                        new AGBridgeConnectorDescription();
                    String bridgeName = description.get("name");
                    svc.setName(bridgeName);
                    svc.setDescription(description.get("description"));
                    svc.setLaunchUrl(baseUrl + dir.getName());
                    String bridgeType = description.get("type");
                    svc.setServerType(bridgeType);

                    String launchClass = description.get("class");
                    if (launchClass == null) {
                        launchClass = bridgeName.substring(0, 1).toLowerCase()
                        + bridgeName.substring(1) + "." + bridgeName;
                    }
                    svc.setBridgeClass(launchClass);
                    bridges.put(bridgeType, svc);
                }
                jar.close();
            }
        }
        return bridges;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws IOException {
        HashMap<String, AGBridgeConnectorDescription> bridges =
            new HashMap<String, AGBridgeConnectorDescription>();
        File dir = new File(
            ((PageContext) getJspContext()).getServletContext().getRealPath(
                "/").concat("/bridges"));
        String url = "";
        HttpServletRequest request = getRequest();
        url += request.getScheme() + "://";
        url += request.getServerName();
        url += ":" + request.getServerPort();
        url += request.getContextPath()
            + "/jsp/findJarForBridge.jsp?dir=";
        bridges = searchForBridges(dir, bridges, url);
        log.info("Bridges = " + bridges.toString());
        getJspContext().setAttribute(var,
                StringEscapeUtils.escapeXml(
                        XMLSerializer.serialize(bridges)));
    }
}
