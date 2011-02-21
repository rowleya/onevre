/*
 * @(#)AGService.java
 * Created: 22-Nov-2006
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

package com.googlecode.onevre.ag.types.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Preferences;

/**
 * An AGTk AGService
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public abstract class AGService {

    private static final int BUFFER_SIZE = 4096;

    // The capabilities of the service
    private Vector<Capability> capabilities = new Vector<Capability>();

    // The configuration parameters
    private Vector<AGParameter> configuration = new Vector<AGParameter>();

    // True if the service is enabled
    private boolean enabled = true;

    // The client profile
    private ClientProfile profile = null;

    // The name of the service (the class name by default)
    private String name = this.getClass().getSimpleName();

    // The uri of the service
    private String id = toString();

    // The package containing the service
    private String packageName = null;

    // The SVC file in the configuration
    private HashMap<String, HashMap<String, String>> svcFile = null;

    // The local resources directory
    private File resourcesDirectory = null;

    /**
     * Creates a new AGService
     *
     */
    public AGService() {
        try {
            svcFile = ConfigFile.read(getClass().getResourceAsStream(
                    "/service.svc"));
            HashMap<String, String> serviceDescription = svcFile.get(
            "ServiceDescription");
            resourcesDirectory = new File(
                Preferences.getInstance().getLocalServicesDir()
                + serviceDescription.get("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected File getResourcesDirectory() {
        return resourcesDirectory;
    }

    // Extracts the service to the local node services directory
    protected void extractResources() throws IOException {
        InputStream res = getClass().getResourceAsStream("/resources.zip");
        if (res != null) {
            File dir = getResourcesDirectory();
            dir.mkdirs();
            ZipInputStream input = new ZipInputStream(res);
            ZipEntry entry = input.getNextEntry();
            while (entry != null) {
                if (entry.isDirectory()) {
                    File newDir = new File(dir, entry.getName());
                    newDir.mkdirs();
                } else {
                    File file = new File(dir, entry.getName());
                    if (!file.exists()
                            || (file.lastModified() != entry.getTime())) {
                        BufferedOutputStream out = new BufferedOutputStream(
                            new FileOutputStream(file));
                        BufferedInputStream in = new BufferedInputStream(input);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int bytesRead = in.read(buffer);
                        while (bytesRead != -1) {
                            out.write(buffer, 0, bytesRead);
                            bytesRead = in.read(buffer);
                        }
                        out.close();
                        if (entry.getTime() != -1) {
                            file.setLastModified(entry.getTime());
                        }
                    }
                }
                entry = input.getNextEntry();
            }
        }
    }

    protected void executeResource(String path, String[] args)
            throws IOException {
        String[] arguments = new String[args.length + 1];
        arguments[0] = path;
        System.arraycopy(args, 0, args, 1, args.length);
        Runtime.getRuntime().exec(arguments, null, getResourcesDirectory());
    }

    protected void addCapability(Capability capability) {
        capabilities.add(capability);
    }

    protected void addConfigurationOption(AGParameter option) {
        System.err.println("Adding config option " + option);
        configuration.add(option);
    }

    protected ClientProfile getProfile() {
        return profile;
    }

    /**
     * Sets the name of the service
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the package name of the service
     * @param packageName The package name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Gets the capabilities of the service
     * @return the capabilities
     */
    public Vector<Capability> getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the configuration
     * @param parameters The configuration parameters
     */
    public void setConfiguration(AGParameter[] parameters) {
        if (parameters != null) {
            try {
                for (int i = 0; i < parameters.length; i++) {
                    AGParameter item = parameters[i];
                    int index = configuration.indexOf(item);
                    if (index != -1) {
                        AGParameter param = configuration.get(index);
                        param.setValue(item.getValue());
                    } else {
                        System.err.println("SetConfiguration: "
                                + "Unrecognized parameter ignored: "
                                + item.getName());
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Gets the current configuration
     * @return The current configuration
     */
    public Vector<AGParameter> getConfiguration() {
        System.err.println("getConfiguration "
                + configuration.toString());
        return configuration;
    }

    /**
     * Configures the given stream for use in the service
     * @param description The stream
     * @return The capability that matches the stream
     */
    public Capability configureStream(StreamDescription description) {
        try {
            Vector<Capability> caps = description.getCapability();
            for (int i = 0; i < capabilities.size(); i++) {
                Capability capability = capabilities.get(i);
                for (int j = 0; j < caps.size(); j++) {
                    Capability cap = (Capability) caps.get(j);
                    if (cap.matches(capability)) {
                        System.err.println("AGService configureStream" + cap);
                        return cap;
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    /**
     * Sets the enabled status of the service
     * @param enabled true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled && isStarted()) {
            stop();
        } else if (enabled && !isStarted()) {
            start();
        }
    }

    /**
     * Returns true if the service is enabled
     * @return true if the service is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Shuts the service down completely
     *
     */
    public void shutdown() {
        stop();
        deallocate();
    }

    /**
     * Sets the identity of the user of the service
     * @param profile The client profile
     */
    public void setIdentity(ClientProfile profile) {
        this.profile = profile;
    }

    /**
     * Gets the name of the service
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the service
     * @return The service description
     */
    public AGServiceDescription getDescription() {
        AGServiceDescription description = new AGServiceDescription();
        description.setName(name);
        description.setId(id);
        description.setCapabilities(capabilities);
        description.setPackageName(packageName);
        return description;
    }

    /**
     * Gets the id
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns 1 if the service is valid
     * @return 1 if the service is valid
     */
    public int isValid() {
        return 1;
    }

    /**
     * Sets a stream
     * @param description The stream description
     */
    public abstract void setStream(StreamDescription description);

    /**
     * Called just before exiting the process
     */
    public abstract void deallocate();

    /**
     * Starts the service
     */
    public abstract void start();

    /**
     * Stops the service
     */
    public abstract void stop();

    /**
     * Returns true if the service has been started
     * @return true if the service has been started, false otherwise
     */
    public abstract boolean isStarted();

}
