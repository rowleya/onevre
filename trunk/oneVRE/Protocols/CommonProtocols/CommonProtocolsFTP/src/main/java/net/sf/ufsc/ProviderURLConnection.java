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
package net.sf.ufsc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Paul Ferraro
 */
public class ProviderURLConnection extends URLConnection
{
	private Provider provider;
	private Session session;

	/**
	 * Constructs a new ProviderURLConnection.
	 * @param url
	 */
	protected ProviderURLConnection(Provider provider, URL url)
	{
		super(url);

		this.provider = provider;
	}

	/**
	 * @see java.net.URLConnection#connect()
	 */
	public void connect() throws java.io.IOException
	{
		if (this.connected) return;

		try
		{
			this.session = this.provider.createSession(this.url.toURI());

			this.connected = true;
		}
		catch (URISyntaxException e)
		{
			throw new IOException(e.toString());
		}
	}

	/**
	 * @see java.net.URLConnection#getContentLength()
	 */
	@Override
	public int getContentLength()
	{
		try
		{
			if (!this.connected) this.connect();

			return Long.valueOf(this.session.getFile().length()).intValue();
		}
		catch (java.io.IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @see java.net.URLConnection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws java.io.IOException
	{
		if (!this.connected) this.connect();

		return this.session.getFile().getInputStream();
	}

	/**
	 * @see java.net.URLConnection#getLastModified()
	 */
	@Override
	public long getLastModified()
	{
		try
		{
			if (!this.connected) this.connect();

			return this.session.getFile().lastModified().getTime();
		}
		catch (java.io.IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @see java.net.URLConnection#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws java.io.IOException
	{
		if (!this.connected) this.connect();

		return this.session.getFile().getOutputStream(false);
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize()
	{
		if (this.connected)
		{
			this.session.close();
		}
	}
}
