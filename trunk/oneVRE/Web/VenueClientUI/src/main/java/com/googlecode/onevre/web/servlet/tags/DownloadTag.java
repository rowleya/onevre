/*
 * @(#)DownloadTag.java
 * Created: 25-Jan-2008
 * Derrived from Compendium Download
 * Copyright (c) 2007-2008, University of Manchester All rights reserved.
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

import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.ufsc.SessionFactory;

import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.utils.Download;
import com.googlecode.onevre.web.ui.VenueClientUI;


/**
 * Downloads a data file from a the current venue
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class DownloadTag extends PortletTag {

	Log log = LogFactory.getLog(this.getClass());

    // The size of the buffer
    private static final int BUFFER_SIZE = 8196;

    // The name of the file
    private String filename = null;

    private String venue = null;

    private String selection =null;
    /**
     * Sets the file to download
     * @param filename The id of the file in the Venue
     */
    public void setFile(String filename) {
        this.filename = filename;
    }

    public void setVenue(String venue) {
        this.venue = venue;
log.info ("set Venue : "+ venue);
    }

    /**
     * sets the flag to decide if the filename is a filename or a data item id
     * @param selection
     */
    public void setSelection(String selection){
        this.selection = selection;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    public void doTag() throws JspException {
        VenueClientUI ui = getVenueClientUI();
        if ((ui != null)) {
            HttpServletResponse response = (HttpServletResponse) ((PageContext)getJspContext()).getResponse();
            try {
                Download.startDownloading();
                DataDescription dataItem=ui.downloadDataItem(filename,selection.equals("filename"),venue);

                log.info("uri: " + dataItem.getUri());

                URI uri = new URI(dataItem.getUri());
                URL datafile = new URL((URL)null,uri.toString(),SessionFactory.getURLStreamHandlerFactory().createURLStreamHandler(uri.getScheme()));
                URLConnection conn=datafile.openConnection();
                BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
                OutputStream output = response.getOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                response.setContentType(dataItem.getType());
                response.setHeader("Content-Disposition", "inline; filename=\""
                        + dataItem.getName() + "\";");
                response.flushBuffer();
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.close();
                Download.stopDownloading();
                ui.closeDataConnection(venue);
            } catch (Exception e) {
                Download.stopDownloading();
                ui.closeDataConnection(venue);
                throw new JspException(e);
            }

        }
    }
}
