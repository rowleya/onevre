package com.googlecode.onevre.protocols.rtp;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import javax.media.Format;

public interface RtpWriter {

	void addPacket(DatagramPacket packet, InetSocketAddress address);

	boolean checkFormatSupport(Format outputFormat);

}
