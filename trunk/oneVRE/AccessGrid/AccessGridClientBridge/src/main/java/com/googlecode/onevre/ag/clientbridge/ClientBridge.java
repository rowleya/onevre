/*
 * @(#)ClientBridge.java
 * Created: 29 Aug 2007
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

package com.googlecode.onevre.ag.clientbridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Vector;

import javax.swing.JOptionPane;


import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.agbridge.BridgeClientCreator;
import com.googlecode.onevre.ag.exceptions.BridgeException;
import com.googlecode.onevre.ag.interfaces.BridgeInterface;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.por.PointOfReferenceClient;
import com.googlecode.onevre.utils.Utils;

import memetic.crypto.AESCrypt;
import memetic.crypto.DESCrypt;
import memetic.crypto.RTPCrypt;


/**
 * A local bridge
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class ClientBridge implements BridgeInterface {

    protected static final short RTP_VERSION = 2;

    private Sender sender = null;

    private Receiver receiver = null;

    private BridgeClient bridgeClient = null;

    private BridgeDescription bridgeDescription = null;

    private PointOfReferenceClient pointOfReference = null;

    private boolean done = false;

    private boolean forcedBridge = false;

    private Vector<StreamDescription> currentStreams =
        new Vector<StreamDescription>();

    private NetworkLocation[] locations = new NetworkLocation[0];

    private String[] encryptionKeys = new String[0];

    private boolean joinCompleted = false;

    private Integer joinSync = new Integer(0);

    private BridgeTester tester = new BridgeTester();

    private String testId = null;

    private String searchId = null;

    private boolean paused = false;

    private boolean encrypted = false;

    /**
     * Creates a new ClientBridge
     *
     * @throws IOException
     */
    public ClientBridge() throws IOException {
        sender = new Sender();
        receiver = new Receiver();
        sender.setIgnoreLocalPort(receiver.getLocalHost(),
                receiver.getLocalPort());
    }

    /**
     * Joins a bridge
     * @param bridgeDescription The bridge to join
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public void joinBridge(BridgeDescription bridgeDescription)
            throws BridgeException {
        try {
            joinBridge(bridgeDescription, true);
        } catch (Exception e) {
            runAutomaticBridging();
            throw new BridgeException(e);
        }
    }

    /**
     * Runs automatic bridge detection
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     *
     */
    public void runAutomaticBridging() throws BridgeException {
        try {
			joinBridge(tester.getMulticastBridge(), false);
		} catch (Exception e) {
			throw new BridgeException(e);
		}
    }

    private void joinBridge(BridgeDescription bridgeDescription,
            boolean force) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException {
        if (bridgeDescription == null) {
            bridgeDescription = tester.getMulticastBridge();
        }
        this.forcedBridge = force;
        System.err.println("Switching to bridge " + bridgeDescription + (force? " (forced)": " (auto)"));
        if (bridgeDescription.equals(this.bridgeDescription)) {
            return;
        }
        pause();
        this.bridgeDescription = bridgeDescription;
        final BridgeClient newBridgeClient = BridgeClientCreator.create(
                    bridgeDescription);
        synchronized (joinSync) {
            joinCompleted = false;
        }
        Thread t = new Thread() {
            public void run() {
                synchronized (joinSync) {
                    try {
                        newBridgeClient.joinBridge(locations);
                        joinCompleted = true;
                        joinSync.notifyAll();
                    } catch (IOException e) {
                        e.printStackTrace();
                        joinSync.notifyAll();
                    }
                }
            }
        };
        t.start();
        synchronized (joinSync) {
            try {
                joinSync.wait(2000);
            } catch (InterruptedException e) {
                // Do Nothing
            }
        }

        if (joinCompleted) {
            sender.setBridge(newBridgeClient);
            receiver.setBridge(newBridgeClient);

            if (bridgeClient != null) {
                bridgeClient.leaveBridge();
            }
            bridgeClient = newBridgeClient;
            String warning = bridgeClient.getWarning();
            if (warning != null) {
                JOptionPane.showMessageDialog(null, warning, "Bridge Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            throw new IOException("Could not join bridge");
        }
        resume();
    }

    /**
     * Stops the bridge
     */
    public void stop() {
        done = true;
        sender.close();
        receiver.close();
        tester.stopBridgeTest();
        try {
            if (bridgeClient != null) {
                bridgeClient.leaveBridge();
            }
        } catch (IOException e) {
            // Does Nothing
        }
    }

    /**
     * Returns the current bridge being used
     * @return The description of the current bridge
     */
    public BridgeDescription getBridge() {
        return bridgeDescription;
    }

    /**
     * Sets the encryption to use
     * @param encryption The encryption to use
     */
    public void setEncryption(String encryption) {
        encrypted = getEncryption(encryption) != null;
        if (encrypted) {
            tester.stopBridgeTest();
        }
        sender.setEncryption(getEncryption(encryption));
        receiver.setDecryption(getEncryption(encryption));
        tester.setEncryption(getEncryption(encryption));
    }

    /**
     * Sets the point of reference URL
     * @param pointOfReferenceUrl The new URL to set
     * @return true if the url was set, false otherwise
     * @throws IOException
     */
    public String setPointOfReferenceUrl(String pointOfReferenceUrl) throws BridgeException  {
    	String url = null;
    	try {
	    	PointOfReferenceClient newClient = new PointOfReferenceClient(pointOfReferenceUrl);
	        if (!done) {
	            pause();
	        }
	        pointOfReference = newClient;
	        url = newClient.getUrl();
	        System.err.println("ClientBridge - Setting point of reference: " + url);
	        tester.setPointOfReference(url);
	        if (!done) {
	            resume();
	        }
    	} catch (Exception e) {
			throw new BridgeException(e);
		}
        return url;
    }


    /**
     * Pauses the sender
     */
    public synchronized void pause() {
        if (!paused) {
            paused = true;
            testId = null;
            searchId = null;
            sender.pause();
            receiver.pause();
            tester.stopCurrentBridgeSearch();
            tester.stopBridgeTest();
            try {
                pointOfReference.stopMonitor();
            } catch (Exception e) {
                // Do Nothing
            }
        }
    }

    /**
     * Resumes the sender
     */
    public synchronized void resume() {
        if (paused) {
            if (locations.length > 0) {
                try {
                    pointOfReference.startMonitor(locations, encryptionKeys);
                } catch (IOException e) {
                    e.printStackTrace();
                }
             }
            sender.resume();
            receiver.resume();
            if (!forcedBridge && (locations.length > 0)) {
                if (!encrypted) {
                    testId = tester.startBridgeTest(this, pointOfReference,
                            receiver, sender);
                } else {
                    System.err.println(
                            "Not starting bridge test due to encryption");
                }
            }
            paused = false;
        }
    }

    /**
     * Notifies the client that there is loss in the bridge
     * @param testId The id of the test
     * @param loss The loss to report
     */
    public synchronized void notifyBridgeLoss(String testId, final float loss) {
        System.err.println("Bridge has detected loss or missing stream");
        if ((this.testId != null) && testId.equals(this.testId)) {
            try {
                pointOfReference.stopMonitor();
            } catch (IOException e1) {
                // Do Nothing
            }
            testId = null;
            searchId = String.valueOf(System.currentTimeMillis())
                + (Math.random() * 1000);
            Thread searchThread = new Thread() {
                public void run() {
                    try {
                        notifyNewBridgeFound(searchId,
                            tester.findWorkingBridge());
                    } catch (IOException e) {
                        notifyNewBridgeFound(searchId, null);
                    }
                }
            };
            searchThread.start();
        }
    }

    private synchronized void notifyNewBridgeFound(String id,
            BridgeDescription bridge) {
        if ((searchId != null) && id.equals(searchId)) {
            searchId = null;
            if (bridge != null) {
                boolean bridgeOK = false;
                while (!bridgeOK) {
                    try {
                        joinBridge(bridge, false);
                        bridgeOK = true;
                    } catch (Exception e) {

                        // Should never happen
                        e.printStackTrace();
                        bridgeOK = false;
                        try {
                            bridge = tester.findWorkingBridge();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Error finding bridge to connect to.\n"
                        + "This could suggest that your firewall rules are too"
                        + " restrictive.\n"
                        + "Please manually select a bridge by clicking on the "
                        + "Mutlicast / Unicast configuration icon.",
                        "Bridge Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private RTPCrypt getEncryption(String enc) {
        if ((enc != null) && !enc.equals("")) {
            String encType = DESCrypt.TYPE;
            int slash = enc.indexOf("/");
            if (slash != -1) {
                encType = enc.substring(0, slash);
                enc = enc.substring(slash + 1);
            }
            if (encType.equals(DESCrypt.TYPE)) {
                return new RTPCrypt(new DESCrypt(enc));
            } else if (encType.equals(AESCrypt.TYPE)) {
                return new RTPCrypt(new AESCrypt(enc));
            }
        }
        return null;
    }

    private RTPCrypt getEncryption(StreamDescription stream, RTPCrypt current) {
        if (stream.getEncryptionFlag() == 1) {
            String enc = stream.getEncryptionKey();
            RTPCrypt crypt = getEncryption(enc);
            if (crypt != null) {
                return crypt;
            }
        }
        return current;
    }

    /**
     * Sets the streams used in the bridge
     * @param streams The new streams
     * @throws IOException
     */
    public void setStreams(Vector<StreamDescription> streams)
            throws BridgeException {
    	try {

	    	pause();

	        Vector<StreamDescription> oldStreams =
	            new Vector<StreamDescription>(currentStreams);
	        Vector<NetworkLocation> locs = new Vector<NetworkLocation>();
	        Vector<RTPCrypt> crypts = new Vector<RTPCrypt>();
	        currentStreams.clear();
	        for (int i = 0; i < streams.size(); i++) {
	            StreamDescription stream = streams.get(i);

	            System.err.println("Searching for stream with capability " + stream.getCapability());
	            StreamDescription matching = null;
	            currentStreams.add(stream);
	            for (int j = 0; j < (oldStreams.size())
	                    && (matching == null); j++) {
	                if (stream.hasSameCapabilitiesAs(oldStreams.get(j))) {
	                    matching = oldStreams.get(j);
	                    oldStreams.remove(j);
	                }
	            }
	            for (int j = 0; j < (oldStreams.size())
	                    && (matching == null); j++) {
	                if (stream.isCapabilitySubsetOf(oldStreams.get(j))) {
	                    matching = oldStreams.get(j);
	                    oldStreams.remove(j);
	                }
	            }

	            if (matching != null) {
	                NetworkLocation newLocation = stream.getLocation();
	                NetworkLocation oldLocation = matching.getLocation();
	                if (!newLocation.equals(oldLocation)) {
	                    RTPCrypt encryption = getEncryption(stream,
	                            sender.getEncryption(oldLocation));
	                    locs.add(newLocation);
	                    crypts.add(encryption);

	                    System.err.println("Changing stream " + oldLocation + " to " + newLocation);

	                    // Change forwarding from old address to new address
	                    sender.changeLocation(oldLocation, newLocation, encryption);
	                    receiver.changeLocation(oldLocation, newLocation,
	                            encryption);
	                    tester.changeLocation(oldLocation, newLocation,
	                            encryption);
	                } else {
	                    System.err.println("Not changing from " + oldLocation + " to " + newLocation);
	                }
	            } else {
	                RTPCrypt encryption = getEncryption(stream, null);
	                InetSocketAddress localAddress = new InetSocketAddress(
	                        "224.0.24.124",
	                        Utils.searchPort(2, 2, false));
	                InetSocketAddress testLocalAddress = new InetSocketAddress(
	                        "224.0.24.125",
	                        Utils.searchPort(2, 2, false));
	                locs.add(stream.getLocation());
	                crypts.add(encryption);
	                System.err.println("Creating new location for " + stream.getLocation());
	                sender.addLocation(stream.getLocation(), localAddress,
	                        encryption);
	                receiver.addLocation(stream.getLocation(), localAddress,
	                        encryption);
	                tester.addLocation(stream.getLocation(), testLocalAddress,
	                        encryption);
	            }
	        }

	        for (int i = 0; i < oldStreams.size(); i++) {
	            NetworkLocation oldLocation = oldStreams.get(i).getLocation();
	            System.err.println("Removing old location " + oldLocation);
	            sender.removeLocation(oldLocation);
	            receiver.removeLocation(oldLocation);
	            tester.removeLocation(oldLocation);
	        }

	        locations = locs.toArray(new NetworkLocation[0]);
	        for (int i = 0; i < locations.length; i++) {
	        encryptionKeys = new String[locations.length];
	            RTPCrypt crypt = crypts.get(i);
	            String key = "";
	            if (crypt != null) {
	                key = crypt.getKey();
	                encrypted = true;
	            }
	            encryptionKeys[i] = key;
	        }

	        if (bridgeClient != null) {
	            bridgeClient.leaveBridge();
	        }
	        if (bridgeDescription == null) {
	            bridgeDescription = tester.getMulticastBridge();
	        }

	        boolean ok = false;
	        while (!ok) {
	            try {
	                bridgeClient = BridgeClientCreator.create(
	                        bridgeDescription);
	                if (locations.length > 0) {
	                    bridgeClient.joinBridge(locations);
	                }
	                sender.setBridge(bridgeClient);
	                receiver.setBridge(bridgeClient);
	                ok = true;
	            } catch (Exception e) {
	                e.printStackTrace();
	                ok = false;
	                bridgeDescription = tester.findWorkingBridge();
	            }
	        }

	        if (locations.length == 0) {
	            done = true;
	        }
	        resume();
		} catch (Exception e) {
			throw new BridgeException(e);
		}
    }

    /**
     * Gets a local InetSocketAddress for a stream
     * @param stream The stream to get the address for
     * @return The InetSocketAddresss
     */
    public InetSocketAddress getLocalSocketAddress(StreamDescription stream) {
        return receiver.getLocalAddress(stream.getLocation());
    }

    /**
     * Gets the local location for a stream
     * @param stream The stream to get the location for
     * @return The local multicast location
     */
    public NetworkLocation getLocalLocation(StreamDescription stream) {
        InetSocketAddress address = getLocalSocketAddress(stream);
        if (address != null) {
            MulticastNetworkLocation location = new MulticastNetworkLocation();
            location.setHost(address.getAddress().getHostAddress());
            location.setPort(address.getPort());
            location.setTtl(0);
            return location;
        }
        return null;
    }
}
