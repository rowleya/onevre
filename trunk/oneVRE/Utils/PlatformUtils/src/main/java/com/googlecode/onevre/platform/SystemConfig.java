/*
 * @(#)SystemConfig.java
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

package com.googlecode.onevre.platform;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.RGBFormat;

import com.googlecode.onevre.utils.OS;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;

/**
 * System configuration on the client machine
 * @author Anja Le Blanc
 * @version 1.0
 */
public class SystemConfig {

    private Logger log = Logger.getLogger(SystemConfig.class.getName());

    private static final String TMP_DIR = "java.io.tmpdir";

    private static final String NOTWRITABLE =
        "UserConfig configuration: TempDir is not writable -- ";

    // The only allowed instance
    private static SystemConfig instance = null;

    //  The locator prefix for civil devices
    private static final String CIVIL_LOCATOR = "civil:";

    protected SystemConfig() {
        // Does Nothing
    }

    /**
     * Gets the instance of the system configuration
     * @return The System configuration for this platform
     */
    public static SystemConfig getInstance() {
        if (instance == null) {
            if (OS.IS_LINUX) {
                instance = new SystemConfigLinux();
            } else if (OS.IS_WINDOWS) {
                instance = new SystemConfigWin();
            } else {
                instance = new SystemConfig();
            }
        }
        return instance;
    }

    /**
     * finding the temporary directory
     * @return complete path of temporary directory
     */
    public String getTempDir() {
        String tmp = System.getProperty(TMP_DIR);
        if (!(new File(tmp)).canWrite()) {
            log.log(Level.SEVERE, NOTWRITABLE + tmp);
        }
        return tmp;
    }

    /**
     * determine the amount of free space available in the file system
     *     containing 'path'
     * @param path a path from the file system
     * @return the amount of free space in the filesystem (in bytes)
     */
    public long getFileSystemFreeSpace(String path) {
        return 0;
    }

    /**
     * Gets the resources available
     * @return The resources
     */
    public Hashtable<String, String[]> getResources() {
        Hashtable<String, String[]> resources =
            new Hashtable<String, String[]>();
        Vector<?> list = detectCaptureDevices();
        String[] ports = new String[]{"external-in"};
        for (int i = 0; i < list.size(); i++) {
            CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(i);
            resources.put(info.getName(), ports);
        }

        return resources;
    }

    /**
     * Detects capture devices for use
     * @return the capture devices detected
     */
    public Vector<?> detectCaptureDevices() {

        // Civil devices

        log.info("Lib path = " + System.getenv("LD_LIBRARY_PATH"));
        try {

            if (!OS.IS_OSX) {
                System.loadLibrary("civil");
            }
    //      System.load(getNodeServicesDir().concat(
    //                System.mapLibraryName("civil")));
            CaptureSystemFactory factory =
            DefaultCaptureSystemFactorySingleton.instance();
            CaptureSystem system = factory.createCaptureSystem();
            system.init();
            List<?> list = system.getCaptureDeviceInfoList();
            for (int i = 0; i < list.size(); ++i) {
                com.lti.civil.CaptureDeviceInfo civilInfo =
                    (com.lti.civil.CaptureDeviceInfo) list.get(i);
                CaptureDeviceInfo jmfInfo = new CaptureDeviceInfo(
                        civilInfo.getDescription(),
                        new MediaLocator(
                                CIVIL_LOCATOR + civilInfo.getDeviceID()),
                        new Format[] {new RGBFormat()});
                CaptureDeviceManager.addDevice(jmfInfo);
                log.info("Device: " + jmfInfo.getName() + " "
                        + jmfInfo.getLocator().getProtocol().toString());
            }
        } catch (CaptureException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (UnsatisfiedLinkError e) {
            log.warning("WARNING: civil not found!!");
        }
        Vector<?> list = CaptureDeviceManager.getDeviceList(new RGBFormat());

        return list;
    }

    /**
     * Configures the firewall for an application
     * @param path The application path
     * @param enableFlag True to enable the firewall, false otherwise
     */
    public void appFirewallConfig(String path, boolean enableFlag) {
        // Do Nothing
    }

}
