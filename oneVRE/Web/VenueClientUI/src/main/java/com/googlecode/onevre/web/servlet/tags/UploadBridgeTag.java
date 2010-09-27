/*
 * @(#)UploadBridgeTag.java
 * Created: 20-Feb-2008
 * Version: 1.0
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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.CodeSigner;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.googlecode.onevre.utils.CountInputStream;
import com.googlecode.onevre.utils.Utils;
import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 * Uploads a bridge
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class UploadBridgeTag extends PortletTag {

    // The size of the buffer
    private static final int BUFFER_SIZE = 8196;

    /**
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    @SuppressWarnings("unchecked")
	public void doTag() {
        HttpServletRequest request = getRequest();
        File bridgesDir = new File(
            ((PageContext) getJspContext()).getServletContext().getRealPath(
                "/bridges/"));

        System.err.println("Content Length = " + request.getContentLength());
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
                    if (name.equals("namespace")) {
                        setNamespace(value);
                    }
                }
            }
            VenueClientUI ui = getVenueClientUI();
            iter = items.iterator();
            if (ui != null) {
                // Process the uploaded items
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if (!item.isFormField()) {
                        String fileName = item.getName();
                        File tempFile = null;
                        long size = item.getSize();
                        ui.showUploadStatus();
                        ui.setUploadStatus(fileName, size, 0L);
                        String error = null;
                        try {
                            tempFile = File.createTempFile("uploaded", ".jar",
                                    bridgesDir);
                            CountInputStream count = new CountInputStream(
                                    item.getInputStream());
                            BufferedInputStream input =
                                new BufferedInputStream(count);
                            OutputStream output = new BufferedOutputStream(
                                    new FileOutputStream(tempFile));
                            byte[] buffer = new byte[BUFFER_SIZE];
                            int bytesRead = 0;
                            while ((bytesRead = input.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                                ui.setUploadStatus(fileName, size,
                                    count.getCount());
                            }
                            output.close();

                            ui.setUploadStatus(fileName, size, size);

                            JarFile jar = new JarFile(tempFile);
                            Enumeration<JarEntry> entries = jar.entries();
                            String bridgeType = null;
                            CodeSigner signer = null;
                            while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (!entry.getName().startsWith("META-INF")
                                        && !entry.isDirectory()) {
                                    Utils.readEntry(jar, entry);
                                    CodeSigner[] signers =
                                        entry.getCodeSigners();
                                    if ((signers == null)
                                            || signers.length != 1) {
                                        jar.close();
                                        throw new Exception(
                                            "Bridge jars must be signed by one"
                                                + " certificate");
                                    }
                                    if (signer == null) {
                                        signer = signers[0];
                                    } else if (!signers[0].getSignerCertPath().
                                            equals(
                                                signer.getSignerCertPath())) {
                                        jar.close();
                                        throw new Exception(
                                            "Bridge jars must be signed by one"
                                                + " certificate");
                                    }
                                    String name = entry.getName();
                                    if (name.endsWith("/BridgeClientImpl.class")) {
                                        bridgeType = name;
                                    }
                                }
                            }
                            jar.close();
                            if (bridgeType == null) {
                                throw new Exception("Bridge jars must contain"
                                        + "BridgeClientImpl.class in "
                                        + "subpackage of com.googlecode.onevre.ag.agbridge.");
                            }

                            File newFile = new File(bridgesDir, fileName);

                            if (newFile.exists()) {

                                // Remove the file if the certificate is
                                // the same
                                JarFile oldJar = new JarFile(newFile);
                                JarEntry oldclass = oldJar.getJarEntry(
                                        bridgeType);
                                Utils.readEntry(oldJar, oldclass);
                                CodeSigner[] oldSigners =
                                    oldclass.getCodeSigners();
                                oldJar.close();
                                if (oldSigners == null) {
                                    throw new Exception(
                                            "Old bridge was not signed!");
                                }
                                String oldDn = ((X509Certificate)
                                        oldSigners[0].getSignerCertPath().
                                        getCertificates().get(0)).
                                        getSubjectX500Principal().getName();
                                String newDn = ((X509Certificate)
                                        signer.getSignerCertPath().
                                        getCertificates().get(0)).
                                        getSubjectX500Principal().getName();
                                if (oldDn.equals(newDn)) {
                                    if (!newFile.delete()) {
                                        throw new Exception(
                                                "Could not remove old bridge");
                                    }
                                } else {
                                    throw new Exception("A bridge can only be "
                                            + "replaced with one signed by the "
                                            + "same certificate");
                                }
                            }

                            // Move the upload directory to the service one
                            if (!tempFile.renameTo(newFile)) {
                                throw new Exception(
                                        "Could not rename jar");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            error = e.getMessage();
                        }

                        if (error != null) {
                            //tempFile.delete();
                            ui.displayMessage("Error uploading bridge: "
                                    + error);
                        } else {
                            ui.displayMessage("Bridge uploaded successfully");
                        }
                        ui.hideUploadStatus();
                    }
                }
            }
        }
    }
}
