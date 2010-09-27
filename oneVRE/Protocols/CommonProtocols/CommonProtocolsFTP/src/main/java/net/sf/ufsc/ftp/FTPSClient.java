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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.SocketClient;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.EnterpriseUnixFTPEntryParser;

/**
 * Extends Commons-Net FTPClient adding AUTH/TLS FTPS support.
 *
 * @author Paul Ferraro
 */
public class FTPSClient extends FTPClient
{
	private static Log logger = LogFactory.getLog(FTPSClient.class);

	private static final String KEY_STORE_TYPE = "JCEKS"; //$NON-NLS-1$
	private static final String PROTOCOL = "TLS"; //$NON-NLS-1$
	private static final String PASSWORD = "password"; //$NON-NLS-1$

	private SecureSocketFactory socketFactory;
	private Socket clearSocket;

	public FTPSClient()
	{
		super();

		try
		{
			KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
			keyStore.load(null, PASSWORD.toCharArray());

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

			keyManagerFactory.init(keyStore, PASSWORD.toCharArray());

			SSLContext context = SSLContext.getInstance(PROTOCOL);
			context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { new SimpleTrustManager() }, null);

			this.socketFactory = new SecureSocketFactory(context);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @see org.apache.commons.net.SocketClient#connect(java.lang.String, int)
	 */
	@Override
	public void connect(String host, int port) throws SocketException, IOException
	{
	//	this.setReaderThread(false);

		super.connect(host, port);

		this.logReply();

		this.secure();
	}

	/**
	 * @see org.apache.commons.net.SocketClient#connect(java.lang.String)
	 */
	@Override
	public void connect(String host) throws SocketException, IOException
	{
		this.connect(host, this.getDefaultPort());
	}

	@SuppressWarnings("nls")
	public void secure() throws IOException
	{
		int reply = this.sendCommand("AUTH", PROTOCOL);

		if (!FTPReply.isPositiveCompletion(reply))
		{
			throw new IOException(this.getReplyString());
		}

		this.logReply();

		this.setSocketFactory(this.socketFactory);

		this.clearSocket = this._socket_;
		this._socket_ = this.socketFactory.secureSocket(this._socket_, this._socket_.getInetAddress().getHostName(), this._socket_.getPort(), true);

		OutputStream output = this._socket_.getOutputStream();
		output.write(("PBSZ 0" + SocketClient.NETASCII_EOL).getBytes());
		output.flush();

		this._connectAction_();

		reply = this.sendCommand("PROT", "P");

		if (!FTPReply.isPositiveCompletion(reply))
		{
			throw new IOException(this.getReplyString());
		}
	}

	/**
	 * @see org.apache.commons.net.ftp.FTPClient#login(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean login(String username, String password) throws IOException
	{
		boolean success = super.login(username, password);

		if (success)
		{
			this.logReply();

			ccc();

			this.logReply();

			this.getSystemName();
		}

		return success;
	}

	/**
	 * @see org.apache.commons.net.ftp.FTPClient#login(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean login(String username, String password, String account) throws IOException
	{
		boolean success = super.login(username, password, account);

		if (success)
		{
			this.logReply();

			ccc();

			this.logReply();

			this.getSystemName();
		}

		return success;
	}

	@SuppressWarnings("nls")
	public int ccc() throws IOException
	{
		int reply = this.sendCommand("CCC");

		if (FTPReply.isPositiveCompletion(reply))
		{
			this._controlInput_ = new BufferedReader(new InputStreamReader(this.clearSocket.getInputStream(), this.getControlEncoding()));
			this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this.clearSocket.getOutputStream(), this.getControlEncoding()));
		}

		return reply;
	}

	/**
	 * @see org.apache.commons.net.ftp.FTPClient#getSystemName()
	 */
	@Override
	public String getSystemName() throws IOException
	{
		String system = super.getSystemName();

		if (system.equals("UNKNOWN Type: L8")) //$NON-NLS-1$
		{
			this.configure(new FTPClientConfig(EnterpriseUnixFTPEntryParser.class.getName()));
		}

		return system;
	}

	public void logReply()
	{
		logger.info(this.getReplyString());
	}

	static class SimpleTrustManager implements X509TrustManager
	{
		public void checkClientTrusted(X509Certificate[] certificates, String authType)
		{
			// Do nothing
		}

		public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException
		{
//			for (int i = 0; i < certificates.length; ++i)
//			{
//				certificates[i].checkValidity();
//			}
		}

		public X509Certificate[] getAcceptedIssuers()
		{
			return null;
		}
	}
}
