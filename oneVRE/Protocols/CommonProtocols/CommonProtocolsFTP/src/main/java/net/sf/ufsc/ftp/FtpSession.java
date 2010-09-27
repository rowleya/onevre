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
import java.net.URI;

import net.sf.ufsc.AbstractSession;
import net.sf.ufsc.File;
import net.sf.ufsc.UserInfo;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author  Paul Ferraro
 * @since   1.0
 */
public class FtpSession extends AbstractSession
{
	private FTPClient client;

	/**
	 * Constructs a new FtpSession.
	 * @param uri
	 * @throws IOException
	 */
	public FtpSession(URI uri, FTPClient client) throws IOException
	{
		super(uri);

		this.client = client;

		UserInfo userInfo = new UserInfo(uri);

		String host = uri.getHost();

		int port = uri.getPort();

		if (port < 0)
		{
			this.client.connect(host);
		}
		else
		{
			this.client.connect(host, port);
		}

		String reply = this.client.getReplyString();

		if (!FTPReply.isPositiveCompletion(this.client.getReplyCode()))
		{
			this.close();

			throw new IOException(reply);
		}

		this.logger.info(reply);

		boolean loggedIn = this.client.login(userInfo.getUser(), userInfo.getPassword());

		reply = this.client.getReplyString();

		if (!loggedIn)
		{
			this.close();

			throw new IOException(reply);
		}

		this.logger.info(reply);

		this.client.enterLocalPassiveMode();
	}

	/**
	 * @see net.sf.ufsc.Session#getFile()
	 */
	public File getFile() throws IOException
	{
		return new FtpFile(this.client, this.uri);
	}

	/**
	 * @see net.sf.ufsc.Session#close()
	 */
	public void close()
	{
		if (!this.isClosed())
		{
			try
			{
				this.client.disconnect();
			}
			catch (IOException e)
			{
				this.logger.warn(e.getMessage());
			}
		}
	}

	/**
	 * @see net.sf.ufsc.Session#isClosed()
	 */
	public boolean isClosed()
	{
		return !this.client.isConnected();
	}
}
