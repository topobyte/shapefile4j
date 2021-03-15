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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;

import de.topobyte.esri.shapefile.dbf.Database;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.index.Record;
import de.topobyte.esri.shapefile.shape.AbstractShape;
import de.topobyte.esri.shapefile.shape.ShapeType;
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

	public List<Geometry> getAllGeometries() throws InvalidShapeFileException,
			IOException
	{
		File shx = shapefile.getIndexFile();
		FileInputStream isShx = new FileInputStream(shx);
		ShapeIndexReader shapeIndexReader = new ShapeIndexReader(isShx);
		shapeIndexReader.read();
		List<Record> records = shapeIndexReader.getRecords();

		File shp = shapefile.getShapefileFile();
		FileInputStream isShp = new FileInputStream(shp);

		List<Geometry> result = new ArrayList<>();

		ValidationPreferences prefs = new ValidationPreferences();
		prefs.setMaxNumberOfPointsPerShape(Integer.MAX_VALUE);
		ShapeFileReader sfr = new ShapeFileReader(isShp, records, prefs);
		AbstractShape s;

		int i = 0;
		while ((s = sfr.next()) != null) {
			i++;
			ShapeType shapeType = s.getShapeType();
			logger.debug("ITEM " + i + ": " + shapeType);
			if (shapeType == ShapeType.POLYGON) {
				PolygonShape p = (PolygonShape) s;
				MultiPolygon polygon = ToJts.convert(p);
				result.add(polygon);
			} else if (shapeType == ShapeType.POLYLINE) {
				PolylineShape p = (PolylineShape) s;
				MultiLineString mls = ToJts.convert(p);
				result.add(mls);
			}
		}

		return result;
	}

	public Database getDatabase()
	{
		return new Database(shapefile.getDatabaseFile().getAbsolutePath());
	}
}
