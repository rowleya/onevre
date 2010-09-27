/*
 * @(#)GetBridgesTag.java
 * Created: 3 Sep 2007
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

import java.io.IOException;
import java.util.Vector;


import com.googlecode.onevre.ag.agbridge.RegistryClient;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;

/**
 * A Tag for getting a list of bridges from a bridge registry
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class GetAvailableBridgesTag extends PortletTag {

    // The urls of the registries to query
    private String[] registryUrls = null;

    // The variable that will hold the xml of the bridge
    private String xmlVar = null;

    /**
     * Sets the registries to query
     * @param registryUrls The urls of the registries
     */
    public void setRegistryUrls(String[] registryUrls) {
        this.registryUrls = registryUrls;
    }

    /**
     * Sets the variable that will hold the XML of each bridge
     * @param xmlVar The name of the variable
     */
    public void setXmlVar(String xmlVar) {
        this.xmlVar = xmlVar;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws IOException {
        Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();
        for (int i = 0; i < registryUrls.length; i++) {
            RegistryClient registry = new RegistryClient(registryUrls[i]);
            bridges.addAll(registry.lookupBridges());
        }
        String xml = XMLSerializer.serialize(bridges);
        getJspContext().setAttribute(xmlVar, xml);
    }
}
