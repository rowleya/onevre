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

package com.googlecode.onevre.web.portlet.tags;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.googlecode.onevre.web.portlet.PagPortlet;
import com.googlecode.onevre.web.ui.VenueClientUI;


/**
 * Extends SimpleTagSupport to provide useful portlet functions
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class PortletTag extends SimpleTagSupport {

    /**
     * Gets the render request
     * @return The render request
     */
    protected RenderRequest getRenderRequest() {
        PageContext pageContext = (PageContext) getJspContext();
        RenderRequest renderRequest =
            (RenderRequest) pageContext.getRequest().getAttribute(
                    "javax.portlet.request");
        return renderRequest;
    }

    /**
     * Gets the render response
     * @return The render response
     */
    protected RenderResponse getRenderResponse() {
        PageContext pageContext = (PageContext) getJspContext();
        RenderResponse renderResponse =
            (RenderResponse) pageContext.getRequest().getAttribute(
                    "javax.portlet.response");
        return renderResponse;
    }

    /**
     * Gets the portlet namespace
     * @return The portlet namespace
     */
    protected String getNamespace() {
        RenderResponse renderResponse = getRenderResponse();
        String namespace = renderResponse.getNamespace();
        return namespace;
    }

    /**
     * Gets the portlet session id
     * @return The portlet namespace
     */
    protected String getSessionId() {
        return getRenderRequest().getPortletSession().getId();
    }

    protected VenueClientUI getVenueClientUI() {
        RenderRequest request = getRenderRequest();
        String namespace = getNamespace();
        PortletSession session = request.getPortletSession();
        return (VenueClientUI) session.getAttribute(
                namespace + PagPortlet.VENUECLIENT_UI_ATTRIBUTE,
                PortletSession.APPLICATION_SCOPE);
    }

    protected HttpServletRequest getRequest() {
        PageContext pageContext = (PageContext) getJspContext();
        return (HttpServletRequest) pageContext.getRequest();
    }
}
