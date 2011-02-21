/*
 * @(#)VideoConsumerService.java
 * Created: 30-May-2007
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
package com.googlecode.onevre.web.videoConsumerService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.server.AGService;
import com.googlecode.onevre.platform.SystemConfig;
import com.googlecode.onevre.utils.OS;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;

/**
 * Video Consumer Service implementation configure and startup vic for viewing
 *
 * @author Anja Le Blanc
 * @version 1.0
 *
 */
public class VideoConsumerService extends AGService {

    private static final String CHMOD = "chmod 777 ";

    private static final String H261 = "H261";

    private static final String VICLINUX = "vic-linux";

    private static final String VICWIN = "vic.exe";

    private static final String VICMAC = "vic-mac";

    private static final String HOME = "HOME";

    private static final String RTPDEFAULTS = "/.RTPdefaults";

    private static final String RTPNAME = "rtpName";

    private static final String RTPEMAIL = "rtpEmail";

    private static final String RTPLOC = "rtpLoc";

    private static final String RTPPHONE = "rtpPhone";

    private static final String RTPNOTE = "rtpNote";

    private static final String COLON = ": ";

    private static final String ASTERISK = "*";

    private static final String PARTICIPANT = "Participant";

    private static final int I90000 = 90000;

    private static final int CHANNELS = 1;

    private static final Capability[] CAPABILITY = new Capability[] {
        new Capability(
            Capability.CONSUMER, Capability.VIDEO, H261, I90000, CHANNELS)};

    private StreamDescription streamDescription;

    private String vic;

    private boolean started = false;

    private Process process = null;

    /**
     * Creates a new VideoConsumerService
     *
     */
    public VideoConsumerService() {

        for (int i = 0; i < CAPABILITY.length; i++) {
            this.addCapability(CAPABILITY[i]);
        }
        super.setIdentity(null);
    }

    /**
     * Set values used by vic for identification
     *
     * @param pf
     *            The profile (ClientProfile)
     */
    private void setRTPDefaults(ClientProfile pf) throws Exception {
        String rtpDefaultsFile = "";
        if (pf == null) {
            throw new Exception(
                    "Can't set RTP Defaults without a valid provile.");
        }
        if (OS.IS_LINUX || OS.IS_OSX
                || OS.IS_BSD) {
            try {
                FileWriter rtpDefaultsFH;
                rtpDefaultsFile = System.getenv(HOME).concat(RTPDEFAULTS);
                rtpDefaultsFH = new FileWriter(rtpDefaultsFile);
                rtpDefaultsFH.write(ASTERISK + RTPNAME + COLON + pf.getName()
                        + "\n" + ASTERISK + RTPEMAIL + COLON + pf.getEmail()
                        + "\n" + ASTERISK + RTPLOC + COLON + pf.getLocation()
                        + "\n" + ASTERISK + RTPPHONE + COLON
                        + pf.getPhoneNumber() + "\n" + ASTERISK + RTPNOTE
                        + COLON + pf.getPublicId());
                rtpDefaultsFH.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            try {
                RegistryKey registry = Registry.HKEY_CURRENT_USER.createSubKey(
                        "Software\\Mbone Applications\\common", "",
                        RegistryKey.ACCESS_WRITE);
                registry.setValue(new RegStringValue(registry,
                        ASTERISK + RTPNAME, pf.getName()));
                registry.setValue(new RegStringValue(registry,
                        ASTERISK + RTPNAME, pf.getName()));
                registry.setValue(new RegStringValue(registry,
                        ASTERISK + RTPEMAIL, pf.getEmail()));
                registry.setValue(new RegStringValue(registry,
                        ASTERISK + RTPPHONE, pf.getPhoneNumber()));
                registry.setValue(new RegStringValue(registry,
                        ASTERISK + RTPLOC, pf.getLocation()));
                registry.setValue(new RegStringValue(registry,
                        ASTERISK + RTPNOTE, pf.getPublicId()));
                registry.flushKey();
                registry.closeKey();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#deallocate()
     */
    public void deallocate() {
        // Does Nothing
    }

    /**
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#isStarted()
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Configure the service according to the StreamDescription, and stop and
     * start vic
     *
     * @param description
     *            description of the stream (StreamDescription)
     */
    public void setStream(StreamDescription description) {
        // configure the stream
        if ((streamDescription != null)
                && description.getLocation().equals(
                        streamDescription.getLocation())) {
            return;
        }
        streamDescription = description;
        // if started stop
        if (isStarted()) {
            stop();
        }
        if (isEnabled()) {
            start();
        }
    }

    /**
     * Start service
     */
    public void start() {
        if (streamDescription == null) {
            return;
        }
        File localServicesDir = new File(System.getProperty("user.home")
                + "/pag/local_services");
        String path = this.getClass().getSimpleName().concat("/");

        // setings for os
        try {
            if (OS.IS_WINDOWS) {
                path = path.concat(VICWIN);
                vic = localServicesDir.getCanonicalPath() + "/" + path;
            } else if (OS.IS_LINUX) {
                path = path.concat(VICLINUX);
                vic = localServicesDir.getCanonicalPath() + "/" + path;
                Runtime.getRuntime().exec("chmod 755 " + vic, null, getResourcesDirectory());
            } else if (OS.IS_OSX) {
                path = path.concat(VICMAC);
                vic = localServicesDir.getCanonicalPath() + "/" + path;
                Runtime.getRuntime().exec("chmod 755 " + vic, null, getResourcesDirectory());
                Thread.sleep(1000);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // enable firewall
        SystemConfig sysConf = SystemConfig.getInstance();
        sysConf.appFirewallConfig(vic, true);

        // start the service; in this case store command line args in a vector
        Vector<String> options = new Vector<String>();
        options.add("-C");
        options.add("\"Video Reception\"");
        if (streamDescription.getEncryptionFlag() != 0) {
            options.add("-K");
            options.add(streamDescription.getEncryptionKey());
        }
        /*
         * Check whether the network location has a "type" attribute Note: this
         * condition is only to maintain compatibility between older venue
         * servers creating network locations without this attribute and newer
         * services relying on the attribute; it should be removed when the
         * incompatibilty is gone
         */

        if (streamDescription.getLocation()
                instanceof MulticastNetworkLocation) {
            options.add("-t");
            options.add(Integer
                    .toString(((MulticastNetworkLocation) streamDescription
                            .getLocation()).getTtl()));
        }

        // set name and email on command line, in case rtp defaults haven't
        // been written (to avoid vic prompting for name/email)
        String name = PARTICIPANT;
        String email = PARTICIPANT;
        if (this.getProfile() != null) {
            name = this.getProfile().getName();
            email = this.getProfile().getEmail();
        }

        // write vic startup file
        String startupfile = SystemConfig.getInstance().getTempDir() + "/"
                + "VideoConsumerService_" + System.currentTimeMillis() + ".vic";
        FileWriter f;
        try {
            f = new FileWriter(startupfile);
            f.write(vicStartup(name, email));
            f.flush();
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (!OS.IS_WINDOWS) {
                Runtime.getRuntime().exec(CHMOD + startupfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set some tk resources to customise vic
        // - this is a consumer, so disable device selection in vic
        options.add("-XrecvOnly=1");

        // - set drop time to something sensible
        options.add("-XsitDropTime=5");

        // - set vic window geometry
        options.add("-Xgeometry=500x500");

        // - set number of columns of thumbnails to display
        options.add("-Xtile=2");

        // replace double backslashes in the startup-file name with single
        // forward slashes (vic will crash otherwise)
        if (OS.IS_WINDOWS) {
            startupfile = startupfile.replaceAll("\\\\", "/");
        }

        options.add("-u");
        options.add(startupfile);

        // add address/port options
        // (these must occur last; don't add options beyond here)
        options.add((streamDescription.getLocation()).getHost() + "/"
                + (streamDescription.getLocation()).getPort());


        // start vic
        options.add(0, vic);
        try {
            Runtime rt = Runtime.getRuntime();
            System.err.println("vic start: " + options);
            process = rt.exec(options.toArray(new String[options.size()]), null,
                    getResourcesDirectory());
            started = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#stop()
     */
    public void stop() {
        started = false;
        if (process != null) {
            process.destroy();
        }
        if (vic != null) {
            SystemConfig.getInstance().appFirewallConfig(vic, false);
        }
    }

    /**
     * Sets the identity of the user of the service
     *
     * @param profile
     *            The client profile
     */
    public void setIdentity(ClientProfile profile) {
        super.setIdentity(profile);
        try {
            setRTPDefaults(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String vicStartup(String name, String email) {
        return "option add Vic.rtpName \"" + name + "\" startupFile\n"
                + "option add Vic.rtpEmail \"" + email + "\" startupFile\n"
                + "proc user_hook {} {\n"
                + "     global V\n"
                + "\n"
                + "$V(session) loopback 1\n"
                + "$V(data-net) loopback 1\n"
                + "$V(ctrl-net) loopback 1\n"
                + "\n"
                + "}\n";
    }
}
