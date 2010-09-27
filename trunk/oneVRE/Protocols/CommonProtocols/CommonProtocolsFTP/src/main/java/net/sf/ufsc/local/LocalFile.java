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
package net.sf.ufsc.local;

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Date;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import net.sf.ufsc.File;

/**
 * @author  Paul Ferraro
 * @since   1.0
 */
public class LocalFile implements File
{
	private java.io.File file;

	protected LocalFile(URI uri)
	{
		this(new java.io.File(uri));
	}

	private LocalFile(java.io.File file)
	{
		this.file = file;
	}

	/**
	 * @see net.sf.ufsc.File#delete()
	 */
	public void delete()
	{
		this.file.delete();
	}

	/**
	 * @see net.sf.ufsc.File#exists()
	 */
	public boolean exists()
	{
		return this.file.exists();
	}

	/**
	 * @see net.sf.ufsc.File#getFile(java.lang.String)
	 */
	public File getFile(String path)
	{
		return new LocalFile(this.getURI().resolve(path));
	}

	/**
	 * @see net.sf.ufsc.File#getInputStream()
	 */
	public InputStream getInputStream() throws IOException
	{
		return new FileInputStream(this.file);
	}

	/**
	 * @see net.sf.ufsc.File#getReader()
	 */

	public Reader getReader() throws IOException
	{
		return new FileReader(this.file);
	}

	/**
	 * @see net.sf.ufsc.File#getOutputStream()
	 */
	public OutputStream getOutputStream(boolean append) throws IOException
	{
		return new FileOutputStream(this.file, append);
	}

	/**
	 * @see net.sf.ufsc.File#getWriter(boolean)
	 */

	public Writer getWriter(boolean append) throws IOException
	{
		return new FileWriter(this.file, append);
	}

	/**
	 * @see net.sf.ufsc.File#getParent()
	 */
	public File getParent()
	{
		return new LocalFile(this.file.getParentFile().toURI());
	}

	/**
	 * @see net.sf.ufsc.File#toURI()
	 */
	public URI getURI()
	{
		return this.file.toURI();
	}

	/**
	 * @see net.sf.ufsc.File#isDirectory()
	 */
	public boolean isDirectory()
	{
		return this.file.isDirectory();
	}

	/**
	 * @see net.sf.ufsc.File#lastModified()
	 */
	public Date lastModified()
	{
		return new Date(this.file.lastModified());
	}

	/**
	 * @see net.sf.ufsc.File#length()
	 */
	public long length()
	{
		return this.file.length();
	}

	/**
	 * @see net.sf.ufsc.File#list()
	 */
	public File[] list()
	{
		if (!this.file.isDirectory()) return null;

		return this.toLocalFiles(this.file.listFiles());
	}

	/**
	 * @see net.sf.ufsc.File#list(java.lang.String)
	 */
	public File[] list(String pattern)
	{
		if (!this.file.isDirectory()) return null;

		FileFilter filter = new WildcardFileFilter(pattern);

		return this.toLocalFiles(this.file.listFiles(filter));
	}

	private File[] toLocalFiles(java.io.File[] files)
	{
		File[] localFiles = new LocalFile[files.length];

		for (int i = 0; i < files.length; ++i)
		{
			localFiles[i] = new LocalFile(files[i]);
		}

		return localFiles;
	}

	/**
	 * @see net.sf.ufsc.File#mkdir()
	 */
	public boolean mkdir()
	{
		return this.file.mkdirs();
	}

	/**
	 * @see net.sf.ufsc.File#move(java.lang.String)
	 */
	public void move(String path)
	{
		this.file.renameTo(new java.io.File(this.file.toURI().resolve(path)));
	}

	/**
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(File file)
	{
		return this.getURI().compareTo(file.getURI());
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object)
	{
		return this.file.equals(LocalFile.class.cast(object).file);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.file.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.file.toString();
	}
}
