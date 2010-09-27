/*
 * @(#)AGNodeService.java
 * Created: 02-August-2007
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;


import com.googlecode.onevre.ag.common.interfaces.ServiceManager;
import com.googlecode.onevre.ag.exceptions.BridgeException;
import com.googlecode.onevre.ag.interfaces.BridgeInterface;
import com.googlecode.onevre.ag.interfaces.ServiceManagerInterface;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.service.AGServiceDescription;
import com.googlecode.onevre.ag.types.service.AGServiceManagerDescription;
import com.googlecode.onevre.ag.types.service.AGServicePackageDescription;
import com.googlecode.onevre.ag.types.service.parameter.AGParameter;
import com.googlecode.onevre.ag.types.service.parameter.ValueParameter;


import com.googlecode.onevre.platform.SystemConfig;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.annotation.SoapReturn;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Preferences;

/**
 * The AGNodeService implements the full Node Service.
 * This is the place where all the services a AG node provides are registered.
 * It keeps a list of Service managers which in turn handle the local services.
 * @author Anja Le Blanc
 * @version 1.0
 */
public class AGNodeService extends SoapServable {

    // The service managers
    private Vector<ServiceManagerInterface> serviceManagers =
        new Vector<ServiceManagerInterface>();

    // The system node config directory
    private String sysNodeConfigDir = null;

    // The uri of the node service
    private String uri = "";

    private BridgeInterface clientBridge = null;
    // The current client profile
    private ClientProfile clientProfile = null;

    // The current set of services
    private HashMap<String, AGServicePackageDescription> allServices =
        new HashMap<String, AGServicePackageDescription>();

    private Vector<BridgeDescription> bridges = new Vector<BridgeDescription>();

    private BridgeDescription currentBridge = null;

    private Vector<StreamDescription> streams = new Vector<StreamDescription>();

    private String encryption = null;

    private String pointOfReference = null;

/*  NOT implemented Methods
    static {
        RESULT_NAMES.put("getConfiguration", "argname");
        RESULT_TYPES.put("getConfiguration", null);
        RESULT_NAMES.put("NeedMigrateNodeConfig", "needMigrate");
        RESULT_TYPES.put("NeedMigrateNodeConfig", INTEGER_TYPE);
        RESULT_NAMES.put("getVersion", "version");
        RESULT_TYPES.put("getVersion", STRING_TYPE);
        RESULT_NAMES.put("isValid", "isValid");
        RESULT_TYPES.put("isvalid", INTEGER_TYPE);
    }
*/

    public void setClientBridge(BridgeInterface clientBridge){
    	this.clientBridge = clientBridge;
    }

    /**
     * Creates a new AGNodeService
     * @param allServices The services that are available to this node service
     */
    public AGNodeService(
            HashMap<String, AGServicePackageDescription> allServices) {

        this.allServices = allServices;
        sysNodeConfigDir = Preferences.getInstance().getNodeConfigDir();
        clientProfile = new ClientProfile();
    }


    /**
     * adds a new service manager to the List of Service Managers
     * @param serviceManager The service manager
     * @throws IOException Reasons: <ul>
     * <li>There is already a service Manager in the internal list,
     *  ie. the service manager has already been registered </li>
     * <li>The service manager cannot be reached at the specified URL</li>
     * </ul>
     */
    public void addServiceManager(
            @SoapParameter("serviceManager") ServiceManagerInterface serviceManager
            ) throws IOException {

        //check whether Service Manger has already being added
        if (serviceManagers.contains(serviceManager)) {
            throw new IOException("Service Manager already Exists");
        }

        try {
            serviceManager.setNodeServiceUrl(uri);
            if (pointOfReference != null) {
                serviceManager.setPointOfReference(pointOfReference);
            }
            if (bridges.size() > 0) {
                if (currentBridge != null) {
                    serviceManager.joinBridge(currentBridge);
                } else {
                    serviceManager.runAutomaticBridging();
                }
            }
            serviceManager.setEncryption(encryption);
            serviceManager.setStreams(streams.toArray(new StreamDescription[0]));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Service Manager unreachable");
        }
        serviceManagers.add(serviceManager);
        return;
    }

    /**
     * removes the specified service manager from list of service managers
     * @param serviceManager The Service Manager to be removed
     * @throws Exception The exception is thrown if the service manager can't
     *     be removed ie. it doesn't exist in the internal list of
     *     Service Managers
     */
    public void removeServiceManager(
            @SoapParameter("serviceManager") ServiceManagerInterface serviceManager
            ) throws Exception {
        if (serviceManagers.contains(serviceManager)) {
            serviceManagers.remove(serviceManager);
        } else {
            throw new Exception("AGNodeService.removeServiceManager failed");
        }
    }

    /**
     * gets a list of service managers
     * @return the list of service manager descriptions
     */
    @SoapReturn (
        name = "serviceManagerList"
    )
    public ServiceManagerInterface[] getServiceManagers() {
        return serviceManagers.toArray(
                new ServiceManagerInterface[0]);
    }

    /**
     * gets a list of all services
     * @return services on node service
     * @throws IOException
     * @throws SoapException
     */
    @SoapReturn (
        name = "serviceList"
    )
    public AGServiceDescription[] getServices() throws IOException, SoapException {
        Vector<AGServiceDescription> services =
            new Vector<AGServiceDescription>();
        Iterator <ServiceManagerInterface> col =
            serviceManagers.iterator();
        while (col.hasNext()) {
            ServiceManagerInterface manager = col.next();
            AGServiceDescription[] subset = manager.getServices();
            for (int i = 0; i < subset.length; i++) {
                services.add(subset[i]);
            }
        }
        return services.toArray(new AGServiceDescription[0]);
    }

    /**
     * Requests to join a bridge
     * @param bridgeDescription The description of the bridge to join
     * @throws IOException
     * @throws SoapException
     */
    public void joinBridge(BridgeDescription bridgeDescription)
            throws IOException, SoapException {
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.joinBridge(bridgeDescription);
        }
        currentBridge = bridgeDescription;
    }

    /**
     * enables services by media type
     * @param mediaType string of media type
     * @param enable true to enable, false to disable
     * @throws IOException
     * @throws SoapException
     */
    public void setServiceEnabledByMediaType(
            @SoapParameter("mediaType") String mediaType,
            @SoapParameter("enableFlag") boolean enable
            ) throws IOException, SoapException {
        Iterator <ServiceManagerInterface> col =
            serviceManagers.iterator();
        while (col.hasNext()) {
            ServiceManagerInterface manager = col.next();
            AGServiceDescription[] services = manager.getServices();
            for (int i = 0; i < services.length; i++) {
                Vector<Capability> v = services[i].getCapabilities();
                for (int j = 0; j < v.size(); j++) {
                    Capability c = v.get(j);
                    if (c.getType().compareTo(mediaType) == 0) {
                        manager.enableService(services[i], enable);
                        break;
                    }
                }
            }
        }

    }

    /**
     * stops the services
     * @throws IOException
     * @throws SoapException
     */
    public void stopServices() throws IOException, SoapException {
        Iterator<ServiceManagerInterface> iterator =
            serviceManagers.iterator();
        while (iterator.hasNext()) {
            ServiceManagerInterface manager = iterator.next();
            manager.stopServices();
            manager.shutdown();
        }
    }

    /*
     * configuration methods
     */

    /**
     * adds a stream to the node service
     * @param description description stream description of new stream
     * @throws IOException
     * @throws SoapException
     */
    public void addStream(
            @SoapParameter("argname") StreamDescription description
            ) throws  IOException, SoapException  {
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.addStream(description);
        }
        streams.add(description);
    }

    /**
     * Adds a service
     * @param name The name of the service
     * @param description The service description
     * @throws IOException
     * @throws SoapException
     */
    public void addService(String name,
            AGServicePackageDescription description)
            throws IOException, SoapException {
        if (allServices.containsKey(name)) {
            Iterator<ServiceManagerInterface> iter =
                serviceManagers.iterator();
            while (iter.hasNext()) {
                ServiceManagerInterface manager = iter.next();
                AGServiceDescription[] services = manager.getServices();
                for (int i = 0; i < services.length; i++) {
                    if (services[i].getPackageName().equals(
                            description.getPackageName())) {
                        AGParameter[] config = manager.getServiceConfiguration(
                                services[i]);
                        Vector<AGParameter> params = new Vector<AGParameter>();
                        for (int j = 0; j < config.length; j++) {
                            params.add(config[j]);
                        }
                        manager.removeService(services[i]);
                        manager.addService(description, params.toArray(new AGParameter[0]), clientProfile);
                    }
                }
            }
        }
        allServices.put(name, description);
    }

    /**
     * Removes a stream
     * @param description The stream to remove
     * @throws IOException
     * @throws SoapException
     */
    public void removeStream(
            @SoapParameter("argname") StreamDescription description
            ) throws IOException, SoapException {
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.removeStream(description);
        }
    }

    private HashMap<String, HashMap<String, String>> getDefaultConfig() {
        Vector<String> res = new Vector<String>();
        String videoProducerServices = "";
        Vector devices =
            SystemConfig.getInstance().detectCaptureDevices();
        for (int i = 0; i < devices.size(); i++) {
            CaptureDeviceInfo info =
                (CaptureDeviceInfo) devices.get(i);
            res.add(info.getName());
            videoProducerServices += " videoProducerService" + i;
        }

        HashMap<String, HashMap<String, String>> def =
            new HashMap<String, HashMap<String, String>>();

        HashMap<String, String> node =
            new HashMap<String, String>();
        node.put("servicemanagers", "serviceManager");
        def.put("node", node);

        HashMap<String, String> serviceManager =
            new HashMap<String, String>();
        serviceManager.put("url", "");
        serviceManager.put("name", "");
        serviceManager.put("services",
                "audioService videoConsumerService"
                + videoProducerServices);
        serviceManager.put("builtin", "1");
        def.put("serviceManager", serviceManager);

        HashMap<String, String> audioService =
            new HashMap<String, String>();
        audioService.put("packageName", "AudioService");
        audioService.put("serviceConfig", "audioServiceConfig");
        def.put("audioService", audioService);

        HashMap<String, String> audioServiceConfig =
            new HashMap<String, String>();
        audioServiceConfig.put("Silence Suppression", "Off");
        audioServiceConfig.put("Output Gain", "50");
        audioServiceConfig.put("Force AC97", "Off");
        audioServiceConfig.put("Talk", "Off");
        audioServiceConfig.put("Input Gain", "50");
        def.put("audioServiceConfig", audioServiceConfig);

        HashMap<String, String> videoConsumerService =
            new HashMap<String, String>();
        videoConsumerService.put("packageName",
                "VideoConsumerService");
        videoConsumerService.put("serviceConfig",
                "videoConsumerServiceConfig");
        def.put("videoConsumerService", videoConsumerService);

        HashMap<String, String> videoConsumerServiceConfig =
            new HashMap<String, String>();
        videoConsumerServiceConfig.put("Thumbnail Columns", "2");
        videoConsumerServiceConfig.put("Processor Usage", "All");
        def.put("videoConsumerServiceConfig",
                videoConsumerServiceConfig);

        for (int i = 0; i < res.size(); i++) {
            HashMap<String, String> videoProducerService =
                new HashMap<String, String>();
            videoProducerService.put("packageName",
                    "VideoProducerService");
            videoProducerService.put("serviceConfig",
                    "videoProducerService" + i + "Config");
            def.put("videoProducerService" + i,
                    videoProducerService);

            HashMap<String, String> videoProducerServiceConfig =
                new HashMap<String, String>();
            videoProducerServiceConfig.put("Frame Rate", "24");
            videoProducerServiceConfig.put("Encoding", "h261");
            videoProducerServiceConfig.put("Standard", "PAL");
            videoProducerServiceConfig.put("Bandwidth", "800");
            videoProducerServiceConfig.put("Processor Usage",
                    "All");
            videoProducerServiceConfig.put("Port", "external-in");
            videoProducerServiceConfig.put("Quality", "75");
            videoProducerServiceConfig.put("Stream Name",
                    res.get(i));
            videoProducerServiceConfig.put("Capture Device", res.get(i));
            def.put("videoProducerService" + i + "Config",
                    videoProducerServiceConfig);
        }
        return def;
    }

    /**
     * checks the sanity of a node configuration file in the local file system
     * @param config name of the file containing the description of node
     * @return true if sane; false otherwise
     * @throws IOException
     */
    public boolean sanityCheckConfig(String config) throws IOException{
        String configFile = sysNodeConfigDir + config;

        HashMap<String, HashMap<String, String>> fileTree = null;
        fileTree = ConfigFile.read(configFile);

        HashMap<String, String> node = fileTree.get("node");
        if (node == null) {
            System.err.println("no 'node' in file");
            return false;
        }
        String value = node.get("servicemanagers");
        if (value == null) {
            System.err.println("no 'servicemanagers' in node");
            return false;
        }
        String[] serviceManagerList = value.split(" ");
        for (int i = 0; i < serviceManagerList.length; i++) {
            HashMap<String, String> sm = fileTree.get(serviceManagerList[i]);
            if (sm==null){
                return false;
            }
            String url = sm.get("url");
            if (sm.get("builtin")==null) {
                return false;
            }
            boolean builtIn = Integer.parseInt(sm.get("builtin")) == 1;
            if (!builtIn) {
                if (url == null){
                    return false;
                }
            }
            // Service List
            String[] services = sm.get("services").split(" +");
            for (int j = 0; j < services.length; j++) {
                if (services[j].trim().length() == 0) {
                    continue;
                }
                HashMap<String, String> service = fileTree.get(services[j]);
                if (service == null) {
                    System.err.println("no service: '"+ services[j] + "' in node");
                    return false;
                }
                String packageName = service.get("packageName");
                if (packageName== null){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * loads a node configuration from a file in the local file system
     * @param config name of the file containing the description of node
     * @throws IOException
     * @throws SoapException
     */
    public void loadConfiguration(String config)
            throws IOException, SoapException {

        Iterator<ServiceManagerInterface> iterator =
            serviceManagers.iterator();
        while (iterator.hasNext()) {
            ServiceManagerInterface manager = iterator.next();
            if (manager instanceof AGServiceManager) {
                manager.stopServices();
                manager.shutdown();
            } else {
                manager.removeServices();
            }
        }
        serviceManagers.clear();

        String configFile = sysNodeConfigDir + config;

        HashMap<String, HashMap<String, String>> fileTree = null;

        if (!(new File(configFile)).exists()) {
            if (config.equals("default")) {
                fileTree = getDefaultConfig();
            } else {
                throw new FileNotFoundException(
                    "Configuration file does not exist " + configFile);
            }
        } else {
            if (sanityCheckConfig(config)){
                fileTree = ConfigFile.read(configFile);
            } else {
                fileTree = getDefaultConfig();
            }
        }

        /*
         * service managers
         */
        HashMap<String, String> node = fileTree.get("node");
        if (node == null) {
            throw new IOException("no 'node' in file");
        }
        String value = node.get("servicemanagers");
        if (value == null) {
            throw new IOException("no 'servicemanagers' in node");
        }
        String[] serviceManagerList = value.split(" ");
        for (int i = 0; i < serviceManagerList.length; i++) {
            HashMap<String, String> sm = fileTree.get(serviceManagerList[i]);
            String url = sm.get("url");
            boolean builtIn = Integer.parseInt(sm.get("builtin")) == 1;
            final ServiceManagerInterface serviceManager;
            if (builtIn) {
            	AGServiceManager agServiceManager = new AGServiceManager();
                if (clientBridge!=null){
                	agServiceManager.setClientBridge(clientBridge);
                } else
                	throw new BridgeException("clientBridge not set for builtin Servicemanager");
                serviceManager = agServiceManager;
            } else {
                serviceManager = (ServiceManagerInterface) new ServiceManager(url);
            }
            addServiceManager(serviceManager);
            serviceManager.removeServices();

            // Service List
            String[] services = sm.get("services").split(" +");
            final boolean[] loadDone = new boolean[services.length];
            final Integer loadSync = new Integer(0);
            for (int j = 0; j < services.length; j++) {
                if (services[j].trim().length() == 0) {
                    continue;
                }
                HashMap<String, String> service = fileTree.get(services[j]);
                if (service == null) {
                    throw new IOException("no service: '"+ services[j] + "' in node");
                }
                String packageName = service.get("packageName");
                final AGServicePackageDescription description =
                    allServices.get(packageName);
                if (description != null) {

                    // Read Service Config
                    final Vector<AGParameter> conf = new Vector<AGParameter>();
                    if (service.get("serviceConfig") != null) {
                        String serviceConfig = service.get("serviceConfig");
                        HashMap<String, String> servConf =
                            fileTree.get(serviceConfig);
                        Iterator<String> keys = servConf.keySet().iterator();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            conf.add(new ValueParameter(key,
                                    servConf.get(key)));
                        }
                    }

                    final int serviceNo = j;
                    Thread t = new Thread() {
                        public void run() {
                            try {
                                serviceManager.addService(description,
                                    conf.toArray(new AGParameter[0]), clientProfile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            synchronized (loadSync) {
                                loadDone[serviceNo] = true;
                                loadSync.notifyAll();
                            }
                        }
                    };
                    t.start();
                } else {
                    System.err.println("Warning: Service " + packageName
                            + " not available on this system");
                }
            }


            boolean done = false;
            do {
                synchronized (loadSync) {
                    done = true;
                    for (int j = 0; (j < services.length) && done; j++) {
                        if (!loadDone[j]) {
                            done = false;
                        }
                    }
                    if (!done) {
                        try {
                            loadSync.wait();
                        } catch (InterruptedException e) {
                            // Do Nothing
                        }
                    }
                }
            } while (!done);
        }
    }

    /**
     * Sets the client profile
     * @param cp The client profile to set
     */
    public void setClientProfile(ClientProfile cp) {
        clientProfile = cp;
        Iterator<ServiceManagerInterface> managerIterator =
            serviceManagers.iterator();
        while (managerIterator.hasNext()) {
            try {
                ServiceManagerInterface manager = managerIterator.next();
                manager.setClientProfile(cp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * stores the node configuration in the local file system
     * @param config name of the file where the the description of node should
     *         be stored
     */
    public void storeConfiguration(String config) {
        String configFile = sysNodeConfigDir + config;

        int serviceNo = 0;
        int sconfigNo = 0;

        HashMap<String, HashMap<String, String>> def =
            new HashMap<String, HashMap<String, String>>();

        HashMap<String, String> node =
            new HashMap<String, String>();

        String serviceManagersStr = "";
        for (int i = 0; i < serviceManagers.size(); i++) {
            serviceManagersStr = serviceManagersStr.concat(
                    "servicemanager" + i + " ");
        }
        node.put("servicemanagers", serviceManagersStr);
        def.put("node", node);

        for (int i = 0; i < serviceManagers.size(); i++) {
            HashMap<String, String> serviceManager =
                new HashMap<String, String>();
            ServiceManagerInterface manager = serviceManagers.get(i);
            try {
                System.err.println("Manager class = "
                        + manager.getClass().getCanonicalName());
                if (manager instanceof AGServiceManager) {
                    serviceManager.put("builtin", "1");
                } else {
                    AGServiceManagerDescription smd = manager.getDescription();
                    serviceManager.put("name", smd.getName());
                    serviceManager.put("url", smd.getUri());
                }
                AGServiceDescription[] sd = manager.getServices();
                String servicesStr = "";
                for (int j = 0; j < sd.length; j++, serviceNo++) {
                    servicesStr = servicesStr.concat(
                            "service" + serviceNo + " ");
                }
                serviceManager.put("services", servicesStr);

                for (int j = 0; j < sd.length; j++) {
                    HashMap<String, String> services =
                        new HashMap<String, String>();
                    services.put("packageName", sd[j].getPackageName());

                    AGParameter[] serviceConfig =
                        manager.getServiceConfiguration(sd[j]);
                    if (serviceConfig != null) {
                        services.put("serviceConfig",
                                "serviceConfig" + sconfigNo);
                        HashMap<String, String> sc =
                            new HashMap<String, String>();
                        for (int k = 0; k < serviceConfig.length; k++) {
                            sc.put(serviceConfig[k].getName(),
                                serviceConfig[k].getValue().toString());
                        }
                        def.put("serviceConfig" + sconfigNo, sc);
                        sconfigNo++;
                    }

                    def.put("service" + j, services);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            def.put("servicemanager" + i, serviceManager);
        }

        try {
            System.err.println("configFile " + configFile);
            ConfigFile.store(configFile, def);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns a list of node service configuration files available in the local
     * file system
     * @return list of node service configuration files
     */
     public String[] getConfigurations() {
        Vector<String> configs = new Vector<String>();
        File dir = new File(sysNodeConfigDir);
        String[] files = dir.list();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                try {
                    if (sanityCheckConfig(files[i])){
                        configs.add(files[i]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return configs.toArray(new String[0]);
    }

    /**
     * gets the capabilities of the node service
     * @return vector of capabilies
     * @throws IOException
     * @throws SoapException
     */
     @SoapReturn (
         name = "capabilities"
     )
     public Capability[] getCapabilities()
            throws IOException, SoapException {
        Vector<Capability> capabilities = new Vector<Capability>();
        AGServiceDescription[] services = getServices();
        for (int i = 0; i < services.length; i++) {
            Vector<Capability> cabs = services[i].getCapabilities();
            capabilities.addAll(cabs);
        }
        return capabilities.toArray(new Capability[0]);
    }

    /**
     * sets the uri of the nodeService
     * @param uri uri of nodeService
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * sets streams in node service
     * @param descriptions a vector of stream descriptions
     * @throws IOException
     * @throws SoapException
     */
    public void setStreams(
            @SoapParameter("streamDescriptionList") StreamDescription[] descriptions
            ) throws IOException, SoapException {
        System.out.println("AGNodeService setting streams");
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.setStreams(descriptions);
        }
        streams = new Vector<StreamDescription>();
        for (StreamDescription str:descriptions){
            streams.add(str);
        };
    }

    /**
     * Sets the point of reference for network traffic
     * @param url The url of the point of reference server
     * @throws IOException
     * @throws SoapException
     */
    public void setPointOfReference(String url)
            throws IOException, SoapException {
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.setPointOfReference(url);
        }
        pointOfReference = url;
    }

    /**
     * Sets the encryption
     * @param encryption The encryption
     * @throws IOException
     * @throws SoapException
     */
    public void setEncryption(String encryption)
            throws IOException, SoapException {
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.setEncryption(encryption);
        }
        this.encryption = encryption;
    }

    /**
     * Instructs the service to run automatic bridging
     * @throws IOException
     * @throws SoapException
     */
    public void runAutomaticBridging()
            throws IOException, SoapException {
        Iterator<ServiceManagerInterface> iter =
            serviceManagers.iterator();
        while (iter.hasNext()) {
            ServiceManagerInterface manager = iter.next();
            manager.runAutomaticBridging();
        }
        currentBridge = null;
    }

}

