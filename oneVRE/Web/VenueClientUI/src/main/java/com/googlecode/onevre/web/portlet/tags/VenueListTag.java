/*
 * @(#)VenueListTag.java
 * Created: 14-May-2007
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
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.servlet.jsp.JspException;

import org.xml.sax.SAXException;

import com.googlecode.onevre.ag.types.VenueList;
import com.googlecode.onevre.ag.types.VenueTreeItem;
import com.googlecode.onevre.web.ui.VenueClientUI;


/**
 * A tag for listing the venues
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class VenueListTag extends PortletTag {

    // The value to give to parentVar when dealing with a root variable
    private String rootParentId = null;

    // The name of the variable to hold the parent uri
    private String parentVar = null;

    // The name of the variable to hold the venue name
    private String nameVar = null;

    // The name of the variable to hold the venue uri
    private String uriVar = null;

    // The name of the variable to hold the node id
    private String idVar = null;

    // The name of the variable to hold if the node is expanded
    private String expandedVar = null;

    // The name of the variable to hold if the node is selected
    private String selectedVar = null;

    /**
     * Sets the variable that will hold if the node is selected
     * @param selectedVar True if selected, false otherwise
     */
    public void setSelectedVar(String selectedVar) {
        this.selectedVar = selectedVar;
    }

    /**
     * Sets the variable that will hold if the node is expanded
     * @param expandedVar True if expanded, false otherwise
     */
    public void setExpandedVar(String expandedVar) {
        this.expandedVar = expandedVar;
    }

    /**
     * Sets the variable that will hold the tree node id
     * @param idVar The name of the variable
     */
    public void setIdVar(String idVar) {
        this.idVar = idVar;
    }

    /**
     * Sets the variable that will hold the venue name
     * @param nameVar The name of the variable
     */
    public void setNameVar(String nameVar) {
        this.nameVar = nameVar;
    }

    /**
     * Sets the variable that will hold the venue parent uri
     * @param parentVar The name of the variable
     */
    public void setParentVar(String parentVar) {
        this.parentVar = parentVar;
    }

    /**
     * Sets the id to use as a parent for root nodes
     * @param rootParentId The root parent id
     */
    public void setRootParentId(String rootParentId) {
        this.rootParentId = rootParentId;
    }

    /**
     * Sets the variable that will hold the venue uri
     * @param uriVar The name of the variable
     */
    public void setUriVar(String uriVar) {
        this.uriVar = uriVar;
    }

    // Goes through the tree executing the tag body
    private void processTree(String parentId, Vector<VenueTreeItem> tree)
            throws JspException, IOException, SecurityException,
            IllegalArgumentException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, SAXException {
        for (int i = 0; i < tree.size(); i++) {
            getJspContext().setAttribute(parentVar, parentId);
            getJspContext().setAttribute(nameVar, tree.get(i).getName());
            getJspContext().setAttribute(uriVar, tree.get(i).getUri());
            getJspContext().setAttribute(idVar, tree.get(i).getId());
            getJspContext().setAttribute(expandedVar, tree.get(i).isExpanded());
            getJspContext().setAttribute(selectedVar, tree.get(i).isSelected());
            getJspBody().invoke(getJspContext().getOut());
            if (tree.get(i).hasConnections() || tree.get(i).isExpanded()) {
                processTree(tree.get(i).getId(), tree.get(i).getConnections());
            }
        }
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    public void doTag() throws JspException {
        VenueClientUI ui = getVenueClientUI();
        VenueList venueList = ui.getVenueList();
        if (venueList != null) {
            Vector<VenueTreeItem> venues = venueList.getRoots();
            try {
                processTree(rootParentId, venues);
            } catch (Exception e) {
                e.printStackTrace();
                throw new JspException(e);
            }
        }
    }
}
