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

import java.io.IOException;
import java.net.URI;

import net.sf.ufsc.AbstractSession;
import net.sf.ufsc.File;

/**
 * @author  Paul Ferraro
 * @since   1.0
 */
public class LocalSession extends AbstractSession
{
	private boolean closed = false;
	
	/**
	 * Constructs a new LocalSession.
	 * @param uri
	 * @throws IOException
	 */
	public LocalSession(URI uri)
	{
		super(uri);
	}

	/**
	 * @see net.sf.ufsc.Session#getFile()
	 */
	public File getFile()
	{
		return new LocalFile(this.uri);
	}

	/**
	 * @see net.sf.ufsc.Session#close()
	 */
	public void close()
	{
		this.closed = true;
	}
	
	/**
	 * @see net.sf.ufsc.Session#isClosed()
	 */
	public boolean isClosed()
	{
		return this.closed;
	}
}
