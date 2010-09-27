/*
 * @(#)InSorsService.java
 * Created: 11-Oct-2007
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

package com.googlecode.onevre.web.IoComService;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;


import com.googlecode.onevre.ag.types.Capability;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.ag.types.server.AGService;
import com.googlecode.onevre.ag.types.service.parameter.TextParameter;
import com.googlecode.onevre.platform.SystemConfig;

/**
 * An AG3 Service for using IoCom software
 *
 * @author Anja Le Blanc
 * @version 1.0
 *
 */
public class IoComService extends AGService {

    private static final int BUFFER_SIZE = 8096;

    private static final String H261 = "H261";

    private static final String L16 = "L16";

    private static final String L8 = "L8";

    private static final String PCMU = "PCMU";

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
            new Capability(Capability.PRODUCER, Capability.AUDIO, PCMU,
                    16000, 1),
            new Capability(Capability.CONSUMER, Capability.VIDEO, H261,
                    90000, 1),
            new Capability(Capability.PRODUCER, Capability.VIDEO, H261,
                    90000, 1)};

    private TextParameter igClient = new TextParameter("IG Client",
            "C:\\Program Files\\inSORS\\IGClient.exe");

    private TextParameter iocomServer = new TextParameter("IoCom Server", "");

    private TextParameter username = new TextParameter("Username", "");

    private TextParameter password = new TextParameter("Password", "");

    private boolean started = false;

    private boolean inSorsLaunched = false;

    private StreamDescription audioDescription = null;

    private StreamDescription videoDescription = null;

    private Process process = null;

    /**
     * Creates a new AudioService
     *
     */
    public IoComService() {
        System.err.println("InSorsService  construced");
        for (int i = 0; i < CAPABILITY.length; i++) {
            addCapability(CAPABILITY[i]);
        }
        setIdentity(null);
        addConfigurationOption(iocomServer);
        addConfigurationOption(username);
        addConfigurationOption(password);
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
     * start rat
     *
     * @param description
     *            description of the stream (StreamDescription)
     */
    public void setStream(StreamDescription description) {

        Capability cap = this.configureStream(description);
        if (cap != null) {
            if (cap.getType().equals(Capability.AUDIO)) {
                audioDescription = description;
            } else if (cap.getType().equals(Capability.VIDEO)) {
                videoDescription = description;
            }
        }

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
        if (!inSorsLaunched) {
            if ((audioDescription != null) && (videoDescription != null)) {
                if (iocomServer.getValue().equals("")
                        || username.getValue().equals("")
                        || password.getValue().equals("")) {
                    System.err.println("Missing configuration for service");
                } else {
                    try {
                        Authenticator.setDefault(new Authenticator() {
                           protected PasswordAuthentication
                                   getPasswordAuthentication() {
                               return new PasswordAuthentication(
                                  (String) username.getValue(),
                                  ((String) password.getValue()).toCharArray());
                           }
                        });
                        NetworkLocation videoLocation =
                            videoDescription.getLocation();
                        NetworkLocation audioLocation =
                            audioDescription.getLocation();
                        String startupfile =
                            SystemConfig.getInstance().getTempDir() + "/"
                            + "IoCom_" + System.currentTimeMillis() + ".igv";
                        String server = (String) iocomServer.getValue();
                        server += "?MMDS=\"Multicast%20Connect\""
                            + "&MBVIP=" + videoLocation.getHost()
                            + "&MBVP=" + videoLocation.getPort()
                            + "&MBAIP=" + audioLocation.getHost()
                            + "&MBAP=" + audioLocation.getPort();
                        URL url = new URL(server);
                        HttpURLConnection connection = (HttpURLConnection)
                            url.openConnection();
                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream outputStream =
                            new FileOutputStream(startupfile);
                        byte[] data = new byte[BUFFER_SIZE];
                        int bytesRead = inputStream.read(data);
                        while (bytesRead != -1) {
                            outputStream.write(data, 0, bytesRead);
                            bytesRead = inputStream.read(data);
                        }
                        outputStream.close();
                        inputStream.close();
                        process = Runtime.getRuntime().exec(new String[]{
                                "\"" + igClient.getValue() + "\"",
                                "\"" + startupfile + "\""});
                        inSorsLaunched = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     *
     * @see com.googlecode.onevre.ag.types.server.AGService#stop()
     */
    public void stop() {
        if (inSorsLaunched) {
            process.destroy();
            inSorsLaunched = false;
        }
    }
}
