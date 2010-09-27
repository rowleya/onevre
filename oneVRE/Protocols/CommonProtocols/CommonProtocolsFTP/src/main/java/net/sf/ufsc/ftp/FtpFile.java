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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ufsc.AbstractFile;
import net.sf.ufsc.File;
import net.sf.ufsc.InputStreamAdapter;
import net.sf.ufsc.OutputStreamAdapter;
import net.sf.ufsc.StreamClosedEvent;
import net.sf.ufsc.StreamClosedListener;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * @author  Paul Ferraro
 * @since   1.0
 */
public class FtpFile extends AbstractFile implements StreamClosedListener
{
	protected FTPClient client;

	public FtpFile(FTPClient client, URI uri)
	{
		super(uri);

		this.client = client;
	}

	/**
	 * @see net.sf.ufsc.File#exists()
	 */
	public boolean exists() throws IOException
	{
		return this.getFile() != null;
	}

	/**
	 * @see net.sf.ufsc.File#isDirectory()
	 */
	public boolean isDirectory() throws IOException
	{
		boolean directory = this.getFile().isDirectory();

		this.logReply();

		return directory;
	}

	/**
	 * @see net.sf.ufsc.File#lastModified()
	 */
	public Date lastModified() throws IOException
	{
		Date date = this.getFile().getTimestamp().getTime();

		this.logReply();

		return date;
	}

	/**
	 * @see net.sf.ufsc.File#length()
	 */
	public long length() throws IOException
	{
		return this.getFile().getSize();
	}

	/**
	 * @see net.sf.ufsc.File#list()
	 */
	public File[] list() throws IOException
	{
		File[] files = this.toFiles(this.client.listFiles(this.uri.getPath()));

		this.logReply();

		return files;
	}

	/**
	 * @see net.sf.ufsc.File#list(java.lang.String)
	 */
	public File[] list(String pattern) throws IOException
	{
		File[] files = this.toFiles(this.client.listFiles(this.uri.resolve(pattern).getPath()));

		this.logReply();

		return files;
	}

	private File[] toFiles(FTPFile[] files)
	{
		if (files == null) return new FtpFile[0];

		List<File> fileList = new ArrayList<File>(files.length);

		for (int i = 0; i < files.length; ++i)
		{
			FTPFile file = files[i];

			if (file != null)
			{
				try
				{
					fileList.add(this.getFile(this.uri.resolve(file.getName())));
				}
				catch (IllegalArgumentException e)
				{
					this.logger.warn(file.getRawListing(), e);
				}
			}
		}

		return fileList.toArray(new File[fileList.size()]);
	}

	/**
	 * @see net.sf.ufsc.File#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return this.getInputStream(FTP.BINARY_FILE_TYPE);
	}

	/**
	 * @see net.sf.ufsc.File#getReader()
	 */
	@Override
	public Reader getReader() throws IOException
	{
		InputStream input = this.getInputStream(FTP.ASCII_FILE_TYPE);

		return (input != null) ? new InputStreamReader(input) : null;
	}

	private InputStream getInputStream(int type) throws IOException
	{
		this.setFileType(type);

		InputStream input = this.client.retrieveFileStream(this.uri.getPath());

		if (input == null)
		{
			this.logger.warn(this.client.getReplyString());

			return null;
		}

		return new InputStreamAdapter(input, this);
	}

	/**
	 * @see net.sf.ufsc.File#getOutputStream()
	 */
	public OutputStream getOutputStream(boolean append) throws IOException
	{
		return this.getOutputStream(FTP.BINARY_FILE_TYPE, append);
	}

	/**
	 * @see net.sf.ufsc.File#getWriter(boolean)
	 */
	@Override
	public Writer getWriter(boolean append) throws IOException
	{
		return new OutputStreamWriter(this.getOutputStream(FTP.ASCII_FILE_TYPE, append));
	}

	private OutputStream getOutputStream(final int type, final boolean append) throws IOException
	{
		this.setFileType(type);

		String path = this.uri.getPath();

		OutputStream output = append ? this.client.appendFileStream(path) : this.client.storeFileStream(path);

		if (output == null)
		{
			throw new IOException(this.client.getReplyString());
		}

		return new OutputStreamAdapter(output, this);
	}

	/**
	 * @see net.sf.ufsc.File#delete()
	 */
	public void delete() throws IOException
	{
		String path = this.uri.getPath();

		if (this.isDirectory())
		{
			this.client.rmd(path);
		}
		else
		{
			this.client.deleteFile(path);
		}

		this.logReply();
	}

	@Override
	protected File getFile(URI uri)
	{
		return new FtpFile(this.client, uri);
	}

	private FTPFile getFile() throws IOException
	{
		FTPFile[] files = this.client.listFiles(this.uri.getPath());

		this.logReply();

		return (files != null) && (files.length > 0) ? files[0] : null;
	}

	/**
	 * @see net.sf.ufsc.AbstractFile#createDirectory()
	 */
	@Override
	protected boolean makeDirectory() throws IOException
	{
		boolean success = this.client.makeDirectory(this.uri.getPath());

		this.logReply();

		return success;
	}

	/**
	 * @see net.sf.ufsc.File#move(java.lang.String)
	 */
	public void move(String path) throws IOException
	{
		this.client.rename(this.uri.getPath(), this.uri.resolve(path).getPath());

		this.logReply();
	}

	/**
	 * @see net.sf.ufsc.StreamClosedListener#closed(net.sf.ufsc.StreamClosedEvent)
	 */
	public void closed(StreamClosedEvent event) throws IOException
	{
		boolean success = this.client.completePendingCommand();

		String reply = this.client.getReplyString();

		if (!success)
		{
			throw new IOException(reply);
		}

		this.logger.info(reply);
	}

	private void setFileType(int type) throws IOException
	{
		this.client.setFileType(type);

		this.logReply();
	}

	private void logReply()
	{
		this.logger.info(this.client.getReplyString());
	}
}
