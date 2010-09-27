/*
 * @(#)JabberUpdateTag.java
 * Created: 6 Jun 2007
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

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringEscapeUtils;

import com.googlecode.onevre.ag.agclient.venue.JabberClient;
import com.googlecode.onevre.ag.agclient.venue.JabberMessage;
import com.googlecode.onevre.protocols.xmlrpc.common.XMLSerializer;
import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 * Updates the Venue Client when receiving Jabber Messages
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class JabberUpdateTag extends PortletTag {

    // The name of the variable to hold the message
    private String messageVar = null;

    /**
     * Sets the variable that will hold the message
     * @param messageVar The name of the variable
     */
    public void setMessageVar(String messageVar) {
        this.messageVar = messageVar;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws JspException, IOException {
       VenueClientUI ui = getVenueClientUI();
       if (ui.isInVenue()) {
           JabberClient jabberClient = ui.getJabberClient();
           if (jabberClient != null) {
               JabberMessage[] jabberHistory = jabberClient.getHistory();
               if (jabberHistory != null) {
                   for (int i = 0; i < jabberHistory.length; i++) {
                       getJspContext().setAttribute(messageVar,
                               StringEscapeUtils.escapeXml(
                                   XMLSerializer.serialize(jabberHistory[i])));
                       getJspBody().invoke(getJspContext().getOut());
                   }
               }
           }
       }
    }

}
