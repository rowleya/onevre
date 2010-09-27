/*
 * @(#)LowBagDefaults.java
 * Created: 11 Feb 2010
 * Version: 1.0
 * Copyright (c) 2005-2010, University of Manchester All rights reserved.
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

package com.googlecode.onevre.bridges.lowbag.common;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.googlecode.vicovre.repositories.rtptype.impl.RtpTypeRepositoryXmlImpl;

/**
 * Default settings for LowBAG bridge.
 * @author Sebastian Starke
 * @version 1.0
 */
public class LowBagDefaults {

    // LowBagDefaults.

    /** Highest port available. */
    public static final int MAX_PORT = 65535;

    /** Default RTP type to use for encoding. */
    public static final int DEFAULT_RTP_TYPE = 77;

    /** Supported RTP types. */
    public static RtpTypeRepositoryXmlImpl typeRepository = null;

    /** Bridge encoding RTP types. */
    public static RtpTypeRepositoryXmlImpl encodingRepository = null;

    /** Address for video client. */
    public static String DEFAULT_VIC_ADDRESS = "224.0.24.124";

    /** Port for video client. */
    public static int DEFAULT_VIC_PORT = 49154;

    /** Address for audio client. */
    public static String DEFAULT_RAT_ADDRESS = "224.0.24.124";

    /** Port for audio client. */
    public static int DEFAULT_RAT_PORT = 49158;

    /** Timeout for XML-RPC queries in milliseconds. */
    public static final int XMLRPC_TIMEOUT = 60000;

    /** Default XML-RPC/control-port used for the bridge server. */
    public static final int DEFAULT_SERVER_CONTROL_PORT = 12345;

    /** Default Bridge Server data port. */
    public static final int DEFAULT_SERVER_DATA_PORT = 12346;

    /** Default address used for the bridge server. */
    public static final String DEFAULT_SERVER_ADDRESS = "localhost";

    /** Default TTL (Time To Live) for packets. */
    public static final int DEFAULT_TTL = 16;

    /** Base port used for client data port calculation. */
    public static final int BASE_PORT = 55000;

    /** Maximum time to next XML-RPC update from client to server. */
    public static final long XMLRPC_UPDATE_TIME = 10000;

    /** Interval for XML-RPC updates to the server. */
    public static final long XMLRPC_UPDATE_INTERVAL = XMLRPC_UPDATE_TIME / 3;

    /** The default frame rate for encoding. */
    public static final float DEFAULT_FRAME_RATE = 5f;

    /** Steps size for scaling the bandwidth. */
    public static final int SCALE_STEP = 10;

    /** Accepted frame rate tolerance before scaling up or down. */
    public static final float FRAME_TOLERNACE = 0.1F;

    /** Interval time to check for XML-RPC updates on the server side. */
    public static final long SERVER_UPDATE_TIME = 10000;

    /** Maximum time until next packet should
     * have been received before deleting the stream. */
    public static final long RTP_UPDATE_TIME = 10000;

    /** Delay until the report is displayed to user in the client interface. */
    public static long REPORT_INIT_DELAY = 10000;

    /** Default time interval in which the client interface will be updated. */
    public static long DEFAULT_REPORT_UDPATE_INTERVAL = 2000;

    /** Default bridge output format rather
     *  the format transmitted to the client. */
    public static String DEFAULT_OUTPUT_FORMAT = "h261as/rtp";

    /** Switch for automated controlling of ratio by the server. */
    public static boolean DEFAULT_AUTO_RATIO = true;

    /** Default path to the codecs file. */
    public static String CODECS_PATH = "/codecs.xml";

    /**
     * Gets all RTP types.
     * @return The RTP types.
     */
    public static RtpTypeRepositoryXmlImpl getAllFormats(){
        try {
            if (typeRepository == null) {
                typeRepository = new RtpTypeRepositoryXmlImpl(
                        "/all-rtptypes.xml");
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return typeRepository;
    }

    /**
     * Gets all RTP types encoded by the bridge.
     * @return RTP types encoded by the bridge.
     */
    public static RtpTypeRepositoryXmlImpl getSupportedFormats() {
        try {
            if (encodingRepository == null) {
                encodingRepository = new RtpTypeRepositoryXmlImpl(
                        "/rtptypes.xml");
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return encodingRepository;
    }
}
