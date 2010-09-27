/*
 * @(#)GetApplicationDescriptionTag.java
 * Created: 20 Mar 2008
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

import java.io.IOException;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.jsp.PageContext;

import com.googlecode.onevre.utils.ConfigFile;

/**
 * A tag for getting the details of a service to execute it
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class GetApplicationDescriptionTag extends PortletTag {

    private String application = null;

    private String nameVar = null;

    private String descriptionVar = null;

    private String nativeVar = null;

    private String jarVar = null;

    private String argsVar = null;

    private String classVar = null;

    private String vendorVar = null;

    /**
     * Sets the argsVar
     * @param argsVar the argsVar to set
     */
    public void setArgsVar(String argsVar) {
        this.argsVar = argsVar;
    }

    /**
     * Sets the classVar
     * @param classVar the classVar to set
     */
    public void setClassVar(String classVar) {
        this.classVar = classVar;
    }

    /**
     * Sets the descriptionVar
     * @param descriptionVar the descriptionVar to set
     */
    public void setDescriptionVar(String descriptionVar) {
        this.descriptionVar = descriptionVar;
    }

    /**
     * Sets the jarVar
     * @param jarVar the jarVar to set
     */
    public void setJarVar(String jarVar) {
        this.jarVar = jarVar;
    }

    /**
     * Sets the nameVar
     * @param nameVar the nameVar to set
     */
    public void setNameVar(String nameVar) {
        this.nameVar = nameVar;
    }

    /**
     * Sets the nativeVar
     * @param nativeVar the nativeVar to set
     */
    public void setNativeVar(String nativeVar) {
        this.nativeVar = nativeVar;
    }

    /**
     * Sets the application
     * @param application the application to set
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Sets the vendor var
     * @param vendorVar The name of the variable
     */
    public void setVendorVar(String vendorVar) {
        this.vendorVar = vendorVar;
    }

    /**
     * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
     */
    public void doTag() throws IOException {
        String filename =
            ((PageContext) getJspContext()).getServletContext().getRealPath(
                "/").concat("/sharedApplications/") + application + "/Application.jar";
        JarFile jar = new JarFile(filename);
        JarEntry svc = jar.getJarEntry("shared.app");
        if (application != null) {
            HashMap<String, HashMap<String, String>> details =
                ConfigFile.read(jar.getInputStream(svc));
            HashMap<String, String> description =
                details.get("SharedApplication");
            getJspContext().setAttribute(nameVar,
                    description.get("name"));
            getJspContext().setAttribute(descriptionVar,
                    description.get("description"));
            getJspContext().setAttribute(classVar,
                    description.get("class"));
            getJspContext().setAttribute(jarVar,
                    description.get("jars").split(";"));
            getJspContext().setAttribute(nativeVar,
                    description.get("native").split(";"));
            getJspContext().setAttribute(argsVar,
                    description.get("args").split(";"));
            getJspContext().setAttribute(vendorVar,
                    description.get("vendor"));
        }
    }
}
