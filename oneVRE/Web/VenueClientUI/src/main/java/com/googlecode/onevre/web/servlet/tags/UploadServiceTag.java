/*
 * @(#)UploadServiceTag.java
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
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.CountInputStream;
import com.googlecode.onevre.utils.Utils;
import com.googlecode.onevre.web.ui.VenueClientUI;

/**
 * Uploads a service
 *
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class UploadServiceTag extends PortletTag {

    private Log log = LogFactory.getLog(this.getClass());

    // The size of the buffer
    private static final int BUFFER_SIZE = 8196;

    private static long uploadCount = 0;

    private static synchronized String getUniqueDir() {
        uploadCount += 1;
        return String.valueOf(System.currentTimeMillis() + uploadCount);
    }

    /**
     *
     * @see javax.servlet.jsp.tagext.SimpleTag#doTag()
     */
    @SuppressWarnings("unchecked")
    public void doTag() {
        HttpServletRequest request = getRequest();
        File servicesDir = new File(((PageContext) getJspContext()).getServletContext().getRealPath("/services/"));
        log.info("Content Length = " + request.getContentLength());
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
            String uri = null;
            iter = items.iterator();
            if (ui != null) {
                // Process the uploaded items
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if (!item.isFormField()) {
                        File uploadDir = new File(servicesDir, getUniqueDir());
                        uploadDir.mkdirs();
                        String fileName = item.getName();
                        long size = item.getSize();
                        ui.showUploadStatus(uri, fileName, size);
                        String error = null;
                        Vector<String> files = new Vector<String>();
                        String name = null;
                        String description = null;
                        try {
                            CountInputStream count = new CountInputStream(item.getInputStream());
                            ZipInputStream zipInput = new ZipInputStream(count);
                            ZipEntry entry = zipInput.getNextEntry();
                            while (entry != null) {
                                BufferedInputStream input = new BufferedInputStream(zipInput);
                                File file = new File(uploadDir, entry.getName());
                                if (file.isDirectory()) {
                                    throw new Exception("Service packages cannot contain directories");
                                }
                                files.add(entry.getName());
                                OutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int bytesRead = 0;
                                while ((bytesRead = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, bytesRead);
                                    ui.setUploadStatus(uri, fileName, size, count.getCount());
                                }
                                output.close();
                                entry = zipInput.getNextEntry();
                            }
                            ui.setUploadStatus(uri, fileName, size, size);
                            // Check that the Service.jar exists
                            File serviceJar = new File(uploadDir, "Service.jar");
                            if (!serviceJar.exists()) {
                                throw new Exception("Service package does not contain Service.jar");
                            }
                            JarFile serviceFile = new JarFile(serviceJar);
                            // Check that the service.svc file exists
                            JarEntry svcFile = serviceFile.getJarEntry("service.svc");
                            if (svcFile == null) {
                                serviceFile.close();
                                throw new Exception("Service.jar does not contain service.svc");
                            }
                            // Check the svc contains the correct values
                            HashMap<String, HashMap<String, String>> service = ConfigFile.read(
                                    serviceFile.getInputStream(svcFile));
                            serviceFile.close();
                            HashMap<String, String> details = service.get("ServiceDescription");
                            if (details == null) {
                                serviceFile.close();
                                throw new Exception("service.svc does not contain ServiceDescription section");
                            }
                            name = Utils.getSection(details, "name", false);
                            description = Utils.getSection(details, "description", false);
                            Utils.getSection(details, "class", false);
                            String[] jars = Utils.getSection(details, "jars", true).split(";");
                            String[] natives = Utils.getSection(details, "native", true).split(";");
                            Utils.getSection(details, "args", true);
                            Utils.getSection(details, "vendor", false);
                            // Check the certificate
                            CodeSigner[] signers = svcFile.getCodeSigners();
                            if (signers.length != 1) {
                                throw new Exception("All jars must be signed by only one certificate");
                            }
                            // Check that all jars and natives are jars and
                            // are signed by the same certificate
                            CertPath signer = signers[0].getSignerCertPath();
                            Utils.checkJarCertificate(serviceJar, signer);
                            files.remove("Service.jar");
                            files = Utils.checkJars(jars, files, uploadDir, signer);
                            files = Utils.checkJars(natives, files, uploadDir, signer);
                            // Check that there are no additional files
                            if (files.size() > 0) {
                                throw new Exception("Additional files found in archive");
                            }
                            // Check if the service already exists
                            File newDir = new File(servicesDir, name);
                            if (newDir.exists()) {
                                // Remove the directory if the certificate is the same
                                JarFile oldJar = new JarFile(new File(newDir, "Service.jar"));
                                JarEntry oldsvc = oldJar.getJarEntry("service.svc");
                                Utils.readEntry(oldJar, oldsvc);
                                CodeSigner[] oldSigners = oldsvc.getCodeSigners();
                                oldJar.close();
                                if (oldSigners == null) {
                                    throw new Exception("Old service was not signed!");
                                }
                                String oldDn = ((X509Certificate) oldSigners[0].getSignerCertPath().
                                        getCertificates().get(0)).getSubjectX500Principal().getName();
                                String newDn = ((X509Certificate) signers[0].getSignerCertPath().
                                        getCertificates().get(0)).getSubjectX500Principal().getName();
                                if (oldDn.equals(newDn)) {
                                    File[] contents = newDir.listFiles();
                                    for (int i = 0; i < contents.length; i++) {
                                        if (!contents[i].delete()) {
                                            throw new Exception("Could not remove old service file " + contents[i]);
                                        }
                                    }
                                    if (!newDir.delete()) {
                                        throw new Exception("Could not remove old service");
                                    }
                                } else {
                                    throw new Exception("A service can only be replaced with one signed by the "
                                            + "same certificate");
                                }
                            }
                            // Move the upload directory to the service one
                            if (!uploadDir.renameTo(newDir)) {
                                throw new Exception("Could not rename directory");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            error = e.getMessage();
                        }
                        if (error != null) {
                            File[] contents = uploadDir.listFiles();
                            for (int i = 0; i < contents.length; i++) {
                                contents[i].delete();
                            }
                            uploadDir.delete();
                            ui.displayMessage(uri, "error", "Error uploading service: " + error);
                        } else {
                            ui.displayMessage(uri, "success", "Service uploaded successfully");
                            String url = request.getScheme() + "://" +  request.getServerName();
                            url += ":" + request.getServerPort();
                            url += request.getContextPath() + "/jsp/executeService.jsp?service=";
                            AGServicePackageDescription desc = new AGServicePackageDescription();
                            desc.setName(name);
                            desc.setDescription(description);
                            desc.setPackageName(name);
                            desc.setLaunchUrl(url + name);
                            getXmlRpcServer().addRequest("addService", new Object[]{name, desc});
                        }
                        ui.hideUploadStatus(uri);
                    }
                }
            }
        }
    }
}
