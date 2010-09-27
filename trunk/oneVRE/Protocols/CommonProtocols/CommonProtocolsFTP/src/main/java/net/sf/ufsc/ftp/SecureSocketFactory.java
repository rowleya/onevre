/*
 * UFSC: Universal File Server Connectivity
 * Copyright (c) 2004-2007 Paul Ferraro
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Contact: ferraro@users.sourceforge.net
 */
package net.sf.ufsc.ftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Extends Commons-Net SocketFactory adding SSL socket support.
 *
 * @author Paul Ferraro
 */
public class SecureSocketFactory extends SocketFactory
{
	private SSLSocketFactory socketFactory;
	private SSLServerSocketFactory serverSocketFactory;

	public SecureSocketFactory(SSLContext context)
	{
//		this.socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//		this.serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		this.socketFactory = context.getSocketFactory();
		this.serverSocketFactory = context.getServerSocketFactory();
	}

	public Socket secureSocket(Socket socket, String host, int port, boolean autoClose) throws IOException
	{
    	return this.init(this.socketFactory.createSocket(socket, host, port, autoClose));
	}

	public Socket createSocket(String host, int port) throws UnknownHostException, IOException
	{
		return this.init(this.socketFactory.createSocket(host, port));
	}

	public Socket createSocket(InetAddress address, int port) throws IOException
	{
		return this.init(this.socketFactory.createSocket(address, port));
	}

	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws UnknownHostException, IOException
	{
		return this.init(this.socketFactory.createSocket(host, port, localAddress, localPort));
	}

	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
	{
		return this.init(this.socketFactory.createSocket(address, port, localAddress, localPort));
	}

	public ServerSocket createServerSocket(int port) throws IOException
	{
		return this.init(this.serverSocketFactory.createServerSocket(port));
	}

	public ServerSocket createServerSocket(int port, int backlog) throws IOException
	{
		return this.init(this.serverSocketFactory.createServerSocket(port, backlog));
	}

	public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException
	{
		return this.init(this.serverSocketFactory.createServerSocket(port, backlog, ifAddress));
	}

	public Socket init(Socket socket) throws IOException
	{
		SSLSocket sslSocket = (SSLSocket) socket;

		sslSocket.setUseClientMode(true);
		sslSocket.setWantClientAuth(false);

		return socket;
	}

	public ServerSocket init(ServerSocket socket)
	{
		SSLServerSocket sslSocket = (SSLServerSocket) socket;

		sslSocket.setUseClientMode(true);
		sslSocket.setWantClientAuth(false);

		return socket;
	}
}
