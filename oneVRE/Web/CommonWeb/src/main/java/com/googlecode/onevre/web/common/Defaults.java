/*
 * @(#)PagDefaults.java
 * Created: 27 May 2008
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

package com.googlecode.onevre.web.common;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Default settings for Portal Access Grid
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class Defaults {


    /**
     * file to log usage data (set in Portlet.xml)
     */
    public static String pagLogFile = "logs/pag-usage.log";

    /**
     * file to log usage data (set in Portlet.xml)
     */
    public static String trustedServerFile = "trustedServers.xml";


    /**
     * @param pagLogFile file to log usage data
     */
    public static void setLogFile(String pagLogFile){
        Defaults.pagLogFile=pagLogFile;
    }
    public static void setTrustedServerFile(String trustedServerFile){
        Defaults.trustedServerFile=trustedServerFile;
    }

    /**
     * Writes an entry into the PAG log file
     * @param entry the entry to write
     */
    public static void writeLog(String entry){
        File logFile=new File(Defaults.pagLogFile);
        try {
            if (logFile.getParentFile() != null) {
                logFile.getParentFile().mkdirs();
            }
            logFile.createNewFile();
            PrintWriter log = new PrintWriter(new FileWriter(Defaults.pagLogFile, true));
            log.println("["+DateFormat.getDateTimeInstance().format(new Date())+ "] " + entry);
            log.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    };

    /**
     * default image for a Personal node
     */
    public static String PAG_USER_PARTICIPANT_IMG = "defaultParticipant.png";

    /**
     * default image for a Group node
     */
    public static String PAG_USER_NODE_IMG="defaultNode.png";

    /**
     * default home venue if not set in portlet.xml
     */
    public static String PAG_HOME_VENUE="https://kuipers.ag.manchester.ac.uk:8000/Venues/default";

    /**
     * default home venue if not set in portlet.xml
     */
    public static String LOWBAG_BRIDGE_SERVER="rosie.rcs.manchester.ac.uk";

    /**
     * default node type
     */
    public static String PAG_NODE_TYPE="user";

    /**
     * PAG Version to be checked on download of services
     */
    public static final String PAG_VERSION="1.1";

}
