/*
 * @(#)UploadTag.java
 * Created: 20-Feb-2008
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
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.ufsc.SessionFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 * Uploads a data file to the CurrentVenue
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class UploadTag extends PortletTag {

	Log log = LogFactory.getLog(this.getClass());
  // The size of the buffer
    private static final int BUFFER_SIZE = 8196;

    private String venue = null;

    public void setVenue(String venue) {
        this.venue = venue;
    }



    /**
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    @SuppressWarnings("unchecked")
	public void doTag() {
    	String parentId = "";
    	String venueUri = "";
    	String description = "";
    	String expiry = "";
     	log.info("in UPLOADTAG.JAVA" );
        HttpServletRequest request = getRequest();
        log.info("Content Type:" + request.getContentType());
        log.info("Content Length = " + request.getContentLength());
        log.info("parameters:" + request.getParameterMap().toString());
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            // Create a factory for disk-based file items
            FileItemFactory factory = new DiskFileItemFactory();
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // Parse the request
            List<FileItem> items = null;
            try {
                items = upload.parseRequest(request);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }
            // Process the uploaded items
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (item.isFormField()) {
                	String name = item.getFieldName();
                    String value = item.getString();
                    log.info("name: " + name + " value: " + value);
                    if (name.equals("namespace")) {
                        setNamespace(value);
                    }
                    if (name.equals("parentId")){
                    	parentId = value;
                    }
                    if (name.equals("venueUri")){
                    	venueUri = value;
                    }
                    if (name.equals("description")){
                    	description = value;
                    }
                    if (name.equals("expiry")){
                    	expiry = value;
                    }
                }
            }

            VenueClientUI ui = getVenueClientUI();
            iter = items.iterator();
            if ((ui != null)) {
                // Process the uploaded items
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if (!item.isFormField()) {
                        String fileName = item.getName().replaceAll(" ", "%20");
                        log.info("Receiving file : "+ fileName);

                        long size = item.getSize();
                        ui.showUploadStatus(venueUri, fileName, size);
                        String error = null;
                        URI uri = null;
                        try {
                            BufferedInputStream input =
                                new BufferedInputStream(item.getInputStream());
                            uri = ui.uploadDataItem(venueUri, fileName);
                            //, parentId, description, expiry);
                            log.info("Upload to URL: " + uri);
                            log.info("scheme: " + uri.getScheme());
                            URL datafile = new URL((URL) null, uri.toString(),
                                SessionFactory.getURLStreamHandlerFactory().
                                    createURLStreamHandler(uri.getScheme()));
                            URLConnection conn = datafile.openConnection();
                            OutputStream output = conn.getOutputStream();
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int bytesRead = 0;
                            long totalBytes = 0;
                            while ((bytesRead = input.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                                totalBytes += bytesRead;
                                ui.setUploadStatus(venueUri, fileName, size, totalBytes);
                            }
                            output.close();
                            input.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            error = e.getMessage();
                        }
                        ui.setUploadStatus(venueUri, fileName, size, size);
                        if (error != null) {
                            ui.displayMessage(venueUri,"error","Error uploading file: " + error);
                        } else {
                            ui.displayMessage(venueUri,"success", "File uploaded successfully");
                        }
                        ui.hideUploadStatus(venueUri);
                        ui.updateData(venueUri, uri.toString(), fileName, parentId, description, expiry, size);
                        ui.closeDataConnection(venue);
                    }
                }
            }
        }
    }
}
