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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Paul Ferraro
 *
 */
public abstract class AbstractFile implements File
{
	protected Log logger = LogFactory.getLog(this.getClass());
	protected URI uri;

	protected AbstractFile(URI uri)
	{
		this.uri = uri;
	}

	/**
	 * @see net.sf.ufsc.File#getFile(java.lang.String)
	 */
	public File getFile(String path) throws IOException
	{
		return this.getFile(this.uri.resolve(path));
	}

	/**
	 * @see net.sf.ufsc.File#getParent()
	 */
	public File getParent() throws IOException
	{
		return this.getFile(this.uri.resolve("..")); //$NON-NLS-1$
	}

	/**
	 * @see net.sf.ufsc.File#getURI()
	 */
	public URI getURI()
	{
		return this.uri;
	}

	protected abstract File getFile(URI uri) throws IOException;

	/**
	 * @see net.sf.ufsc.File#mkdir()
	 */
	public boolean mkdir() throws IOException
	{
		File parent = this.getParent();

		if ((parent != null) && !parent.exists())
		{
			parent.mkdir();
		}

		return this.makeDirectory();
	}

	protected abstract boolean makeDirectory() throws IOException;

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object)
	{
		return this.uri.equals(((File) object).getURI());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.uri.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.uri.toString();
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(File file)
	{
		return this.uri.compareTo(file.getURI());
	}

	/**
	 * @see net.sf.ufsc.File#getReader()
	 */
	public Reader getReader() throws IOException
	{
		return new InputStreamReader(this.getInputStream());
	}

	/**
	 * @see net.sf.ufsc.File#getWriter(boolean)
	 */
	public Writer getWriter(boolean append) throws IOException
	{
		return new OutputStreamWriter(this.getOutputStream(append));
	}
}
