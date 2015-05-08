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

package de.topobyte.esri.shapefile.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import de.topobyte.esri.shapefile.exception.DataStreamEOFException;

public class ISUtil
{

	private static final byte[] BUFFER = new byte[8];
	private static final ByteBuffer BYTE_BUFFER = ByteBuffer.wrap(BUFFER);

	// Big endian int

	public static int readBeIntMaybeEOF(final InputStream is)
			throws DataStreamEOFException, IOException
	{
		readIntoBufferMaybeEOF(is, 4);
		return IntSerializer.deserializeBigEndian(BYTE_BUFFER);
	}

	public static int readBeInt(final InputStream is) throws IOException
	{
		readIntoBuffer(is, 4);
		return IntSerializer.deserializeBigEndian(BYTE_BUFFER);
	}

	// Big endian double

	public static double readBeDoubleMaybeEOF(final InputStream is)
			throws DataStreamEOFException, IOException
	{
		readIntoBufferMaybeEOF(is, 8);
		return DoubleSerializer.deserializeBigEndian(BYTE_BUFFER);
	}

	public static double readBeDouble(final InputStream is) throws IOException
	{
		readIntoBuffer(is, 8);
		return DoubleSerializer.deserializeBigEndian(BYTE_BUFFER);
	}

	// Little endian int

	public static int readLeIntMaybeEOF(final InputStream is)
			throws DataStreamEOFException, IOException
	{
		readIntoBufferMaybeEOF(is, 4);
		return IntSerializer.deserializeLittleEndian(BYTE_BUFFER);
	}

	public static int readLeInt(final InputStream is) throws IOException
	{
		readIntoBuffer(is, 4);
		// System.out.println("--> " + HexaUtil.byteArrayToString(BUFFER));
		return IntSerializer.deserializeLittleEndian(BYTE_BUFFER);
	}

	// Little endian double

	public static double readLeDoubleMaybeEOF(final InputStream is)
			throws DataStreamEOFException, IOException
	{
		readIntoBufferMaybeEOF(is, 8);
		return DoubleSerializer.deserializeLittleEndian(BYTE_BUFFER);
	}

	public static double readLeDouble(final InputStream is) throws IOException
	{
		readIntoBuffer(is, 8);
		return DoubleSerializer.deserializeLittleEndian(BYTE_BUFFER);
	}

	// Utils

	public static void skip(final InputStream is, long n) throws IOException
	{
		long skipped = 0;
		while (skipped < n) {
			skipped += is.skip(n - skipped);
		}
	}

	private static void readIntoBufferMaybeEOF(final InputStream is,
			final int length) throws DataStreamEOFException, IOException
	{
		try {
			int read = is.read(BUFFER, 0, length);
			if (read != length) {
				throw new DataStreamEOFException();
			}
		} catch (EOFException e) {
			throw new DataStreamEOFException();
		}
	}

	private static void readIntoBuffer(final InputStream is, final int length)
			throws IOException
	{
		int read = is.read(BUFFER, 0, length);
		if (read != length) {
			throw new EOFException();
		}
	}

}
