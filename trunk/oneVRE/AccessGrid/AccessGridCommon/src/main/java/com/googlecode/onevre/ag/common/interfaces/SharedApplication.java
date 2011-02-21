/*
 * @(#)SharedApplication.java
 * Created: 25-Mar-2008
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

package com.googlecode.onevre.ag.common.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;


import com.googlecode.onevre.ag.types.ClientProfile;
import com.googlecode.onevre.ag.types.application.AGSharedApplicationDescription;
import com.googlecode.onevre.ag.types.application.AppDataDescription;
import com.googlecode.onevre.ag.types.application.AppParticipantDescription;
import com.googlecode.onevre.ag.types.application.SharedAppState;
import com.googlecode.onevre.protocols.soap.common.SoapDeserializer;
import com.googlecode.onevre.protocols.soap.common.SoapResponseHash;
import com.googlecode.onevre.protocols.soap.soapclient.SoapRequest;
import com.googlecode.onevre.types.soap.exceptions.SoapException;
import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

/**
 * A proxy for a SharedApplication
 * @author Tobias M Schiebeck
 * @version 1.0
 */
public class SharedApplication {

    // The string that is the connection id parameter
//    private static final String CONNECTION_ID = "connectionId";

    private static final String APPLICATION_NS =
        "http://www.accessgrid.org/v3.0/sharedapplication";

    // The soap request
    private SoapRequest soapRequest = null;

    static {
        SoapDeserializer.mapType(SharedAppState.class);
        SoapDeserializer.mapType(AppParticipantDescription.class);
        SoapDeserializer.mapType(AppDataDescription.class);
/*       SoapDeserializer.mapType(EventDescription.class);
        SoapDeserializer.mapType(ProviderProfile.class);
        SoapDeserializer.mapType(ApplicationDescription.class);
        SoapDeserializer.mapType(ApplicationCmdDescription.class);
        SoapDeserializer.mapType(ConnectionDescription.class);
        SoapDeserializer.mapType(DataDescription.class);
        SoapDeserializer.mapType(ServiceDescription.class);
        SoapDeserializer.mapType(StreamDescription.class);
        SoapDeserializer.mapType(MulticastNetworkLocation.class);
        SoapDeserializer.mapType(UnicastNetworkLocation.class);
        SoapDeserializer.mapType(NetworkLocation.class);
        SoapDeserializer.mapType(EventDescription.class);
*/
    }

    /**
     * Creates a new Venue
     * @param serverUrl The url of the venue
     * @throws MalformedURLException
     */
    public SharedApplication(String serverUrl) throws MalformedURLException {
        this.soapRequest = new SoapRequest(serverUrl);
    }

    /**
     * Gets the state of the shared application
     * @param privateToken token of the application
     * @return The response
     * @throws IOException
     * @throws SoapException
     */
    public SharedAppState getState(String privateToken)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(APPLICATION_NS, "GetState",
                "GetStateRequest", new String[]{"privateToken"}, new Object[]{privateToken},
                new Object[]{SoapSerializable.STRING_TYPE},
                new SoapResponseHash(
                        new String[]{APPLICATION_NS + "/name"},
                         new Class[]{SharedAppState.class}));
        Object state = result.get("name");
        if ((state != null) && (state instanceof SharedAppState)) {
            return (SharedAppState) state;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * not implemented yet
     * @param name
     */
    public void setName(String name) {
        // not implemented yet

    }

    /**
     * not implemented yet
     * @param Identity
      */
   public void setIdentity(ClientProfile identity) {
        // not implemented yet

    }

    /**
     * not implemented yet
     * @param packageName
     */
    public void setPackageName(String packageName) {
        // not implemented yet
    }


    /**
     * not implemented yet
     * @return null
     */
    public AGSharedApplicationDescription getDescription() {
            return null;
    };


    /**
     * Gets the participants of an application
     * @param privateToken token of the shared application
     * @return list of application participant descriptions
     * @throws IOException
     * @throws SoapException
     */
    public AppParticipantDescription[] getParticipants(String privateToken)
        throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(APPLICATION_NS,
                "GetParticipants", "GetParticipantsRequest",
                new String[]{"privateToken"},  new Object[]{privateToken},
                new Object[]{SoapSerializable.STRING_TYPE},
                new SoapResponseHash(
                        new String[]{APPLICATION_NS + "/participants"},
                         new Class[]{AppParticipantDescription.class}));
        Object participants = result.get("participants");
        if ((participants != null)
                && AppParticipantDescription.class.equals(participants.getClass().getComponentType())) {
            return (AppParticipantDescription[]) participants;
        }
        throw new SoapException("Return type not correct");
    }

    /**
     * get the data channel a shared application uses
     * @param privateToken token to access an individual application
     * @return the uri of the Application data channel
     * @throws IOException
     * @throws SoapException
     */
    public HashMap<String, Object> getDataChannel(String privateToken)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(APPLICATION_NS, "GetDataChannel",
                "GetDataChannelRequest", new String[] {"privateToken" },
                new Object[] {privateToken }, new Object[] {SoapSerializable.STRING_TYPE},
                new SoapResponseHash(new String[] {
                        APPLICATION_NS + "/channelId",
                        APPLICATION_NS + "/address" ,
                        APPLICATION_NS + "/port" ,
                        }, new Class[] {
                        String.class, String.class, Integer.class}));
        Object o = result.get("channelId");
        Object o1 = result.get("address");
        Object o2 = result.get("port");
        if ((o == null) || !(o instanceof String)) {
            throw new SoapException("Return type not correct -- channelId");
        }
        if ((o1 == null) || !(o1 instanceof String)) {
            throw new SoapException("Return type not correct -- address");
        }
        if ((o2 == null) || !(o2 instanceof Integer)) {
            throw new SoapException("Return type not correct -- port");
        }
        return result;
    }



    /**
     * joins a shared application
     * @param clientProfile The client profile of the participant joining the application
     * @return the token to be used
     * @throws IOException
     * @throws SoapException
     */
    public HashMap<String, String> join(ClientProfile clientProfile)
            throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(APPLICATION_NS, "Join",
                "JoinRequest", new String[]{"clientProfile"},
                new Object[]{clientProfile}, new Object[]{null},
                new SoapResponseHash(
                        new String[]{APPLICATION_NS + "/publicId", APPLICATION_NS + "/privateId"},
                         new Class[]{String.class, String.class}));
        HashMap<String, String> retval = new HashMap<String, String>();
        Object publicId = result.get("publicId");
        Object privateId = result.get("privateId");
        if ((publicId != null) && (publicId instanceof String)) {
            retval.put("publicId", (String) publicId);
        } else  {
            throw new SoapException("Return type not correct -- publicId");
        }
        if ((privateId != null) && (privateId instanceof String)) {
            retval.put("privateId", (String) privateId);
        } else  {
            throw new SoapException("Return type not correct -- privateId");
        }
        return retval;
    }


    /**
     * a client leaves the shared application
     * @param privateToken the token to access the application
     * @return flag to indicate the success of the request
     * @throws IOException
     * @throws SoapException
     */
    public int leave(String privateToken) throws IOException, SoapException {
        HashMap<String, Object> result = soapRequest.call(APPLICATION_NS, "Leave",
                "LeaveRequest", new String[] {"privateToken" },
                new Object[] {privateToken }, new Object[] {SoapSerializable.STRING_TYPE},
                new SoapResponseHash(new String[] {
                        APPLICATION_NS + "/retval"}, new Class[] {
                        Integer.class}));
        Object retval = result.get("retval");
        if ((retval != null) && (retval instanceof Integer)) {
                return ((Integer) retval).intValue();
        }
        throw new SoapException("Return type not correct");
    }


    /**
     * remove a data item from the applications key-value store
     * @param privateToken token of the shared application
     * @param key key of the dataitem to remove
     * @throws IOException
     * @throws SoapException
     */
    public void removeData(String privateToken, String key)
            throws IOException, SoapException {
            soapRequest.call(APPLICATION_NS, "RemoveData", "RemoveDataRequest",
                new String[]{"privateToken", "key"},
                new Object[]{privateToken, key},
                new Object[]{SoapSerializable.STRING_TYPE, SoapSerializable.STRING_TYPE},
                null);
    }

    /**
     * Shuts down the application
     * @throws IOException
     * @throws SoapException
     */
     public void shutdown() throws IOException, SoapException {
        soapRequest.call(APPLICATION_NS, "Shutdown",
                "ShutdownRequest", new String[]{},
                new Object[]{}, new Object[]{},
                null);
    }

    /**
     * Cancels the most recent request
     *
     */
    public void cancel() {
        soapRequest.cancel();
    }
}
