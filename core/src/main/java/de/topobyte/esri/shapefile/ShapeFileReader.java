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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.esri.shapefile.exception.DataStreamEOFException;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.header.ShapeFileHeader;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.shape.AbstractShape;
import de.topobyte.esri.shapefile.shape.ShapeHeader;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.shape.shapes.MultiPatchShape;
import de.topobyte.esri.shapefile.shape.shapes.MultiPointMShape;
import de.topobyte.esri.shapefile.shape.shapes.MultiPointPlainShape;
import de.topobyte.esri.shapefile.shape.shapes.MultiPointZShape;
import de.topobyte.esri.shapefile.shape.shapes.NullShape;
import de.topobyte.esri.shapefile.shape.shapes.PointMShape;
import de.topobyte.esri.shapefile.shape.shapes.PointShape;
import de.topobyte.esri.shapefile.shape.shapes.PointZShape;
import de.topobyte.esri.shapefile.shape.shapes.PolygonMShape;
import de.topobyte.esri.shapefile.shape.shapes.PolygonShape;
import de.topobyte.esri.shapefile.shape.shapes.PolygonZShape;
import de.topobyte.esri.shapefile.shape.shapes.PolylineMShape;
import de.topobyte.esri.shapefile.shape.shapes.PolylineShape;
import de.topobyte.esri.shapefile.shape.shapes.PolylineZShape;
import de.topobyte.esri.shapefile.util.ISUtil;

public class ShapeFileReader
{

	final static Logger logger = LoggerFactory.getLogger(ShapeFileReader.class);

	private PositionInputStream pis;
	private ValidationPreferences rules;

	private ShapeFileHeader header;
	private boolean eofReached;

	private List<Record> records;

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
	public ShapeFileReader(final InputStream is, List<Record> records)
			throws InvalidShapeFileException, IOException
	{
		ValidationPreferences rules = new ValidationPreferences();
		initialize(is, records, rules);
	}

	/**
	 * <p>
	 * Reads a Shape File from an InputStream using the specified validation
	 * preferences. Use this constructor when you want to relax or change the
	 * validation preferences.
	 * </p>
	 * 
	 * <p>
	 * The constructor will automatically read the header of the file.
	 * Thereafter, use the method next() to read all shapes.
	 * </p>
	 * 
	 * @param is
	 *            the InputStream to be read.
	 * @param records
	 * @param preferences
	 *            Customized validation preferences.
	 * @throws InvalidShapeFileException
	 *             if the data is malformed, according to the specified
	 *             preferences.
	 * @throws IOException
	 *             if it's not possible to read from the InputStream.
	 */
	public ShapeFileReader(final InputStream is, List<Record> records,
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

		if (rules.getForceShapeType() != null) {
			header.setShapeType(rules.getForceShapeType());
		}

		this.rules.advanceOneRecordNumber();
	}

	/**
	 * Reads one shape from the InputStream.
	 * 
	 * @return a shape object, or null when the end of the stream is reached.
	 *         The returned shape object will be of one of the following
	 *         classes:
	 *         <ul>
	 *         <li>NullShape,</li>
	 *         <li>PointShape,</li>
	 *         <li>PolylineShape,</li>
	 *         <li>PolygonShape,</li>
	 *         <li>MultiPointPlainShape,</li>
	 *         <li>PointZShape,</li>
	 *         <li>PolylineZShape,</li>
	 *         <li>PolygonZShape,</li>
	 *         <li>MultiPointZShape,</li>
	 *         <li>PointMShape,</li>
	 *         <li>PolylineMShape,</li>
	 *         <li>PolygonMShape,</li>
	 *         <li>MultiPointMShape,</li>
	 *         <li>or MultiPatchShape.</li>
	 *         </ul>
	 *         The method getShapeType() of the AbstractShape object provides
	 *         the shape type, in order to to cast the object to the appropriate
	 *         class.
	 * 
	 * @throws InvalidShapeFileException
	 *             if the data is malformed.
	 * @throws IOException
	 *             if it's not possible to read from the InputStream.
	 */
	public AbstractShape next() throws IOException, InvalidShapeFileException
	{
		if (this.eofReached) {
			return null;
		}

		int n = rules.getExpectedRecordNumber();

		if (n > records.size()) {
			logger.debug("No more records left according to index");
			return null;
		}

		logger.debug("next(). Expected record: " + n);

		long currentPosition = pis.getPosition();
		logger.debug("Current position: " + currentPosition);
		Record record = records.get(n - 1);
		logger.debug("Index position: " + record.getOffset() * 2);
		boolean ok = pis.getPosition() == record.getOffset() * 2;
		if (!ok) {
			long delta = record.getOffset() * 2 - pis.getPosition();
			if (delta > 0) {
				logger.warn("Skipping " + delta
						+ " bytes of the shapefile to reach the next item as defined in the index");
				ISUtil.skip(pis, delta);
			} else {
				logger.warn(
						"Error in shapefile: shapefile record was longer than defined in index");
			}
		}

		// Shape header

		ShapeHeader shapeHeader = null;
		ShapeType shapeType = null;

		try {
			shapeHeader = new ShapeHeader(this.pis, this.rules);
		} catch (DataStreamEOFException e) {
			this.eofReached = true;
			return null;
		}

		int contentLength = shapeHeader.getContentLength();
		logger.debug("Record length (index): " + record.getLength());
		logger.debug("Record length (shapefile): " + contentLength);

		int typeId;
		try {
			typeId = ISUtil.readLeInt(this.pis);
		} catch (EOFException e) {
			throw new InvalidShapeFileException("Unexpected end of stream. "
					+ "The data is too short for the shape that was being read.");
		}

		// Shape body

		if (this.rules.getForceShapeType() != null) {
			shapeType = this.rules.getForceShapeType();
		} else {
			shapeType = ShapeType.parse(typeId);
			if (shapeType == null) {
				throw new InvalidShapeFileException("Invalid shape type '"
						+ typeId + "'. " + "The shape type can be forced using "
						+ "the additional constructor with "
						+ "ValidationRules.");
			}
			if (!this.rules.isAllowMultipleShapeTypes()
					&& !this.header.getShapeType().equals(shapeType)) {
				throw new InvalidShapeFileException("Invalid shape type '"
						+ shapeType
						+ "'. All included shapes must have the same "
						+ "type as the one specified on the file header ("
						+ this.header.getShapeType()
						+ "). This validation can be disabled using the "
						+ "additional constructor with ValidationRules.");
			}
		}

		this.rules.advanceOneRecordNumber();

		try {
			switch (shapeType) {
			case NULL:
				return new NullShape(shapeHeader, shapeType, this.pis,
						this.rules);

			case POINT:
				return new PointShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case POLYLINE:
				return new PolylineShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case POLYGON:
				return new PolygonShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case MULTIPOINT:
				return new MultiPointPlainShape(shapeHeader, shapeType,
						this.pis, this.rules);

			case POINT_Z:
				return new PointZShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case POLYLINE_Z:
				return new PolylineZShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case POLYGON_Z:
				return new PolygonZShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case MULTIPOINT_Z:
				return new MultiPointZShape(shapeHeader, shapeType, this.pis,
						this.rules);

			case POINT_M:
				return new PointMShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case POLYLINE_M:
				return new PolylineMShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case POLYGON_M:
				return new PolygonMShape(shapeHeader, shapeType, this.pis,
						this.rules);
			case MULTIPOINT_M:
				return new MultiPointMShape(shapeHeader, shapeType, this.pis,
						this.rules);

			case MULTIPATCH:
				return new MultiPatchShape(shapeHeader, shapeType, this.pis,
						this.rules);

			default:
				throw new InvalidShapeFileException(
						"Unexpected shape type '" + shapeType + "'");
			}

		} catch (EOFException e) {
			throw new InvalidShapeFileException("Unexpected end of stream. "
					+ "The data is too short for the last shape (" + shapeType
					+ ") that was being read.");
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

}
