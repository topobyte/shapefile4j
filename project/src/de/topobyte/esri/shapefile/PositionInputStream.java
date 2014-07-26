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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PositionInputStream extends FilterInputStream
{

	private long position = 0;

	public PositionInputStream(InputStream in)
	{
		super(in);
	}

	public long getPosition()
	{
		return position;
	}

	@Override
	public int read() throws IOException
	{
		int r = super.read();
		if (r >= 0) {
			position++;
		}
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		int r = super.read(b);
		if (r >= 0) {
			position += r;
		}
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		int r = super.read(b, off, len);
		if (r >= 0) {
			position += r;
		}
		return r;
	}

	@Override
	public long skip(long n) throws IOException
	{
		long r = super.skip(n);
		position += r;
		return r;
	}
}
