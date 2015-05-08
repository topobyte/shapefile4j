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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DoubleSerializer
{

	private static final int BYTE_ARRAY_SIZE = 8;

	public static double deserializeBigEndian(final ByteBuffer b)
	{
		if (b == null) {
			throw new RuntimeException("Cannot deserialize null byte buffer.");
		}
		if (b.array().length < BYTE_ARRAY_SIZE) {
			throw new RuntimeException(
					"Cannot deserialize. Byte buffer must have at least "
							+ BYTE_ARRAY_SIZE + " bytes.");
		}
		b.order(ByteOrder.BIG_ENDIAN);
		b.position(0);
		return b.getDouble();
	}

	public static double deserializeLittleEndian(final ByteBuffer b)
	{
		if (b == null) {
			throw new RuntimeException("Cannot deserialize null buffer.");
		}
		if (b.array().length < BYTE_ARRAY_SIZE) {
			throw new RuntimeException(
					"Cannot deserialize. Byte buffer must have at least "
							+ BYTE_ARRAY_SIZE + " bytes.");
		}
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.position(0);
		return b.getDouble();
	}

	public static void serializeBigEndian(final double value, final ByteBuffer b)
	{
		if (b == null) {
			throw new RuntimeException(
					"Cannot serialize into null byte buffer.");
		}
		if (b.array().length < BYTE_ARRAY_SIZE) {
			throw new RuntimeException(
					"Cannot serialize. Byte buffer must have at least "
							+ BYTE_ARRAY_SIZE + " bytes.");
		}
		b.order(ByteOrder.BIG_ENDIAN);
		b.position(0);
		b.putDouble(value);
	}

	public static void serializeLittleEndian(final double value,
			final ByteBuffer b)
	{
		if (b == null) {
			throw new RuntimeException(
					"Cannot serialize into a null byte buffer.");
		}
		if (b.array().length < BYTE_ARRAY_SIZE) {
			throw new RuntimeException(
					"Cannot serialize. Byte buffer must have at least "
							+ BYTE_ARRAY_SIZE + " bytes.");
		}
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.position(0);
		b.putDouble(value);
	}

}
