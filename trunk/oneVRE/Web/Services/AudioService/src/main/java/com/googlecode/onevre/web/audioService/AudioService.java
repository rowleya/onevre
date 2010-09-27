/*
 * @(#)AudioService.java
 * Created: 17-May-2007
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

package com.googlecode.onevre.web.audioService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.server.AGService;
import com.googlecode.onevre.ag.types.service.parameter.OptionSetParameter;
import com.googlecode.onevre.ag.types.service.parameter.RangeParameter;
import com.googlecode.onevre.platform.SystemConfig;
import com.googlecode.onevre.utils.OS;
import com.ice.jni.registry.RegDWordValue;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;

/**
 * Audio Service implementation configure and start up rat
 *
 * @author Anja Le Blanc
 * @version 1.0
 *
 */
public class AudioService extends AGService {

    private static final String L16 = "L16";

    private static final String L8 = "L8";

    private static final String PCMU = "PCMU";

    private static final String GSM = "GSM";

    private static final String TALK = "Talk";

    private static final String OFF = "Off";

    private static final String ON = "On";

    private static final String FALSE = "False";

    private static final String TRUE = "True";

    private static final String INPUTGAIN = "Input Gain";

    private static final String OUTPUTGAIN = "Output Gain";

    private static final String SILENCESUPPRESSION = "Silence Suppression";

    private static final String AUTOMATIC = "Automatic";

    private static final String MANUAL = "Manual";

    private static final String FORCEAC97 = "Force AC97";

    private static final String RATWIN = "rat.exe";

    private static final String RATMEDIAWIN = "ratmedia.exe"; // not used

    private static final String RATUIWIN = "ratui.exe"; // not used

    private static final String RATKILLWIN = "rat-kill.exe";

    private static final String RATLINUX = "rat-linux";

    private static final String RATEXECLINUX = "rat-4.4.01";

    private static final String RATMEDIALINUX = "rat-4.4.01-media";

    private static final String RATUILINUX = "rat-4.4.01-ui";

    private static final String RATKILLLINUX = "rat-kill-linux";

    private static final String RATMAC = "rat-mac";

    private static final String RATEXECMAC = "rat-4.4.00";

    private static final String RATMEDIAMAC = "rat-4.4.00-media"; // not used

    private static final String RATUIMAC = "rat-4.4.00-ui"; // not used

    private static final String RATKILLMAC = "rat-kill-mac";

    private static final String HOME = "HOME";

    private static final String RTPDEFAULTS = "/.RTPdefaults";

    private static final String RATDEFAULTS = "/.RATdefaults";

    private static final String RTPNAME = "rtpName";

    private static final String RTPEMAIL = "rtpEmail";

    private static final String RTPLOC = "rtpLoc";

    private static final String RTPPHONE = "rtpPhone";

    private static final String RTPNOTE = "rtpNote";

    private static final String COLON = ": ";

    private static final String ASTERISK = "*";

    private static final String OSSISAC97 = "OSS_IS_AC97";

    private static final String L168KMONO = "L16-8K-Mono";

    private static final String L1616KMONO = "L16-16K-Mono";

    private static final String AUDIOINPUTMUTE = "audioInputMute";

    private static final String AUDIOINPUTGAIN = "audioInputGain";

    private static final String AUDIOOUTPUTGAIN = "audioOutputGain";

    private static final String AUDIOSILENCE = "audioSilence";

    private static final Capability[] CAPABILITY = new Capability[] {
            new Capability(Capability.CONSUMER, Capability.AUDIO, L16,
                    16000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, L16, 8000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, L8, 16000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, L8, 8000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, PCMU,
                    16000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, PCMU,
                    8000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, GSM,
                    16000, 1),
            new Capability(Capability.CONSUMER, Capability.AUDIO, GSM, 8000, 1),
            new Capability(Capability.PRODUCER, Capability.AUDIO, L16,
                    16000, 1)};

    private static final OptionSetParameter TALK_OPTION =
        new OptionSetParameter(TALK,
            OFF, new String[] {ON, OFF});

    private static final RangeParameter INPUT_GAIN_OPTION = new RangeParameter(
            INPUTGAIN, 50, 0, 100);

    private static final OptionSetParameter SILENCE_SUPPRESSION_OPTION =
        new OptionSetParameter(
            SILENCESUPPRESSION, OFF, new String[] {OFF, AUTOMATIC, MANUAL});

    private OptionSetParameter forceOSSAC97;

    private RangeParameter outputGain;

    private StreamDescription streamDescription;

    private String rtpDefaultsFile;

    private boolean started = false;

    private Process process = null;

    private String rat;

    private String ratkill;

    private String ratmedia;

    private String ratui;

    /**
     * Creates a new AudioService
     *
     */
    public AudioService() {
        System.err.println("AudioService  construced");
        for (int i = 0; i < CAPABILITY.length; i++) {
            this.addCapability(CAPABILITY[i]);
        }

        if (OS.IS_OSX){
            outputGain=new RangeParameter(OUTPUTGAIN,4,0,100);
        } else {
            outputGain=new RangeParameter(OUTPUTGAIN,50,0,100);
        }

        this.addConfigurationOption(TALK_OPTION);
        this.addConfigurationOption(INPUT_GAIN_OPTION);
        this.addConfigurationOption(outputGain);
        this.addConfigurationOption(SILENCE_SUPPRESSION_OPTION);

        if (OS.IS_LINUX || OS.IS_BSD) {
            forceOSSAC97 = new OptionSetParameter(FORCEAC97, FALSE,
                    new String[] {TRUE, FALSE});
            this.addConfigurationOption(forceOSSAC97);
        }
        this.setIdentity(null);

    }

    /**
     * Set values used by rat for identification
     *
     * @param pf
     *            The profile (ClientProfile)
     */
    private void setRTPDefaults(ClientProfile pf) throws Exception {
        if (pf == null) {
            System.err.println("Invalid profile (None)");
            throw new Exception("Profile not set");
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
                        "SOFTWARE\\Mbone Applications\\common", "",
                        RegistryKey.ACCESS_WRITE);
                if (registry != null) {
                    registry.setValue(new RegStringValue(registry, RTPNAME,
                            pf.getName()));
                    registry.setValue(new RegStringValue(registry, RTPEMAIL,
                            pf.getEmail()));
                    registry.setValue(new RegStringValue(registry, RTPPHONE,
                            pf.getPhoneNumber()));
                    registry.setValue(new RegStringValue(registry, RTPLOC,
                            pf.getLocation()));
                    registry.setValue(new RegStringValue(registry, RTPNOTE,
                            pf.getPublicId()));
                    registry.flushKey();
                    registry.closeKey();
                } else {
                    throw new NullPointerException("Registry key null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Write defaults not file or preferences
     */
    private void writeRatDefaults() throws Exception {
        int mute = 0;
        if (OS.IS_WINDOWS) {
            try {
                RegistryKey registry = Registry.HKEY_CURRENT_USER.createSubKey(
                        "SOFTWARE\\Mbone Applications\\rat", "",
                        RegistryKey.ACCESS_WRITE);

                if (TALK_OPTION.getValue().equals(ON)) {
                    mute = 0;
                } else {
                    mute = 1;
                }
                registry.setValue(new RegDWordValue(registry, AUDIOINPUTMUTE,
                        RegistryValue.REG_DWORD, mute));
                registry.setValue(new RegDWordValue(registry, AUDIOINPUTGAIN,
                        RegistryValue.REG_DWORD,
                        (Integer) INPUT_GAIN_OPTION.getValue()));
                registry.setValue(new RegDWordValue(registry, AUDIOOUTPUTGAIN,
                        RegistryValue.REG_DWORD,
                        (Integer) outputGain.getValue()));
                registry.setValue(new RegStringValue(registry, AUDIOSILENCE,
                        (String) SILENCE_SUPPRESSION_OPTION.getValue()));
                registry.flushKey();
                registry.closeKey();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else if (OS.IS_LINUX ||OS.IS_OSX
                || OS.IS_BSD) {
            String ratDefaultsFile = System.getenv(HOME).concat(RATDEFAULTS);
            Hashtable<String, String> ratDefaults =
                new Hashtable<String, String>();
            try {
                if (new File(ratDefaultsFile).exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(
                            ratDefaultsFile));
                    String line;
                    String[] parts;
                    while ((line = br.readLine()) != null) {
                        parts = line.split(":", 2);
                        if (parts.length != 2) {
                            System.err.println(
                                "Error processing rat defaults line:" + line);
                        } else {
                            ratDefaults.put(parts[0], parts[1]);
                        }
                    }
                    br.close();
                }
                // Update settings
                if (TALK_OPTION.getValue().equals(ON)) {
                    mute = 0;
                } else {
                    mute = 1;
                }
                ratDefaults.put(ASTERISK.concat(AUDIOINPUTMUTE), Double
                        .toString(mute));
                ratDefaults.put(ASTERISK.concat(AUDIOINPUTGAIN),
                        INPUT_GAIN_OPTION
                        .getValue().toString());
                ratDefaults.put(ASTERISK.concat(AUDIOOUTPUTGAIN), outputGain
                        .getValue().toString());
                ratDefaults.put(ASTERISK.concat(AUDIOSILENCE),
                        SILENCE_SUPPRESSION_OPTION.getValue().toString());

                // write file with these settings
                FileWriter ratDefaultsFH;
                Enumeration<String> keys = ratDefaults.keys();
                Enumeration<String> values = ratDefaults.elements();
                ratDefaultsFH = new FileWriter(ratDefaultsFile);
                for (int i = 0; i < ratDefaults.size(); i++) {
                    ratDefaultsFH.write(keys.nextElement().concat(COLON)
                            .concat(values.nextElement()).concat("\n"));
                }
                ratDefaultsFH.flush();
                ratDefaultsFH.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }

        } else {
            System.err.println("Unknown platform: " + OS.OS);
            throw new Exception("Unknown platform: " + OS.OS);
        }

    }

    /**
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#deallocate()
     */
    public void deallocate() {
        // do nothing
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
     * start rat
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
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#start()
     */
    public void start() {
        if (streamDescription == null) {
            return;
        }
        File dir1 = new File(System.getProperty("user.home")
                + "/pag/local_services");
        // setings for os
        String path = this.getClass().getSimpleName().concat("/");
        String ratexec="";
        try {
            if (OS.IS_WINDOWS) {
                String path1 = path.concat(RATWIN);
                rat = dir1.getCanonicalPath() + "/" + path1;
                ratmedia = dir1.getCanonicalPath() + "/"
                        + path.concat(RATMEDIAWIN);
                ratui = dir1.getCanonicalPath() + "/" + path.concat(RATUIWIN);
                path1 = path.concat(RATKILLWIN);
                ratkill = dir1.getCanonicalPath() + "/" + path1;
            } else if (OS.IS_LINUX) {
                rat = dir1.getCanonicalPath() + "/" + path.concat(RATLINUX);
                ratmedia = dir1.getCanonicalPath() + "/" + path.concat(RATMEDIALINUX);
                ratui = dir1.getCanonicalPath() + "/" + path.concat(RATUILINUX);
                ratexec=dir1.getCanonicalPath() + "/" + path.concat(RATEXECLINUX);
                ratkill = dir1.getCanonicalPath() + "/" + path.concat(RATKILLLINUX);
            } else if (OS.IS_OSX) {
                rat = dir1.getCanonicalPath() + "/" + path.concat(RATMAC);
                ratmedia =  dir1.getCanonicalPath() + "/" + path.concat(RATMEDIAMAC);
                ratui = dir1.getCanonicalPath() + "/" + path.concat(RATUIMAC);
                ratexec=dir1.getCanonicalPath() + "/" + path.concat(RATEXECMAC);
                ratkill = dir1.getCanonicalPath() + "/" + path.concat(RATKILLMAC);
            }
            if (OS.IS_OSX) {
                outputGain = new RangeParameter(OUTPUTGAIN, 4, 0, 100);
            } else {
                outputGain = new RangeParameter(OUTPUTGAIN, 50, 0, 100);
            }
            if(OS.IS_WINDOWS==false) {
                Runtime.getRuntime().exec("chmod 755 "+ ratexec,null,getResourcesDirectory());
                Runtime.getRuntime().exec("chmod 755 "+ rat,null,getResourcesDirectory());
                Runtime.getRuntime().exec("chmod 755 "+ ratmedia,null,getResourcesDirectory());
                Runtime.getRuntime().exec("chmod 755 "+ ratui,null,getResourcesDirectory());
                Runtime.getRuntime().exec("chmod 755 "+ ratkill,null,getResourcesDirectory());
                Thread.sleep(1000);
          }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // enable firewall
        SystemConfig sysConf = SystemConfig.getInstance();
        sysConf.appFirewallConfig(rat, true);
        sysConf.appFirewallConfig(ratkill, true);
        sysConf.appFirewallConfig(ratmedia, true);
        sysConf.appFirewallConfig(ratui, true);

        if (OS.IS_OSX) {
            Runtime rt = Runtime.getRuntime();
            try {
                rt.exec("/usr/bin/open -a X11");
                Thread.sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // can't happen -- don't do anything
            }
        }
        try {
            try {
                setRTPDefaults(this.getProfile());
            } catch (Exception e) {
                e.printStackTrace();
            }
            writeRatDefaults();
            if (OS.IS_LINUX || OS.IS_BSD) {
                // note: the forceOSSAC97 attribute will only exist for the
                // above platforms
                if (forceOSSAC97.getValue().equals(TRUE)) {
                    Properties props = new Properties();
                    props.setProperty(OSSISAC97, "1");

                }
            }
            // start the service
            /*
             * in this case, store command line args in a list and let the
             * superclass start the service
             */
            Vector<String> options = new Vector<String>();
            options.add("-C");
            options.add("\"Audio\"");
            // pass public id as site id
            if ((this.getProfile() != null)
                    && (this.getProfile().getPublicId().length() != 0)
                    && OS.IS_OSX) {
                // site id not supported in UCL rat yet, which is used on mac-os.
                options.add("-S");
                options.add(this.getProfile().getPublicId());
            }
            options.add("-f");
            if (OS.IS_OSX) {
                options.add(L168KMONO);
                // prevent mac mash converter
            } else {
                options.add(L1616KMONO);
            }
            /*
             * Check whether the network location has a "type" attribute Note:
             * this condition is only to maintain compatibility between older
             * venue servers creating network locations without this attribute
             * and newer services relying on the attribute; it should be removed
             * when the incompatibilty is gone
             */

            if (streamDescription.getLocation()
                    instanceof MulticastNetworkLocation) {
                options.add("-t");
                options.add(Integer
                        .toString(((MulticastNetworkLocation) streamDescription
                                .getLocation()).getTtl()));
            }
            if (streamDescription.getEncryptionFlag() != 0) {
                options.add("-crypt");
                options.add(streamDescription.getEncryptionKey());
            }
            options.add((streamDescription.getLocation()).getHost() + "/"
                    + (streamDescription.getLocation()).getPort());

            // start RAT
            options.add(0, rat);
            Runtime rt = Runtime.getRuntime();
            System.err.println("rat start: " + options);
            Thread.sleep(5000);
            process = rt.exec(options.toArray(new String[0]), null,
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
        System.err.println("AudioService stop called");
        started = false;
        if (process != null) {
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec(ratkill);
            } catch (Exception e) {
                // Does Nothing
            }
            process.destroy();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Does Nothing
            }
        }
        if (rat != null) {
            SystemConfig sysConf = SystemConfig.getInstance();
            sysConf.appFirewallConfig(rat, false);
            sysConf.appFirewallConfig(ratkill, false);
            sysConf.appFirewallConfig(ratmedia, false);
            sysConf.appFirewallConfig(ratui, false);
        }
    }
}
