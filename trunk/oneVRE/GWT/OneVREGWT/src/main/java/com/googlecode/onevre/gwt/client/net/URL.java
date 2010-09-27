package com.googlecode.onevre.gwt.client.net;

public class URL {

	String protocol = "https";
	String host = null;
	int port = 80;
	String file= "";

	public URL(String url) {
		String urlsplits[] = url.split("://");
		if (urlsplits.length>1) {
			protocol = urlsplits[0];
			host = urlsplits[1];
		} else {
			host = urlsplits[0];
		}
		int slashidx = host.indexOf('/');
		file = host.substring(slashidx);
		host = host.substring(0,slashidx);
		urlsplits = host.split(":");
		if (urlsplits.length>1) {
			host = urlsplits[0];
			port = Integer.valueOf(urlsplits[1]);
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getProtocol() {
		return protocol;
	}

}
