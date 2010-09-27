/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.web.servlet.tags;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * A tag to find a jar containing a file
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class FindJarContainingResourceTag extends PortletTag {

    // The root directory to search for the jar in
    private String rootDirectory = null;

    // The name of the resource to find
    private String resourceName = null;

    // The variable to return the jar url in
    private String var = null;

    /**
     * Sets the root directory to search within the web application
     * @param rootDirectory The root directory to search
     */
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    /**
     * Sets the name of the resource to search for
     * @param resourceName The name of the resource
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Sets the variable to return the url of the jar in
     * @param var The name of the variable
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     *
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws IOException {
        String url = "";
        HttpServletRequest request = getRequest();

        PrintWriter pw = new PrintWriter(new File("/tmp/fjcrt.txt"));

        pw.println("FindJarContainingResourceTag  -  ReqURI: " + request.getRequestURI());

        url = url + request.getScheme() + "://";
        url += request.getServerName();
        url += ":" + request.getServerPort();
        url += getRequest().getContextPath() + "/" + rootDirectory + "/";
        pw.println("FindJarContainingResourceTag  -  URL: " + url);
        pw.close();
        String dirUrl =
            ((PageContext) getJspContext()).getServletContext().getRealPath(
                    "/").concat(rootDirectory);
        File dir = new File(dirUrl);
        String[] listing = dir.list();
        boolean found = false;
        for (int i = 0; (i < listing.length) && !found; i++) {
            if (listing[i].endsWith(".jar")) {
                File jarFile = new File(dir, listing[i]);
                JarFile jar = new JarFile(jarFile);
                JarEntry entry = jar.getJarEntry(resourceName);
                if (entry != null) {
                    getJspContext().setAttribute(var, url + listing[i]);
                    found = true;
                }
                jar.close();
            }
        }
    }
}
