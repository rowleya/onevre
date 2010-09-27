/*
 * @(#)PagButtonTag.java
 * Created: 20-Apr-2007
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * A tag for drawing a PAG Toolbar button
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class PagImageButtonTag extends SimpleTagSupport {

    // The number of borders that will be changed
    private static final int NO_BORDERS = 2;

    // The width of the border in pixels
    private static final int BORDER_WIDTH = 2;

    // The indicator for pixels
    private static final String PIXELS = "px;";

    // A quote mark
    private static final String QUOTE = "\"";

    // The url of the image
    private String url = null;

    // The text to display
    private String text = null;

    // The width of the button in pixels
    private int width = 0;

    // The height of the button in pixels
    private int height = 0;

    // The position of the button from the left in pixels
    private int left = 0;

    // The position of the button from the top in pixels
    private int top = 0;

    // The position of the button from the right in pixels
    private int right = -1;

    // The position of the button from the bottom in pixels
    private int bottom = -1;

    // The id of the button
    private String id = null;

    // The title of the button (i.e. tooltip text)
    private String title = null;

    // The javascript method to call when clicked
    private String action = null;

    /**
     * Sets the height of the button
     * @param height The height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the id of the button
     * @param id The id of the button
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the title of the button
     * @param title The title of the button
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the url of the button image
     * @param url The image url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the text to display on the button
     * @param text The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the width of the button
     * @param width The width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the action of the button
     * @param action The javascript to call when the button is clicked
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Sets the position of the button from the left in pixels
     * @param left The position of the button from the left in pixels
     */
    public void setLeft(int left) {
        this.left = left;
    }

    /**
     * Sets the position of the button from the top in pixels
     * @param top The position of the button from the top in pixels
     */
    public void setTop(int top) {
        this.top = top;
    }

    /**
     * Sets the position of the button from the right in pixels
     * @param right The position of the button from the right in pixels
     */
    public void setRight(int right) {
        this.right = right;
    }

    /**
     * Sets the position of the button from the bottom in pixels
     * @param bottom The position of the button from the bottom in pixels
     */
    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    /**
     * @throws IOException
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    public void doTag() throws IOException {
        JspWriter out = getJspContext().getOut();
        out.print("<div");
        out.print(" onclick=\"" + action + QUOTE);
        if (id != null) {
            out.print(" id=\"" + id + QUOTE);
        }
        if (title != null) {
            out.print(" title=\"" + title + QUOTE);
        }
        out.print(" onmousedown=\"this.style.borderColor ="
                + " '#828177 #f9f8f3 #f9f8f3 #828177';\"");
        out.print(" onmouseup=\"this.style.borderColor ="
                + " '#f9f8f3 #828177 #828177 #f9f8f3';\"");
        out.print(" onmouseout=\"this.style.borderColor = '#f9f8f3 "
                + "#828177 #828177 #f9f8f3';\"");
        out.print(" style=\"width: " + (width - (BORDER_WIDTH * NO_BORDERS))
                + PIXELS);
        out.print(" height: " + (height - (BORDER_WIDTH * NO_BORDERS))
                + PIXELS);
        out.print(" position: absolute;");
        if (bottom != -1) {
            out.print(" bottom: " + bottom + PIXELS);
        } else {
            out.print(" top: " + top + PIXELS);
        }
        if (right != -1) {
            out.print(" right: " + right + PIXELS);
        } else {
            out.print(" left: " + left + PIXELS);
        }
        out.print(" border-width: " + BORDER_WIDTH + PIXELS);
        out.print(" border-style: solid;");
        out.print(" border-color: #f9f8f3 #828177 #828177 #f9f8f3;");
        out.print(" background-color: #ece9d8;");
        out.print(" text-align: center; overflow: hidden;");
        out.print("\">");
        out.print("<table style=\"border-width: 0px; width: 100%;"
                + " height: 100%\">");
        out.print("<tr><td style=\""
                + "text-align: center; width: 100%; "
                + "vertical-align: middle;\">");

        if (url != null) {
            out.print("<img src=\"");
            if (url.startsWith("/")) {
                PageContext pageContext = (PageContext) getJspContext();
                HttpServletRequest request = (HttpServletRequest)
                    pageContext.getRequest();
                out.print(request.getContextPath());
            }
            out.print(url);
            out.print("\" id=\"" + id + "_image\">");
        } else if (text != null) {
            out.print("<span style=\"cursor: default\">" + text + "</span>");
        }
        out.print("</td></tr></table>");
        out.print("</div>");
    }
}
