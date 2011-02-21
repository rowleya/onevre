/*
 * @(#)XmlRpcExecuteTag.java
 * Created: 16 Sep 2007
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.PagXmlRpcServer;
import com.googlecode.onevre.protocols.xmlrpc.xmlrpcserver.XmlRpcThread;

/**
 * Executes an XML-RPC query
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class XmlRpcExecuteSyncTag extends PortletTag {

    private Log log = LogFactory.getLog(this.getClass());

    // The variable that will contain the xml response
    private String var = null;

    // The message to process
    private String message = null;

    /**
     * The message to process
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the variable that will receive the next response.
     * @param var The name of the variable
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() {
        log.info("XmlRpcExecuteSyncTag -- namespace" + getNamespace());
        PagXmlRpcServer server = getXmlRpcServer();
        if (server != null) {
            getJspContext().setAttribute(var, server.handleSyncRequest(getRequest()));
        } else {
            getJspContext().setAttribute(var, XmlRpcThread.getFault(null, 537, "XML-RPC Server not running"));
        }

    }
}
