// Copyright 2014 Vladimir Alarcon, Sebastian Kürten
//
// This file is part of shapefile4j.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package de.topobyte.esri.shapefile;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.header.ShapeFileHeader;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.util.ISUtil;

public class ShapeIndexReader
{

	private BufferedInputStream is;

	private ShapeFileHeader header;

	private List<Record> records = new ArrayList<>();

	/**
	 * <p>
	 * Reads a Shape File from an InputStream using the default validation
	 * preferences. The default validation preferences conforms strictly to the
	 * ESRI ShapeFile specification.
	 * </p>
	 * 
	 * <p>
	 * The constructor will automatically read the header of the file.
	 * Thereafter, use the method next() to read all shapes.
	 * </p>
	 * 
	 * @param is
	 *            the InputStream to be read.
	 * @throws InvalidShapeFileException
	 *             if the data is malformed, according to the ESRI ShapeFile
	 *             specification.
	 * @throws IOException
	 *             if it's not possible to read from the InputStream.
	 */
	public ShapeIndexReader(final InputStream is)
			throws InvalidShapeFileException, IOException
	{
		initialize(is);
	}

	private void initialize(final InputStream is)
			throws IOException, InvalidShapeFileException
	{
		if (is == null) {
			throw new RuntimeException(
					"Must specify a non-null input stream to read from.");
		}
		this.is = new BufferedInputStream(is);
		this.header = new ShapeFileHeader(this.is);
	}

	public void read() throws IOException
	{
		while (true) {
			try {
				int offset = ISUtil.readBeInt(is);
				int length = ISUtil.readBeInt(is);
				records.add(new Record(offset, length));
			} catch (EOFException e) {
				break;
			}
		}
	}

	/**
	 * Returns the shape's header.
	 * 
	 * @return shape's header.
	 */
	public ShapeFileHeader getHeader()
	{
		return header;
	}

	public List<Record> getRecords()
	{
		return records;
	}
}
