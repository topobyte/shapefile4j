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

public class HexaUtil
{

	public static final byte[] stringToByteArray(final String orig)
	{

		String txt = orig.toLowerCase();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < txt.length(); i++) {
			if (txt.charAt(i) != ' ') {
				sb.append(txt.charAt(i));
			}
		}

		String packed = sb.toString();
		if (packed.length() % 2 != 0) {
			throw new RuntimeException("Must have even number of hexadigits, "
					+ "but has " + packed.length() + ".");
		}

		byte[] result = new byte[packed.length() / 2];
		for (int i = 0; i < packed.length(); i = i + 2) {
			int left = hexaToDecimal(packed.charAt(i));
			int right = hexaToDecimal(packed.charAt(i + 1));
			int total = left * 16 + right;
			result[i / 2] = total < 128 ? (byte) total : (byte) (total - 256);
			// System.out.println("[" + left + ":" + right + "] -> " + result[i
			// / 2]);
		}

		return result;
	}

	public static final String byteArrayToString(final byte[] b)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int v = b[i] >= 0 ? b[i] : b[i] + 256;
			int left = v / 16;
			int right = v % 16;
			sb.append(decimalToHexa(left));
			sb.append(decimalToHexa(right));
		}
		return sb.toString();
	}

	// Util

	private static final String HEXA_DIGITS = "0123456789abcdef";

	private static char decimalToHexa(final int d)
	{
		return HEXA_DIGITS.charAt(d);
	}

	private static int hexaToDecimal(final char c)
	{
		for (int i = 0; i < HEXA_DIGITS.length(); i++) {
			if (c == HEXA_DIGITS.charAt(i)) {
				return i;
			}
		}
		throw new RuntimeException("Invalid hexa digit '" + c + "'.");
	}

}
