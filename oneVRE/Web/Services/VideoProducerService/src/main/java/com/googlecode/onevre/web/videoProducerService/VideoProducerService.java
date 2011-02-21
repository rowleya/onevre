/*
 * @(#)VideoProducerService.java
 * Created: 01-June-2007
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
package com.googlecode.onevre.web.videoProducerService;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.server.AGService;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.ag.types.service.parameter.OptionSetParameter;
import com.googlecode.onevre.ag.types.service.parameter.RangeParameter;
import com.googlecode.onevre.ag.types.service.parameter.TextParameter;
import com.googlecode.onevre.ag.types.service.parameter.ValueParameter;
import com.googlecode.onevre.platform.SystemConfig;
import com.googlecode.onevre.utils.OS;
import com.googlecode.onevre.utils.Preferences;

import com.ice.jni.registry.RegDWordValue;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;


/**
 * Video Consumer Service implementation configure and startup vic for viewing
 *
 * @author Anja Le Blanc
 * @version 1.0
 *
 */
public class VideoProducerService extends AGService {

    private static final String H261 = "H261";

    private static final String NTSC = "NTSC";

    private static final String PAL = "PAL";

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

    private static final String STREAMNAME = "Stream Name";

    private static final String PORT = "Port";

    private static final String ENCODING = "Encoding";

    private static final String STANDARD = "Standard";

    private static final String BANDWIDTH = "Bandwidth";

    private static final String FRAMERATE = "Frame Rate";

    private static final String QUALITY = "Quality";

    private static final String VVC = "Videum Video Capture";

    private static final String SOURCE = "Source";

    private static final String HEIGHT = "Height";

    private static final String WIDTH = "Width";

    private static final String NONE = "None";

    private static final String CHMOD = "chmod 777 ";

    private static final int CHANNELS = 1;

    private static final Capability[] CAPABILITY = new Capability[] {
        new Capability(
            Capability.PRODUCER, Capability.VIDEO, H261, 90000, CHANNELS)};

    private StreamDescription streamDescription;

    private static final String[] ENCODINGS = new String[] {"h261"};

    private static final String[] STANDARDS = new String[] {NTSC, PAL};

    private static final TextParameter STREAM_NAME = new TextParameter(
            STREAMNAME, "");

    private static final OptionSetParameter ENCODING_OPTION =
        new OptionSetParameter(
            ENCODING, "h261", ENCODINGS);

    private static final OptionSetParameter STANDARD_OPTION =
        new OptionSetParameter(
            STANDARD, NTSC, STANDARDS);

    private static final RangeParameter BANDWIDTH_OPTION = new RangeParameter(
            BANDWIDTH, 800, 0, 3072);

    private static final RangeParameter FRAME_RATE_OPTION = new RangeParameter(
            FRAMERATE, 24, 1, 30);

    private static final RangeParameter QUALITY_OPTION =
        new RangeParameter(QUALITY, 75, 1, 100);

    private AGParameter port = new TextParameter(PORT, "");

    private String device = null;

    private boolean started = false;

    private String vic;

    private Process process = null;

    /**
     * Creates a new VideoProducerService
     *
     */
    public VideoProducerService() {

        for (int i = 0; i < CAPABILITY.length; i++) {
            this.addCapability(CAPABILITY[i]);
        }

        addConfigurationOption(STREAM_NAME);
        addConfigurationOption(port);
        addConfigurationOption(ENCODING_OPTION);
        addConfigurationOption(STANDARD_OPTION);
        addConfigurationOption(BANDWIDTH_OPTION);
        addConfigurationOption(FRAME_RATE_OPTION);
        addConfigurationOption(QUALITY_OPTION);
    }

    /**
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#getConfiguration()
     */
    public Vector<AGParameter> getConfiguration() {
        Vector<AGParameter> configuration =
            new Vector<AGParameter>(super.getConfiguration());
        Hashtable<String, String[]> resources =
            SystemConfig.getInstance().getResources();
        OptionSetParameter devices = new OptionSetParameter("Capture Device",
                device, new String[0]);
        Iterator<String> iter = resources.keySet().iterator();
        while (iter.hasNext()) {
            String dev = iter.next();
            if (dev != null) {
                devices.setOptions(dev);
            }
        }
        configuration.add(devices);
        return configuration;
    }

    /**
     * @see com.googlecode.onevre.ag.types.server.AGService#setConfiguration(com.googlecode.onevre.ag.types.service.parameter.AGParameter[])
     */
    public void setConfiguration(AGParameter[] config) {
        AGParameter param = new ValueParameter("Capture Device", null);
        Vector<AGParameter> config_vector = new Vector<AGParameter>();
        for (AGParameter par : config) {
            if (par.equals(param)) {
                device = (String) par.getValue();
            } else {
                config_vector.add(par);
            }
        }
        super.setConfiguration(config_vector.toArray(new AGParameter[0]));
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
                    "Can't set RTP Defaults without a valid profile.");
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
        } else if (OS.IS_WINDOWS) {
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
        } else {
            System.err.println("No Support for " + OS.OS);
        }
    }

    // Maps a windows device
    private void mapWinDevice(String deviceStr) {
        int h261width = 352;
        int h261height = 288;
        int devnum = -1;
        final String pattern = ".*([0-9])_Videum.*";

        if (deviceStr == null) {
            return;
        }
        if (deviceStr.indexOf("Videum") != -1) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(deviceStr);
            if (m.matches()) {
                devnum = Integer.getInteger(m.group(1));
            } else {
                if (deviceStr.startsWith(VVC)) {
                    devnum = 0;
                }
            }
            if (devnum >= 0) {
                try {
                    RegistryKey registry =
                        Registry.HKEY_CURRENT_USER.createSubKey(
                            "Software\\Winnov\\Videum\\vic.exe", "",
                            RegistryKey.ACCESS_WRITE);
                    registry.setValue(new RegDWordValue(registry, SOURCE,
                            RegistryValue.REG_DWORD, devnum));
                    registry.setValue(new RegDWordValue(registry, WIDTH,
                            RegistryValue.REG_DWORD, h261width));
                    registry.setValue(new RegDWordValue(registry, HEIGHT,
                            RegistryValue.REG_DWORD, h261height));
                    registry.flushKey();
                    registry.closeKey();
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
    @Override
    public void setStream(StreamDescription description) {
        // configure the stream
        if ((streamDescription != null)
                && description.getLocation().equals(
                        streamDescription.getLocation())) {
            return;
        }
        streamDescription = description;
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
        File dir1 = new File(Preferences.getInstance().getLocalServicesDir());
        String path = this.getClass().getSimpleName().concat("/");
        // settings for os
        try {
            if (OS.IS_WINDOWS) {
                path = path.concat(VICWIN);
                vic = dir1.getCanonicalPath() + "/" + path;
            } else if (OS.IS_LINUX) {
                path = path.concat(VICLINUX);
                vic = dir1.getCanonicalPath() + "/" + path;
                Runtime.getRuntime().exec("chmod 755 " + vic, null, getResourcesDirectory());
            } else if (OS.IS_OSX) {
                path = path.concat(VICMAC);
                vic = dir1.getCanonicalPath() + "/" + path;
                Runtime.getRuntime().exec("chmod 755 " + vic, null, getResourcesDirectory());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String vicDevice;
        String name;
        String email;
        String portstr;
        // enable firewall
        SystemConfig sysConf = SystemConfig.getInstance();
        sysConf.detectCaptureDevices();
        sysConf.appFirewallConfig(vic, true);
        // resolve assigned resource to a device understood by vic
        if (device == null) {
            vicDevice = NONE;
        } else {
            vicDevice = device;
            vicDevice = vicDevice.replaceAll("\\[", "\\\\[");
            vicDevice = vicDevice.replaceAll("\\]", "\\\\]");
        }
        if (OS.IS_WINDOWS) {
            mapWinDevice(device);
        }
        // write vic startup file
        String startupfile = SystemConfig.getInstance().getTempDir() + "/"
                + "VideoProducerService_" + System.currentTimeMillis() + ".vic";
        if (((String) port.getValue()).length() == 0) {
            portstr = NONE;
        } else {
            portstr = (String) port.getValue();
        }
        name = PARTICIPANT;
        email = PARTICIPANT;
        if (this.getProfile() != null) {
            name = this.getProfile().getName();
            email = this.getProfile().getEmail();
        } else {
            // error case
        }
        FileWriter f;
        try {
            f = new FileWriter(startupfile);
            f.write(vicStartup((Integer) BANDWIDTH_OPTION.getValue(),
                    (Integer) FRAME_RATE_OPTION.getValue(),
                    (Integer) QUALITY_OPTION.getValue(),
                    (String) ENCODING_OPTION.getValue(),
                    (String) STANDARD_OPTION.getValue(), vicDevice, name,
                    (String) STREAM_NAME.getValue(), email, portstr));
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
        // replace double backslashes in the startup-file name with single
        // forward
        // slashes (vic will crash otherwise)
        if (OS.IS_WINDOWS) {
            startupfile = startupfile.replaceAll("\\\\", "/");
        }
        // start the service; in this case store command line args in a vector
        Vector<String> options = new Vector<String>();
        options.add("-u");
        options.add(startupfile);
        options.add("-C");
        if (device == null) {
            options.add("\"Video Transmitter\"");
        } else {
            options.add("\"Video Transmitter: " + device + "\"");
        }
        // - set vic window geometry
        options.add("-Xgeometry=384x100");
        // - set number of columns of thumbnails to display
        options.add("-Xtile=2");
        options.add("-X");
        options.add("noMulticastBind=true");
        if (OS.IS_OSX) {
            options.add("-X");
            options.add("transmitOnStartup=1");
        }
        if (streamDescription.getEncryptionFlag() != 0) {
            options.add("-K");
            options.add(streamDescription.getEncryptionKey());
        }
        if ((this.getProfile() != null)
                && (this.getProfile().getPublicId() != null)
                && (this.getProfile().getPublicId().trim().length() > 0)) {
            options.add("-X");
            options.add("site=" + this.getProfile().getPublicId());
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
            options.add(Integer.toString(((MulticastNetworkLocation)
                    streamDescription.getLocation()).getTtl()));
        }
        // add address/port options
        // (these must occur last; don't add options beyond here)
        options.add((streamDescription.getLocation()).getHost() + "/"
                + (streamDescription.getLocation()).getPort());
        // start VIC
        options.add(0, vic);
        try {
            Runtime rt = Runtime.getRuntime();
            System.err.println("Vic start: " + options);
            process = rt.exec(options.toArray(new String[options.size()]), null,
                    getResourcesDirectory());
            // log.log(Level.INFO,"process exit value: " + process.exitValue());
            started = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stops the current service, kills vic
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

    /**
     * Produces a string to write as vic configuration file
     *
     * @return the complete string for VideoProducerService_xxxx.vic
     */
    private String vicStartup(int bandwidth, int framerate, int quality,
            String encoding, String standard, String device, String name,
            String streamname, String email, String port) {
        return "option add Vic.disable_autoplace true startupFile\n"
                + "option add Vic.muteNewSources true startupFile\n"
                + "option add Vic.maxbw 6000 startupFile\n"
                + "option add Vic.bandwidth " + bandwidth + " startupFile\n"
                + "option add Vic.framerate " + framerate + " startupFile\n"
                + "option add Vic.quality " + quality + " startupFile\n"
                + "option add Vic.defaultFormat " + encoding + " startupFile\n"
                + "option add Vic.inputType " + standard + " startupFile\n"
                + "option add Vic.device \"" + device + "\" startupFile\n"
                + "option add Vic.defaultTTL 127 startupFile\n"
                + "option add Vic.rtpName \"" + name + "\" startupFile\n"
                + "option add Vic.rtpEmail \"" + email + "\" startupFile\n"
                + "option add Vic.rtpNote \"" + streamname + "\" startupFile\n"
                + "proc user_hook {} {\n"
                + " 	global videoDevice inputPort transmitButton transmitButtonState V\n"
                + "\n"
                + "$V(session) loopback 1\n"
                + "$V(data-net) loopback 1\n"
                + "$V(ctrl-net) loopback 1\n"
                + "\n"
                + " 	after 200 {\n"
                + "		if { ![winfo exists .menu] } {\n"
                + " 			build.menu\n"
                + " 		}\n"
                + "\n"
                + "		if { ![info exists env(VIC_DEVICE)] } {\n"
                + "			set deviceName \"" + device + "\"\n"
                + "\n"
                + "			foreach v $inputDeviceList {\n"
                + " 				if { [string last $deviceName [$v nickname]] != -1 } {\n"
                + " 					set videoDevice $v\n"
                + " 					select_device $v\n"
                + " 					break\n"
                + " 				}\n"
                + " 			}\n"
                + " 		}\n"
                + " 		set inputPort \"" + port + "\"\n"
                + "		grabber port \"" + port + "\"\n"
                + "\n"
                + "		if { [$transmitButton cget -state] != \"disabled\" } {\n"
                + "			set transmitButtonState 1\n"
                + " 			transmit\n" + "		}\n"
                + "	}\n" + "}\n";
    }
}
