package com.googlecode.onevre.ag.interfaces;

import java.util.Vector;


import com.googlecode.onevre.ag.exceptions.BridgeException;
import com.googlecode.onevre.ag.types.BridgeDescription;
import com.googlecode.onevre.ag.types.StreamDescription;
import com.googlecode.onevre.ag.types.network.NetworkLocation;

public interface BridgeInterface {

    String setPointOfReferenceUrl(String url) throws BridgeException;

    void joinBridge(BridgeDescription bridgeDescription) throws BridgeException;

    void setStreams(Vector<StreamDescription> streamDescriptions) throws BridgeException;

    void stop();

    NetworkLocation getLocalLocation(StreamDescription stream);

    void setEncryption(String encryption);

    void runAutomaticBridging() throws BridgeException;

}
