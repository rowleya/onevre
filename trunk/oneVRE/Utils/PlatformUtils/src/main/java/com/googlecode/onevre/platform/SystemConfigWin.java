/*
 * @(#)SystemConfigWin.java
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

import com.googlecode.onevre.utils.Preferences;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

/**
 * System configuration for Windows
 * @author Anja Le Blanc
 * @version 1.0
 */
public class SystemConfigWin extends SystemConfig {

    /**
     * Configures the firewall for an application
     * <dl><dt><b>overrides:</b></dt><dd>{@link ag3.platform.SystemConfig#appFirewallConfig(java.lang.String, boolean)}</dd></dl>
     * @param path The application path
     * @param enableFlag True to enable the firewall, false otherwise
     */
    public void appFirewallConfig(String path, boolean enableFlag) {
        String servicePath = Preferences.getInstance().getNodeServicesDir();
        while (servicePath.contains("/")) {
            servicePath = servicePath.replace('/', '\\');
        }
        appFirewallConfig(path, enableFlag, "StandardProfile");
        appFirewallConfig(path, enableFlag, "DomainProfile");
        return;
    }

    private void appFirewallConfig(String path, boolean enableFlag,
            String profile) {
        String  enStr = null;
        String winpath = path;
        while (winpath.contains("/")) {
            winpath = winpath.replace('/', '\\');
        }
        if (enableFlag) {
            enStr = "Enabled";
        } else {
            enStr = "Disabled";
        }

        try {
            RegistryKey key = Registry.HKEY_LOCAL_MACHINE.createSubKey(
                    "SYSTEM\\CurrentControlSet\\Services\\SharedAccess"
                    + "\\Parameters\\FirewallPolicy\\"
                    + profile + "\\AuthorizedApplications\\List", "",
                    RegistryKey.ACCESS_WRITE);
            key.setValue(winpath,
                    new RegStringValue(key, winpath,
                            winpath + ":*:" + enStr + ":AG Tools"));
            key.flushKey();
            key.finalize();
            key.closeKey();

        } catch (RegistryException e) {
            System.err.println(e.getMessage());
        	//e.printStackTrace();
        }

        return;
    }
}
