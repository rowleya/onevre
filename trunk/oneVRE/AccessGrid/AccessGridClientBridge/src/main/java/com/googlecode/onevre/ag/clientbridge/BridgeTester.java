/*
 * Copyright (c) 2008, University of Manchester All rights reserved.
 * See LICENCE in root directory of source code for details of the license.
 */

package com.googlecode.onevre.ag.clientbridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;


import com.googlecode.onevre.ag.agbridge.BridgeClient;
import com.googlecode.onevre.ag.agbridge.BridgeClientCreator;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.network.MulticastNetworkLocation;
import com.googlecode.onevre.ag.types.network.NetworkLocation;
import com.googlecode.onevre.por.ConnectionParameters;
import com.googlecode.onevre.por.PointOfReferenceClient;
import com.googlecode.onevre.por.ReceiveThread;
import com.googlecode.onevre.por.SendThread;
import com.googlecode.onevre.protocols.rtp.RtpCheckProcessor;

import memetic.crypto.RTPCrypt;


/**
 * A class for testing bridges
 *
 * @author Andrew G D Rowley
 * @version 1.0
 */
public class BridgeTester {

    private static final int PACKET_COUNT_BEFORE_MISSING = 100;

    private static final int DELAY_BEFORE_FIRST_CHECK = 5000;

    private static final float INITIAL_ACCEPTABLE_LOSS = 0.1f;

    private static final int FULL_LOSS = 100;

    private static final int DELAY_BETWEEN_CHECKS = 1000;

    private static final int BRIDGES_TO_CHECK = 5;

    private static final int DELAY_BETWEEN_FIND_BRIDGES = 120000;

    private Sender testSender = null;

    private Receiver testReceiver = null;

    private PointOfReferenceClient testReference = null;

    private Vector<NetworkLocation> locations = new Vector<NetworkLocation>();

    private Vector<String> encryptionKeys = new Vector<String>();

    private float acceptableLoss = INITIAL_ACCEPTABLE_LOSS;

    private int bridgeSwitchesDueToLoss = 0;

    private BridgeTestThread currentTester = null;

    private boolean searchStopped = false;

    private BridgeDescription multicast = new BridgeDescription();

    private BridgePingThread bridgePinger = null;

    private BridgeFinder bridgeFinder = null;

    private BridgeDescription[] bridges = null;

    private Integer bridgePingSync = new Integer(0);

    /**
     * Creates a new BridgeTester
     *
     * @throws IOException
     */
    public BridgeTester() throws IOException {
        testSender = new Sender();
        testReceiver = new Receiver();
        testSender.setIgnoreLocalPort(testReceiver.getLocalHost(),
                testReceiver.getLocalPort());
        testSender.pause();
        testReceiver.pause();
        multicast.setName("Multicast");
        multicast.setServerType("multicast");
        multicast.setPingTime(-1);
    }

    /**
     * Sets the encryption to use
     * @param encryption The encryption to use
     */
    public void setEncryption(RTPCrypt encryption) {
        testSender.setEncryption(encryption);
        testReceiver.setDecryption(encryption);
    }

    /**
     * Sets the point of reference
     * @param pointOfReferenceUrl The new point of reference url
     * @throws MalformedURLException
     */
    public void setPointOfReference(String pointOfReferenceUrl)
            throws MalformedURLException {
        if (bridgeFinder != null) {
            bridgeFinder.close();
            bridgeFinder = null;
        }
        try {
			testReference = new PointOfReferenceClient(pointOfReferenceUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
        bridgeFinder = new BridgeFinder();
        bridgeFinder.start();
    }

    /**
     * Adss a location
     * @param newLocation The new location to test
     * @param localAddress The new local address to connect to
     * @param encryption The new encryption
     * @throws IOException
     */
    public void addLocation(NetworkLocation newLocation,
            InetSocketAddress localAddress, RTPCrypt encryption)
            throws IOException {
        testSender.addLocation(newLocation, localAddress, encryption);
        testReceiver.addLocation(newLocation, localAddress, encryption);
        locations.add(newLocation);
        String key = "";
        if (encryption != null) {
            key = encryption.getKey();
        }
        encryptionKeys.add(key);
    }

    /**
     * Changes a location
     * @param oldLocation The old location
     * @param newLocation The new location
     * @param encryption The encryption
     */
    public void changeLocation(NetworkLocation oldLocation,
            NetworkLocation newLocation, RTPCrypt encryption) {
        testSender.changeLocation(oldLocation, newLocation,
                encryption);
        testReceiver.changeLocation(oldLocation, newLocation,
                encryption);
        int index = locations.indexOf(oldLocation);
        if (index != -1) {
            locations.remove(index);
            encryptionKeys.remove(index);
            locations.add(newLocation);
            String key = "";
            if (encryption != null) {
                key = encryption.getKey();
            }
            encryptionKeys.add(key);
        }
    }

    /**
     * Removes a location
     * @param oldLocation The location to remove
     */
    public void removeLocation(NetworkLocation oldLocation) {
        testSender.removeLocation(oldLocation);
        testReceiver.removeLocation(oldLocation);
        int index = locations.indexOf(oldLocation);
        if (index != -1) {
            locations.remove(index);
            encryptionKeys.remove(index);
        }
    }

    /**
     * Finds a bridge that appears to work
     * @return The found bridge or null if there isn't one
     * @throws IOException
     */
    public BridgeDescription findWorkingBridge() throws IOException {
        searchStopped = false;

        // Get the bridges, and ping them
        List<BridgeDescription> currentBridges = null;
        synchronized (bridgePingSync) {
            while (!searchStopped && (bridges == null)) {
                System.err.println("Waiting for bridges to be received");
                try {
                    bridgePingSync.wait(1000);
                } catch (InterruptedException e) {
                    // Does Nothing
                }
            }
            currentBridges = new Vector<BridgeDescription>(
                    Arrays.asList(bridges));
        }
        Iterator<BridgeDescription> iter = currentBridges.iterator();


        // Add the multicast and sort by type and ping time
        currentBridges.add(multicast);
        Collections.sort(currentBridges, new BridgeComparator());

        System.err.println("Testing bridges " + currentBridges);

        // Find the best bridge from the list
        float bestLoss = FULL_LOSS;
        BridgeDescription bestLossBridge = null;
        iter = currentBridges.iterator();
        int bridgesOfSameType = 0;
        String lastType = null;
        while (iter.hasNext() && !searchStopped) {
            BridgeDescription bridge = iter.next();
            if ((lastType == null) || (bridgesOfSameType < BRIDGES_TO_CHECK)
                    || !bridge.getServerType().equals(lastType)) {
                if ((lastType == null)
                        || (!bridge.getServerType().equals(lastType))) {
                    lastType = bridge.getServerType();
                    bridgesOfSameType = 0;
                } else {
                    bridgesOfSameType += 1;
                }
                float loss = testBridge(bridge);
                if (loss < acceptableLoss) {
                    return bridge;
                }
                if (loss < bestLoss) {
                    bestLoss = loss;
                    bestLossBridge = bridge;
                }
            }
        }
        return bestLossBridge;
    }

    /**
     * Stops the current search from continuing
     */
    public void stopCurrentBridgeSearch() {
        searchStopped = true;
    }

    private float testBridge(BridgeDescription bridge) throws IOException {
        System.err.println("Testing Bridge " + bridge);
        ConnectionParameters params =
            testReference.getConnectionParameters();
        try {
            try {
                long ssrc = params.getSsrc();
                BridgeClient client = BridgeClientCreator.create(bridge);
                MulticastNetworkLocation location = params.getLocation();
                client.joinBridge(new NetworkLocation[]{location});
                client.setReceiveTimeout(2000);
                ReceiveThread receiver = new ReceiveThread(client, location);
                SendThread sender = new SendThread(client, location, ssrc);
                sender.start();
                receiver.start();
                Thread.sleep(2000);
                receiver.close();
                sender.close();
                float sendLoss = testReference.getLossFraction(ssrc);
                float receiveLoss = receiver.getLoss(params.getMultissrc());
                client.leaveBridge();
                testReference.releaseSSRC(ssrc);
                float maxLoss = Math.max(sendLoss, receiveLoss);
                System.err.println("Bridge " + bridge + " has loss " + maxLoss + "(send = " + sendLoss + " receive = " + receiveLoss + ")");
                return maxLoss;
            } catch (Exception e) {
                e.printStackTrace();
                testReference.releaseSSRC(params.getSsrc());
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to " + bridge.getName());
        }
        return FULL_LOSS;
    }

    private float checkBridge(Receiver receiver, Sender sender,
            NetworkLocation[] locations,
            PointOfReferenceClient pointOfReference)
            throws IOException {
        boolean changeBridge = false;
        float fractionLoss = 0;
        Vector<Long> remoteSSRCs = pointOfReference.getSSRCs();

        // Check if the local streams being sent are being
        // received remotely
        for (int i = 0; (i < locations.length) && !changeBridge; i++) {
            RtpCheckProcessor processor = (RtpCheckProcessor)
                sender.getProcessor(locations[i]);
            Long[] ssrcs = processor.getSsrcs();
            for (int j = 0; (j < ssrcs.length) && !changeBridge; j++) {
                if (!remoteSSRCs.contains(ssrcs[j])) {
                    if (processor.getReceivedPacketCount(ssrcs[j])
                            > PACKET_COUNT_BEFORE_MISSING) {
                        System.err.println("Source " + ssrcs[j] + " not being received remotely");
                        changeBridge = true;
                        fractionLoss = FULL_LOSS;
                    }
                } else {

                    // Check that the number of packets received
                    // is similar to the number sent
                    float loss =
                        pointOfReference.getMissingFraction(
                                ssrcs[j]);
                    if (loss > acceptableLoss) {
                        System.err.println("Source " + ssrcs[j] + " is too lossy (" + loss + ")");
                        changeBridge = true;
                        fractionLoss = loss;
                        bridgeSwitchesDueToLoss += 1;
                    }
                    remoteSSRCs.remove(ssrcs[j]);
                }
            }
        }

        // Check if the remote SSRCs being received are
        // also received here
        for (int i = 0; (i < locations.length) && !changeBridge; i++) {
            RtpCheckProcessor processor = (RtpCheckProcessor)
                receiver.getProcessor(locations[i]);
            Long[] ssrcs = processor.getSsrcs();
            for (int j = 0; (j < ssrcs.length) && !changeBridge; j++) {

                // Check that the number of packets received
                // is similar to the number sent
                float loss = processor.getMissingPacketFraction(
                        ssrcs[j]);
                float remoteLoss =
                    pointOfReference.getMissingFraction(
                            ssrcs[j]);
                if ((loss - remoteLoss) > acceptableLoss) {
                    System.err.println("Remote Source " + ssrcs[j] + " is too lossy (" + loss + ")");
                    changeBridge = true;
                    fractionLoss = loss - remoteLoss;
                    bridgeSwitchesDueToLoss += 1;
                }
                remoteSSRCs.remove(ssrcs[j]);
            }
        }
        if (!changeBridge && (remoteSSRCs.size() > 0)) {
            if (pointOfReference.getPacketCount(
                    remoteSSRCs.get(0))
                    > PACKET_COUNT_BEFORE_MISSING) {
                System.err.println("Remote source " + remoteSSRCs.get(0) + " is not received locally");
                changeBridge = true;
                fractionLoss = FULL_LOSS;
            }
        }

        if (changeBridge) {
            return fractionLoss;
        }
        return 0;
    }

    /**
     * Starts a bridge tester
     * @param clientBridge The client bridge to notify
     * @param pointOfReference The point of reference client to use
     * @param receiver The reciever to receive with
     * @param sender The sender to send with
     * @return the id that will be used to notify the clientbridge
     */
    public String startBridgeTest(ClientBridge clientBridge,
                PointOfReferenceClient pointOfReference, Receiver receiver,
                Sender sender) {
        if (currentTester != null) {
            currentTester.close();
        }
        currentTester = new BridgeTestThread(clientBridge,
            pointOfReference, receiver, sender);
        currentTester.start();
        return currentTester.getUid();
    }

    /**
     * Stops the current bridge test
     */
    public void stopBridgeTest() {
        if (currentTester != null) {
            currentTester.close();
            currentTester = null;
        }
    }

    private class BridgeTestThread extends Thread {

        private String uid = String.valueOf(
                System.currentTimeMillis()) + (Math.random() * 1000);

        private ClientBridge clientBridge = null;

        private PointOfReferenceClient pointOfReference = null;

        private Receiver receiver = null;

        private Sender sender = null;

        private boolean done = false;

        private BridgeTestThread(ClientBridge clientBridge,
                PointOfReferenceClient pointOfReference, Receiver receiver,
                Sender sender) {
            this.clientBridge = clientBridge;
            this.pointOfReference = pointOfReference;
            this.receiver = receiver;
            this.sender = sender;
        }

        private void close() {
            done = true;
        }

        private String getUid() {
            return uid;
        }

        public void run() {
            float loss = -1;
            try {
                Thread.sleep(DELAY_BEFORE_FIRST_CHECK);
            } catch (InterruptedException e1) {
                // Do Nothing
            }
            boolean change = false;
            System.err.println("Starting bridge checking");
            while (!done && !change) {
                try {
                    float fractionLoss = checkBridge(receiver, sender,
                        locations.toArray(new NetworkLocation[0]),
                        pointOfReference);
                    if ((fractionLoss > 0) && !done) {
                        change = true;
                        loss = fractionLoss;
                    }
                    try {
                        Thread.sleep(DELAY_BETWEEN_CHECKS);
                    } catch (InterruptedException e) {
                        // Does Nothing
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    done = true;
                    JOptionPane.showMessageDialog(null,
                            "Error testing bridge: " + e.getMessage() + ".\n"
                            + "Bridge testing will be disabled!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            if (!done && change) {
                clientBridge.notifyBridgeLoss(uid, loss);
            }
        }
    }

    /**
     * Gets the multicast bridge description
     * @return The multicast bridge description
     */
    public BridgeDescription getMulticastBridge() {
        return multicast;
    }

    private class BridgeComparator implements Comparator<BridgeDescription> {

        private BridgeDescription.BridgePingComparator comparator =
            new BridgeDescription.BridgePingComparator();

        /**
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(BridgeDescription b1, BridgeDescription b2) {
            boolean created1 = false;
            boolean created2 = false;
            BridgeClient c1 = null;
            BridgeClient c2 = null;
            try {
                c1 = BridgeClientCreator.create(b1);
                created1 = true;
            } catch (Exception e) {
                // Do Nothing
            }
            try {
                c2 = BridgeClientCreator.create(b2);
                created2 = true;
            } catch (Exception e) {
                // Do Nothing
            }
            if (created1 && created2) {
                if (c1.getOrder() == c2.getOrder()) {
                    return comparator.compare(b1, b2);
                }
                return c1.getOrder() - c2.getOrder();
            }
            if (created1 && !created2) {
                return -1;
            } else if (created2 && !created1) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private class BridgeFinder extends Thread {

        private boolean finished = false;

        public void run() {
            while (!finished) {
                synchronized (bridgePingSync) {
                    if (bridgePinger != null) {
                        bridgePinger.close();
                        bridgePinger = null;
                    }
                    try {
                        System.err.println("Getting OK Bridges");
                        bridges = testReference.getOKBridges();
                        System.err.println("Got bridges = " + Arrays.toString(bridges));
                        bridgePinger = new BridgePingThread(bridges,
                                DELAY_BETWEEN_CHECKS);
                        bridgePinger.start();
                        bridgePingSync.notifyAll();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Error getting bridges");
                    }
                    try {
                        bridgePingSync.wait(DELAY_BETWEEN_FIND_BRIDGES);
                    } catch (InterruptedException e) {
                        // Does Nothing
                    }
                }
            }
        }

        private void close() {
            finished = true;
            synchronized (bridgePingSync) {
                bridgePingSync.notifyAll();
            }
        }
    }

    /**
     * Test Method to run standalone
     * @param args commona line arguments - not required
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BridgeTester tester = new BridgeTester();
        tester.setPointOfReference("http://rosie.rcs.manchester.ac.uk:8080/por");
        Thread.sleep(2000);
        BridgeDescription bridge = tester.findWorkingBridge();
        System.err.println("Best bridge = " + bridge);
        System.exit(0);
    }
}
