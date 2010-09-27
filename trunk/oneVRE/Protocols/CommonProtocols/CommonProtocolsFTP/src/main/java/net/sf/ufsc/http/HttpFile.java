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
package net.sf.ufsc.http;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

import net.sf.ufsc.AbstractFile;
import net.sf.ufsc.File;
import net.sf.ufsc.InputStreamAdapter;
import net.sf.ufsc.OutputStreamAdapter;
import net.sf.ufsc.StreamClosedEvent;
import net.sf.ufsc.StreamClosedListener;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

/**
 * @author Paul Ferraro
 *
 */
public class HttpFile extends AbstractFile
{
	public static final String CONTENT_LENGTH = "Content-Length"; //$NON-NLS-1$
	public static final String LAST_MODIFIED = "Last-Modified"; //$NON-NLS-1$

	private HttpClient client;

	public HttpFile(HttpClient client, URI uri)
	{
		super(uri);

		this.client = client;
	}

	/**
	 * @see net.sf.ufsc.AbstractFile#getFile(java.net.URI)
	 */
	@Override
	protected File getFile(URI uri)
	{
		return new HttpFile(this.client, uri);
	}

	/**
	 * @see net.sf.ufsc.File#delete()
	 */
	public void delete() throws java.io.IOException
	{
		DeleteMethod method = new DeleteMethod(this.uri.toString());

		try
		{
			this.execute(method);

			method.getResponseBody();
		}
		finally
		{
			method.releaseConnection();
		}
	}

	/**
	 * @see net.sf.ufsc.File#exists()
	 */
	public boolean exists() throws java.io.IOException
	{
		HeadMethod method = new HeadMethod(this.uri.toString());

		int status = this.client.executeMethod(method);

		if (status == HttpStatus.SC_OK)
		{
			return true;
		}
		else if (status == HttpStatus.SC_NOT_FOUND)
		{
			return false;
		}
		else
		{
			throw new IOException(method.getStatusText());
		}
	}

	/**
	 * @see net.sf.ufsc.File#isDirectory()
	 */
	public boolean isDirectory()
	{
		return false;
	}

	/**
	 * @see net.sf.ufsc.File#lastModified()
	 */
	public Date lastModified() throws java.io.IOException
	{
		HeadMethod method = new HeadMethod(this.uri.toString());

		try
		{
			this.execute(method);

			return DateUtil.parseDate(method.getResponseHeader(LAST_MODIFIED).getValue());
		}
		catch (DateParseException e)
		{
			throw new IOException(e.toString());
		}
		finally
		{
			method.releaseConnection();
		}
	}

	/**
	 * @see net.sf.ufsc.File#length()
	 */
	public long length() throws java.io.IOException
	{
		HeadMethod method = new HeadMethod(this.uri.toString());

		try
		{
			this.execute(method);

			return Long.parseLong(method.getResponseHeader(CONTENT_LENGTH).getValue());
		}
		finally
		{
			method.releaseConnection();
		}
	}

	/**
	 * @see net.sf.ufsc.File#list()
	 */
	public File[] list()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see net.sf.ufsc.File#list(java.lang.String)
	 */
	public File[] list(String pattern)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see net.sf.ufsc.File#getInputStream()
	 */
	public InputStream getInputStream() throws java.io.IOException
	{
		final GetMethod method = new GetMethod(this.uri.toString());

		this.execute(method);

		StreamClosedListener listener = new StreamClosedListener()
		{
			public void closed(StreamClosedEvent event) throws IOException
			{
				method.releaseConnection();
			}
		};

		return new InputStreamAdapter(method.getResponseBodyAsStream(), listener);
	}

	/**
	 * @see net.sf.ufsc.File#getOutputStream()
	 */
	public OutputStream getOutputStream(boolean append) throws java.io.IOException
	{
		if (append) throw new UnsupportedOperationException();

		final java.io.File file = java.io.File.createTempFile("ufsc", null); //$NON-NLS-1$

		StreamClosedListener listener = new StreamClosedListener()
		{
			public void closed(StreamClosedEvent event) throws IOException
			{
				PutMethod method = new PutMethod(HttpFile.this.getURI().toString());

				try
				{
					InputStream inputStream = new FileInputStream(file);

					method.setRequestEntity(new InputStreamRequestEntity(inputStream));

					HttpFile.this.execute(method);

					inputStream.close();
				}
				finally
				{
					method.releaseConnection();

					file.delete();
				}
			}
		};

		return new OutputStreamAdapter(new FileOutputStream(file), listener);
	}

	protected void execute(HttpMethod method) throws java.io.IOException
	{
		method.setFollowRedirects(true);
		method.setDoAuthentication(true);

		int status = this.client.executeMethod(method);

		if (status != HttpStatus.SC_OK)
		{
			throw new java.io.IOException(method.getStatusText());
		}
	}

	/**
	 * @see net.sf.ufsc.AbstractFile#makeDirectory()
	 */
	@Override
	protected boolean makeDirectory()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see net.sf.ufsc.File#move(java.lang.String)
	 */
	public void move(String path)
	{
		throw new UnsupportedOperationException();
	}
}
