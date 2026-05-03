// Copyright 2026 Vladimir Alarcon, Sebastian Kürten
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
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.esri.shapefile.exception.DataStreamEOFException;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.header.ShapeFileHeader;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.record.ShapeRecordInfo;
import de.topobyte.esri.shapefile.shape.ShapeHeader;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.util.ISUtil;

public class ShapeRecordInfoReader
{

	final static Logger logger = LoggerFactory
			.getLogger(ShapeRecordInfoReader.class);

	private PositionInputStream pis;
	private ValidationPreferences rules;

	private ShapeFileHeader header;
	private boolean eofReached;

	private List<Record> records;

	public ShapeRecordInfoReader(final InputStream is, List<Record> records)
			throws InvalidShapeFileException, IOException
	{
		ValidationPreferences rules = new ValidationPreferences();
		initialize(is, records, rules);
	}

	public ShapeRecordInfoReader(final InputStream is, List<Record> records,
			final ValidationPreferences preferences)
			throws InvalidShapeFileException, IOException
	{
		initialize(is, records, preferences);
	}

	private void initialize(final InputStream is, List<Record> records,
			final ValidationPreferences preferences)
			throws IOException, InvalidShapeFileException
	{
		if (is == null) {
			throw new RuntimeException(
					"Must specify a non-null input stream to read from.");
		}
		if (preferences == null) {
			throw new RuntimeException("Must specify non-null rules.");
		}
		BufferedInputStream bis = new BufferedInputStream(is);
		this.pis = new PositionInputStream(bis);
		this.rules = preferences;
		this.eofReached = false;
		this.header = new ShapeFileHeader(this.pis);
		this.records = records;

		this.rules.advanceOneRecordNumber();
	}

	public ShapeRecordInfo next() throws IOException, InvalidShapeFileException
	{
		if (this.eofReached) {
			return null;
		}

		int n = rules.getExpectedRecordNumber();
		if (n > records.size()) {
			logger.debug("No more records left according to index");
			return null;
		}

		Record record = records.get(n - 1);
		if (pis.getPosition() != record.getOffset() * 2L) {
			long delta = record.getOffset() * 2L - pis.getPosition();
			if (delta > 0) {
				logger.debug("Skipping " + delta
						+ " bytes of the shapefile to reach the next item as defined in the index");
				ISUtil.skip(pis, delta);
			} else {
				logger.warn(
						"Error in shapefile: shapefile record was longer than defined in index");
			}
		}

		// Shape header

		ShapeHeader shapeHeader = null;
		try {
			shapeHeader = new ShapeHeader(this.pis, this.rules);
		} catch (DataStreamEOFException e) {
			this.eofReached = true;
			return null;
		}

		int typeId;
		try {
			typeId = ISUtil.readLeInt(this.pis);
		} catch (EOFException e) {
			throw new InvalidShapeFileException("Unexpected end of stream. "
					+ "The data is too short for the shape that was being read.");
		}

		// Shape body

		ShapeType shapeType = ShapeType.parse(typeId);

		if (shapeType == null) {
			throw new InvalidShapeFileException("Invalid shape type.");
		}

		this.rules.advanceOneRecordNumber();

		int contentBytes = shapeHeader.getContentLength() * 2;
		int bytesRead = 4;

		Envelope envelope = null;
		switch (shapeType) {
		case NULL:
			break;
		case POINT:
		case POINT_M:
		case POINT_Z:
			double x = ISUtil.readLeDouble(this.pis);
			double y = ISUtil.readLeDouble(this.pis);
			envelope = new Envelope(x, x, y, y);
			bytesRead += 16;
			break;
		case POLYLINE:
		case POLYLINE_M:
		case POLYLINE_Z:
		case POLYGON:
		case POLYGON_M:
		case POLYGON_Z:
		case MULTIPOINT:
		case MULTIPOINT_M:
		case MULTIPOINT_Z:
		case MULTIPATCH:
			double minX = ISUtil.readLeDouble(this.pis);
			double minY = ISUtil.readLeDouble(this.pis);
			double maxX = ISUtil.readLeDouble(this.pis);
			double maxY = ISUtil.readLeDouble(this.pis);
			envelope = new Envelope(minX, maxX, minY, maxY);
			bytesRead += 32;
			break;
		default:
			break;
		}

		int bytesToSkip = contentBytes - bytesRead;
		if (bytesToSkip > 0) {
			ISUtil.skip(this.pis, bytesToSkip);
		}

		return new ShapeRecordInfo(record, shapeHeader.getRecordNumber(),
				shapeHeader.getContentLength(), shapeType, envelope);
	}

	public ShapeFileHeader getHeader()
	{
		return header;
	}

}
