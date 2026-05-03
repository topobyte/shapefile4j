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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.esri.shapefile.dbf.Database;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.record.ShapeRecordInfo;
import de.topobyte.esri.shapefile.shape.AbstractShape;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.shape.shapes.AbstractMultiPointShape;
import de.topobyte.esri.shapefile.shape.shapes.AbstractPointShape;
import de.topobyte.esri.shapefile.shape.shapes.PolygonShape;
import de.topobyte.esri.shapefile.shape.shapes.PolylineShape;

public class ShapefileAccess
{

	final static Logger logger = LoggerFactory.getLogger(ShapefileAccess.class);

	private Shapefile shapefile;

	public ShapefileAccess(Shapefile shapefile)
	{
		this.shapefile = shapefile;
	}

	/**
	 * Read all geometries from the shapefile using permissive point-count
	 * validation suitable for large production datasets.
	 * 
	 * @return all geometries contained in the shapefile.
	 */
	public List<Geometry> getAllGeometries()
			throws InvalidShapeFileException, IOException
	{
		ValidationPreferences prefs = new ValidationPreferences();
		prefs.setMaxNumberOfPointsPerShape(Integer.MAX_VALUE);
		return getAllGeometries(prefs);
	}

	/**
	 * Read all geometries from the shapefile using the supplied validation
	 * preferences.
	 * 
	 * @param prefs
	 *            validation preferences to use while reading.
	 * @return all geometries contained in the shapefile.
	 */
	public List<Geometry> getAllGeometries(ValidationPreferences prefs)
			throws InvalidShapeFileException, IOException
	{
		List<Record> records = getRecords();
		return getGeometries(records, prefs);
	}

	/**
	 * Read one geometry for the supplied shapefile record.
	 * 
	 * <p>
	 * This method assumes that the supplied record is most likely not the first
	 * entry and therefore relaxes record-number validation accordingly.
	 * </p>
	 * 
	 * @param record
	 *            the record to read
	 * @return geometry for the supplied record.
	 */
	public Geometry getGeometry(Record record)
			throws InvalidShapeFileException, IOException
	{
		ValidationPreferences prefs = new ValidationPreferences();
		prefs.setMaxNumberOfPointsPerShape(Integer.MAX_VALUE);
		// We need to allow bad records numbers here because we most likely only
		// select a subset of the shapefile's records and will not encounter all
		// shapes indices without any gaps.
		prefs.setAllowBadRecordNumbers(true);

		return getGeometry(record, prefs);
	}

	/**
	 * Read geometries for the supplied subset of shapefile records.
	 * 
	 * <p>
	 * This method assumes that the supplied list may be sparse and therefore
	 * relaxes record-number validation accordingly.
	 * </p>
	 * 
	 * @param records
	 *            the subset of records to read.
	 * @return geometries for the supplied records.
	 */
	public List<Geometry> getGeometries(List<Record> records)
			throws InvalidShapeFileException, IOException
	{
		ValidationPreferences prefs = new ValidationPreferences();
		prefs.setMaxNumberOfPointsPerShape(Integer.MAX_VALUE);
		// We need to allow bad records numbers here because we most likely only
		// select a subset of the shapefile's records and will not encounter all
		// shapes indices without any gaps.
		prefs.setAllowBadRecordNumbers(true);
		return getGeometries(records, prefs);
	}

	/**
	 * Read one geometry for the supplied shapefile record using the supplied
	 * validation preferences.
	 * 
	 * @param record
	 *            the record to read.
	 * @param prefs
	 *            validation preferences to use while reading.
	 * @return geometry for the supplied record.
	 */
	public Geometry getGeometry(Record record, ValidationPreferences prefs)
			throws InvalidShapeFileException, IOException
	{
		File shp = shapefile.getShapefileFile();
		try (InputStream input = new FileInputStream(shp)) {
			ShapeFileReader reader = new ShapeFileReader(input,
					Arrays.asList(record), prefs);
			AbstractShape s = reader.next();
			return getGeometry(1, s);
		}
	}

	/**
	 * Read geometries for the supplied subset of shapefile records using the
	 * supplied validation preferences.
	 * 
	 * @param records
	 *            the subset of records to read.
	 * @param prefs
	 *            validation preferences to use while reading.
	 * @return geometries for the supplied records.
	 */
	public List<Geometry> getGeometries(List<Record> records,
			ValidationPreferences prefs)
			throws InvalidShapeFileException, IOException
	{
		List<Geometry> result = new ArrayList<>();

		File shp = shapefile.getShapefileFile();
		try (InputStream input = new FileInputStream(shp)) {
			ShapeFileReader reader = new ShapeFileReader(input, records, prefs);
			AbstractShape s;

			int i = 0;
			while ((s = reader.next()) != null) {
				result.add(getGeometry(++i, s));
			}
		}

		return result;
	}

	/**
	 * Read geometries for the supplied subset of shapefile records using a
	 * sequential streaming traversal and invoke the supplied handler for each
	 * converted geometry.
	 * 
	 * <p>
	 * This method assumes that the supplied list may be sparse and therefore
	 * relaxes record-number validation accordingly.
	 * </p>
	 * 
	 * @param records
	 *            the subset of records to read.
	 * @param handler
	 *            callback invoked once per converted geometry.
	 */
	public void forEachGeometry(List<Record> records, GeometryHandler handler)
			throws InvalidShapeFileException, IOException
	{
		ValidationPreferences prefs = new ValidationPreferences();
		prefs.setMaxNumberOfPointsPerShape(Integer.MAX_VALUE);
		// We need to allow bad records numbers here because we most likely only
		// select a subset of the shapefile's records and will not encounter all
		// shapes indices without any gaps.
		prefs.setAllowBadRecordNumbers(true);
		forEachGeometry(records, prefs, handler);
	}

	/**
	 * Read geometries for the supplied subset of shapefile records using the
	 * supplied validation preferences and a sequential streaming traversal and
	 * invoke the supplied handler for each converted geometry.
	 * 
	 * @param records
	 *            the subset of records to read.
	 * @param prefs
	 *            validation preferences to use while reading.
	 * @param handler
	 *            callback invoked once per converted geometry.
	 */
	public void forEachGeometry(List<Record> records,
			ValidationPreferences prefs, GeometryHandler handler)
			throws InvalidShapeFileException, IOException
	{
		File shp = shapefile.getShapefileFile();
		try (InputStream input = new FileInputStream(shp)) {
			ShapeFileReader reader = new ShapeFileReader(input, records, prefs);
			AbstractShape s;

			int i = 0;
			while ((s = reader.next()) != null) {
				int index = ++i;
				Record record = records.get(index - 1);
				handler.handle(index, record, getGeometry(index, s));
			}
		}
	}

	private Geometry getGeometry(int i, AbstractShape s)
	{
		ShapeType shapeType = s.getShapeType();
		logger.debug("ITEM " + i + ": " + shapeType);
		if (shapeType == ShapeType.POINT || shapeType == ShapeType.POINT_M
				|| shapeType == ShapeType.POINT_Z) {
			AbstractPointShape p = (AbstractPointShape) s;
			return ToJts.convert(p);
		} else if (shapeType == ShapeType.POLYGON
				|| shapeType == ShapeType.POLYGON_M
				|| shapeType == ShapeType.POLYGON_Z) {
			PolygonShape p = (PolygonShape) s;
			return ToJts.convert(p);
		} else if (shapeType == ShapeType.POLYLINE
				|| shapeType == ShapeType.POLYLINE_M
				|| shapeType == ShapeType.POLYLINE_Z) {
			PolylineShape p = (PolylineShape) s;
			return ToJts.convert(p);
		} else if (shapeType == ShapeType.MULTIPOINT
				|| shapeType == ShapeType.MULTIPOINT_M
				|| shapeType == ShapeType.MULTIPOINT_Z) {
			AbstractMultiPointShape p = (AbstractMultiPointShape) s;
			return ToJts.convert(p);
		}
		throw new IllegalStateException();
	}

	/**
	 * Read all index records from the shapefile's {@code .shx} file.
	 * 
	 * @return all index records contained in the shapefile.
	 */
	public List<Record> getRecords()
			throws InvalidShapeFileException, IOException
	{
		File shx = shapefile.getIndexFile();
		try (InputStream input = new FileInputStream(shx)) {
			ShapeIndexReader reader = new ShapeIndexReader(input);
			reader.read();
			return reader.getRecords();
		}
	}

	/**
	 * Select index records whose per-record envelope intersects the supplied
	 * envelope.
	 * 
	 * <p>
	 * This is only a coarse pre-filter based on record bounding boxes read from
	 * the {@code .shp} file. It does not perform exact geometry intersection
	 * tests.
	 * </p>
	 * 
	 * @param envelope
	 *            the query envelope.
	 * @return records whose record envelope intersects the query envelope.
	 */
	public List<Record> getRecords(Envelope envelope)
			throws InvalidShapeFileException, IOException
	{
		List<Record> records = getRecords();
		List<Record> selected = new ArrayList<>();

		File shp = shapefile.getShapefileFile();
		try (InputStream input = new FileInputStream(shp)) {
			ShapeRecordInfoReader reader = new ShapeRecordInfoReader(input,
					records);
			ShapeRecordInfo info;
			while ((info = reader.next()) != null) {
				Envelope itemEnvelope = info.getEnvelope();
				if (itemEnvelope != null && itemEnvelope.intersects(envelope)) {
					selected.add(info.getRecord());
				}
			}
		}

		return selected;
	}

	/**
	 * Read geometries whose per-record envelope intersects the supplied query
	 * envelope.
	 * 
	 * <p>
	 * Record selection is only a coarse pre-filter based on record bounding
	 * boxes. The returned geometries are not clipped to the envelope and may
	 * lie partially or fully outside it.
	 * </p>
	 * 
	 * @param envelope
	 *            the query envelope.
	 * @return geometries whose record envelope intersects the query envelope.
	 */
	public List<Geometry> getGeometries(Envelope envelope)
			throws InvalidShapeFileException, IOException
	{
		List<Record> records = getRecords(envelope);
		return getGeometries(records);
	}

	/**
	 * Read geometries whose per-record envelope intersects the supplied query
	 * envelope using the supplied validation preferences.
	 * 
	 * <p>
	 * Record selection is only a coarse pre-filter based on record bounding
	 * boxes. The returned geometries are not clipped to the envelope and may
	 * lie partially or fully outside it.
	 * </p>
	 * 
	 * @param envelope
	 *            the query envelope.
	 * @param prefs
	 *            validation preferences to use while reading.
	 * @return geometries whose record envelope intersects the query envelope.
	 */
	public List<Geometry> getGeometries(Envelope envelope,
			ValidationPreferences prefs)
			throws InvalidShapeFileException, IOException
	{
		List<Record> records = getRecords(envelope);
		// We need to allow bad records numbers here because we most likely only
		// select a subset of the shapefile's records and will not encounter all
		// shapes indices without any gaps.
		prefs.setAllowBadRecordNumbers(true);
		return getGeometries(records, prefs);
	}

	public Database getDatabase()
	{
		return new Database(shapefile.getDatabaseFile().getAbsolutePath());
	}

}
