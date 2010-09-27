/*
 * @(#)OS.java
 * Created: 20 Sep 2007
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

package com.googlecode.onevre.utils;

/**
 * Determination of the platform for the platform dependent routines
 * @author Anja Le Blanc
 * @version 1.0
 */
public final class OS {

    private OS() {
        // Does Nothing
    }

    /**
     * True if the OS is windows, false otherwise
     */
    public static final boolean IS_WINDOWS =
        System.getProperty("os.name").toLowerCase().indexOf("win") != -1;

    /**
     * True if the OS is windows Vista , false otherwise IS_WINDOWS will will
     * be true as well to be used as sub-classification
     */
    public static final boolean IS_WINDOWS_VISTA =
        System.getProperty("os.name").toLowerCase().indexOf("vista") != -1;

    /**
     * True if the OS is linux, false otherwise
     */
    public static final boolean IS_LINUX =
        System.getProperty("os.name").toLowerCase().indexOf("linux") != -1;

    /**
     * True if the OS is Mac OS X, false otherwise
     */
    public static final boolean IS_OSX =
        System.getProperty("os.name").toLowerCase().indexOf("mac os x") != -1;

    /**
     * True if the OS is BSD, false otherwise
     */
    public static final boolean IS_BSD =
        System.getProperty("os.name").toLowerCase().indexOf("bsd") != -1;

    /** the standard file separator */
    public static final String SLASH = System.getProperty("file.separator");


    /**
     * The OS name
     */
    public static final String OS = System.getProperty("os.name");

    /**
     * The OS architecture
     */
   public static final String ARCH = System.getProperty("os.arch");

    // Global env vars

    /**
     * The AGTK location variable
     */
    public static final String AGTK_LOCATION = "AGTK_LOCATION";

    /**
     * The AGTK user variable
     */
    public static final String AGTK_USER = "AGTK_USER";

    /**
     * The AGTk install variable
     */
    public static final String AGTK_INSTALL = "AGTK_INSTALL";

}
