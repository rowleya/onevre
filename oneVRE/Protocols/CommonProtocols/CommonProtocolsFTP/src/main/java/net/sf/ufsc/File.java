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
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.Date;

/**
 * An abstract representation of a file or directory on a file server.
 * @author  Paul Ferraro
 * @since   1.0
 */
public interface File extends Comparable<File>
{
	/**
	 * Deletes the file or directory represented by this file.
	 * Directories must be empty in order to be deleted.
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public void delete() throws IOException;
	
	/**
	 * Tests whether this file or directory exists.
	 * @return true, if this file or directory exists, false otherwise
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public boolean exists() throws IOException;
	
	/**
	 * Returns the file or directory found at the specifed relative or absolute path.
	 * @param path a relative or absolute path
	 * @return a file
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public File getFile(String path) throws IOException;
	
	/**
	 * Returns an input stream capable of reading the contents of this file.
	 * @return an input stream
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public InputStream getInputStream() throws IOException;

	public Reader getReader() throws IOException;

	/**
	 * Returns an output stream capable of writing/appending data to this file.
	 * @param append true, if data should be appended, false, if any existing data should be overwritten.
	 * @return an output stream
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public OutputStream getOutputStream(boolean append) throws IOException;

	public Writer getWriter(boolean append) throws IOException;

	/**
	 * Returns the parent directory of this file.
	 * @return a File, or null if this is the root directory of the file server.
	 * @throws IOException if an error occurred accessing the underlying file server
	 */	
	public File getParent() throws IOException;
	
	/**
	 * Returns the uri of this file.
	 * @return a URI.
	 */
	public URI getURI();
	
	/**
	 * Indicates whether or not this file is a directory.
	 * @return true, if this file is a directory, false otherwise
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public boolean isDirectory() throws IOException;

	/**
	 * Returns the last modified timestamp of this file.
	 * @return the data this file was last modified.
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public Date lastModified() throws IOException;
	
	/**
	 * Returns the size of this file.
	 * @return the file size
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public long length() throws IOException;
	
	/**
	 * Returns file contained in the current directory.
	 * @return an array of Files.
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public File[] list() throws IOException;
	
	/**
	 * Returns file matching the specified pattern in the current directory.
	 * @return an array of Files.
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public File[] list(String pattern) throws IOException;
	
	/**
	 * Creates the directory named by this file, including any necessary but nonexistent parent directories.
	 * @return true, if the directory was created; false otherwise
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public boolean mkdir() throws IOException;

	/**
	 * Moves/Renames this file to the specified path.
	 * @param path a relative or absolute path
	 * @throws IOException if an error occurred accessing the underlying file server
	 */
	public void move(String path) throws IOException;
}
