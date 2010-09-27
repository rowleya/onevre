/*
 * @(#)ModeListTag.java
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

import com.googlecode.onevre.ag.types.VenueList;
import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 * Lists the venue list modes
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ModeListTag extends PortletTag {

    // The variable to hold the name of the mode
    private String nameVar = null;

    // The variable to hold the index of the node
    private String indexVar = null;

    // The variable to hold if the node is selected or not
    private String isSelectedVar = null;

    /**
     * Sets the variable that will hold the index of the mode
     * @param indexVar the name of the variable
     */
    public void setIndexVar(String indexVar) {
        this.indexVar = indexVar;
    }

    /**
     * Sets the variable that will hold if the mode is selected
     * @param isSelectedVar the name of the variable
     */
    public void setIsSelectedVar(String isSelectedVar) {
        this.isSelectedVar = isSelectedVar;
    }

    /**
     * Sets the variable that will hold the name of the mode
     * @param nameVar the name of the variable
     */
    public void setNameVar(String nameVar) {
        this.nameVar = nameVar;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws JspException, IOException {
       VenueClientUI ui = getVenueClientUI();
        for (int i = 0; i < VenueList.MODES.length; i++) {
            getJspContext().setAttribute(nameVar, VenueList.MODES[i]);
            getJspContext().setAttribute(indexVar, i);
            getJspContext().setAttribute(isSelectedVar,
                      i == ui.getVenueListMode());
            getJspBody().invoke(getJspContext().getOut());
        }
    }
}
