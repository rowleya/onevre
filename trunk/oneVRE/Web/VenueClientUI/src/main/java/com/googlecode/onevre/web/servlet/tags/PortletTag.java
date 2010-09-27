/*
 * @(#)PortletTag.java
 * Created: 24 May 2007
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

package com.googlecode.onevre.web.servlet.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.PagXmlRpcServer;
import com.googlecode.onevre.web.portlet.PagPortlet;
import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 * Extends SimpleTagSupport to provide useful portlet functions
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class PortletTag extends SimpleTagSupport {

	Log log = LogFactory.getLog(this.getClass());

	// The namespace
    private String namespace = null;

    /**
     * Sets the portlet namespace
     * @param namespace The namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets the namespace
     * @return The namespace
     */
    protected String getNamespace() {
        return namespace;
    }

    /**
     * Gets the VenueClientUI
     * @return The venue client ui
     */
    protected VenueClientUI getVenueClientUI() {
    	System.out.println("VenueClientUI namespace = " + namespace );
        return (VenueClientUI) getJspContext().getAttribute(
                namespace + PagPortlet.VENUECLIENT_UI_ATTRIBUTE,
                PageContext.SESSION_SCOPE);
    }

    /**
     * Gets the request
     * @return The request
     */
    protected HttpServletRequest getRequest() {
        PageContext pageContext = (PageContext) getJspContext();
        return (HttpServletRequest) pageContext.getRequest();
    }

    /**
     * Gets the XmlRpcServer
     * @return The XmlRpcServer
     */
    protected PagXmlRpcServer getXmlRpcServer() {
    	log.info("GETTING PagXmlRpcServer :" + namespace + PagPortlet.XMLRPC_SERVER_ATTRIBUTE);
        return (PagXmlRpcServer) getJspContext().getAttribute(
                namespace + PagPortlet.XMLRPC_SERVER_ATTRIBUTE,
                PageContext.SESSION_SCOPE);
    }
}
