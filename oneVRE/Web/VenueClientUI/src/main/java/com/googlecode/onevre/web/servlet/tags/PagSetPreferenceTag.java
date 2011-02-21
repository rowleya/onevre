/*
 * @(#)PagSetPreferenceTag.java
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

package com.googlecode.onevre.web.servlet.tags;

import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A tag for setting a portlet preference value
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PagSetPreferenceTag extends PortletTag {

    private Log log = LogFactory.getLog(this.getClass());

    // The name of the preference
    private String name = null;

    // The value to store
    private String value = null;

    /**
     * Sets the name of the preference
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the value to set
     * @param value The value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    public void doTag() {
        log.info("IN PagSetPreferenceTag");
        log.info("Setting attribute " + getNamespace() + "pref_" + name + " = " + value);
        @SuppressWarnings("unchecked")
        Vector<String> attr = (Vector<String>) getRequest().getSession().getAttributeNames();
        for (String att : attr) {
            log.info("analyzing: " + att);
        }
        getJspContext().setAttribute(getNamespace() + "pref_" + name, value,
                PageContext.SESSION_SCOPE);
    }
}
