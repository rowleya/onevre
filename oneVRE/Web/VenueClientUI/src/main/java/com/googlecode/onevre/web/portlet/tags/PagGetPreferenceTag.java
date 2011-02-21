/*
 * @(#)PagGetPreferenceTag.java
 * Created: 01-May-2007
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

import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;


/**
 * A tag for getting a portlet preference value
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PagGetPreferenceTag extends PortletTag {

    // The name of the preference
    private String name = null;

    // The name of the variable to store the value in
    private String var = null;

    // The default value to return if none defined
    private String def = null;

    // load the default as alternative value
    private Boolean loadDefault = false;

    /**
     * Sets the name of the preference
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the name of the variable to return the value in
     * @param var The name of the variable
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Sets the default value to return
     * @param def The default value
     */
    public void setDef(String def) {
        this.def = def;
    }

    /**
     * Sets the default value to return
     * @param loadDefault decides if the default value is read
     */
    public void setLoadDefault(String loadDefault) {
        this.loadDefault = Boolean.valueOf(loadDefault);
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    public void doTag() {
        RenderRequest renderRequest = getRenderRequest();
        String namespace = getNamespace();
        String value = (String) renderRequest.getPortletSession().getAttribute(
                namespace + "pref_" + name, PortletSession.APPLICATION_SCOPE);
        if (value == null || loadDefault) {
            PortletPreferences preferences = renderRequest.getPreferences();
            value = preferences.getValue(name, def);
        }
        getJspContext().setAttribute(var, value);
    }
}
