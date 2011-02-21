package com.googlecode.onevre.ag.types.server;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.ag.types.StreamDescription;


/**
 *  The AGStreamDescriptionList is actually a hashmap of streamproducers
 *  mapped against the streamDescriptions
 *
 *  @author Tobias M Schiebeck
 *
 *  AGTk documentation:
 *
 *  Class to represent stream descriptions in a venue.  Entries in the
 *  list are a tuple of (stream description, producing users).  A stream
 *  is added to the list with a producer.  Producing users can be added
 *  to and removed from an existing stream.  When the number of users
 *  producing a stream becomes zero, the stream is removed from the list.
 *
 *  @author Tobias M Schiebeck
 *
 */
public class AGStreamDescriptionList {

    //

    private HashMap<StreamDescription, Vector<String>> streams = new HashMap<StreamDescription, Vector<String>>();

    /**
     * Add a stream to the list, only if it doesn't already exist
     * @param stream A stream description
     */
    public void addStream(StreamDescription stream) {
        if (streams.containsKey(stream)) {
            throw new RuntimeException("AddStream: Stream already present.");
        }
        streams.put(stream, new Vector<String>());
    }

    /**
     * Remove a stream from the list
     *
     * @param stream A stream description to be removed from the venue.
     *
     */

    public void removeStream(StreamDescription stream) {
        if (streams.remove(stream) == null) {
            throw new RuntimeException("RemoveStream: Stream not found.");
        }
    }

    /**
     * Add a stream to the list, with the given producer
     *
     * @param producingUser  A user who is producing media for this stream description.
     * @param stream  The stream description of the media the user is producing.
     *
     */

    public void addStreamProducer(String producingUser, StreamDescription stream) {
        Vector<String> producers = streams.get(stream);
        if (producers == null) {
            producers = new Vector<String>();
            streams.put(stream, producers);
        }
        producers.add(producingUser);
    }

    /**
     * Remove a stream producer from the given stream.  If the last
     * producer is removed, the stream will be removed from the list
     * if it is non-static.
     *
     * @param producingUser The user producing the stream.
     * @param stream the Stream descriptino the producing user is producing.
     * @return the stream description if the stream ins removed otherwise null
     *
     */

    public StreamDescription removeStreamProducer(String producingUser, StreamDescription stream) {
        Vector<String> producers = streams.get(stream);
        if (producers != null) {
            producers.remove(producingUser);
            if ((!stream.getStatic()) && (producers.size() == 0)) {
                streams.remove(stream);
                return stream;
            }
        }
        return null;
    }

    /**
     * Remove producer from all streams. If the last
     * producer is removed, the stream will be removed from the list
     * if it is non-static.
     *
     * @param producingUser The user to be removed from all existing streams.
     * @return The list of the streams which have been removed from the Venue
     */

    public Vector<StreamDescription> removeProducer(String producingUser) {
        Vector<StreamDescription> removedStreams = new Vector<StreamDescription>();
        for (StreamDescription stream : streams.keySet()) {
            StreamDescription removedStream = removeStreamProducer(producingUser, stream);
            if (removedStream != null) {
                removedStreams.add(removedStream);
            }
        }
        return removedStreams;
    }

    /**
     * Get the list of streams, without producing user info
     * @return The list of stream descriptions.
     */
    public Vector<StreamDescription> getStreams() {
        Vector<StreamDescription> streamDescriptions = new Vector<StreamDescription>();
        for (StreamDescription stream : streams.keySet()) {
            streamDescriptions.add(stream);
        }
        return streamDescriptions;
    }


    /**
     * GetStaticStreams returns a list of static stream descriptions to the caller.
     *
     * @return The list of stream descriptions for the static streams in this Venue.
     */
    public Vector<StreamDescription> getStaticStreams() {
        Vector<StreamDescription> staticStreams = new Vector<StreamDescription>();
        for (StreamDescription stream : streams.keySet()) {
            if (stream.getStatic()) {
                staticStreams.add(stream);
            }
        }
        return staticStreams;
    }

    public String toString() {
        return streams.toString();
    }

}
