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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import de.topobyte.esri.shapefile.shape.PointData;
import de.topobyte.esri.shapefile.shape.shapes.PolygonShape;
import de.topobyte.esri.shapefile.shape.shapes.PolylineShape;

public class ToJts
{

	final static Logger logger = LoggerFactory.getLogger(ToJts.class);

	private static GeometryFactory f = new GeometryFactory();

	private static Coordinate[] convert(PointData[] points)
	{
		Coordinate[] coordinates = new Coordinate[points.length];
		for (int k = 0; k < points.length; k++) {
			PointData pointData = points[k];
			coordinates[k] = new Coordinate(pointData.getX(), pointData.getY());
		}
		return coordinates;
	}

	public static MultiPolygon convert(PolygonShape p)
	{
		List<LinearRing> outerRings = new ArrayList<LinearRing>();
		List<LinearRing> innerRings = new ArrayList<LinearRing>();
		int parts = p.getNumberOfParts();
		for (int i = 0; i < parts; i++) {
			Coordinate[] coordinates = convert(p.getPointsOfPart(i));
			LinearRing ring = f.createLinearRing(coordinates);
			boolean ccw = CGAlgorithms.isCCW(coordinates);
			if (ccw) {
				innerRings.add(ring);
			} else {
				outerRings.add(ring);
			}
		}
		return createMultipolygon(outerRings, innerRings);
	}

	public static MultiLineString convert(PolylineShape p)
	{
		int parts = p.getNumberOfParts();
		LineString[] strings = new LineString[parts];
		for (int i = 0; i < parts; i++) {
			Coordinate[] coordinates = convert(p.getPointsOfPart(i));
			strings[i] = f.createLineString(coordinates);
		}
		return f.createMultiLineString(strings);
	}

	private static MultiPolygon createMultipolygon(List<LinearRing> outerRings,
			List<LinearRing> innerRings)
	{
		Polygon[] polygons = new Polygon[outerRings.size()];
		for (int i = 0; i < polygons.length; i++) {
			LinearRing shell = outerRings.get(i);
			polygons[i] = f.createPolygon(shell, null);
		}

		// Without holes, easy case
		if (innerRings.size() == 0) {
			return f.createMultiPolygon(polygons);
		}

		// We got holes, locate outer rings for each inner ring
		Map<Polygon, List<LinearRing>> map = new HashMap<Polygon, List<LinearRing>>();
		for (LinearRing inner : innerRings) {
			for (Polygon p : polygons) {
				if (p.contains(inner)) {
					List<LinearRing> holes = map.get(p);
					if (holes == null) {
						holes = new ArrayList<LinearRing>();
						map.put(p, holes);
					}
					holes.add(inner);
				}
			}
		}
		Polygon[] polygonsWithHoles = new Polygon[outerRings.size()];
		for (int i = 0; i < outerRings.size(); i++) {
			Polygon p = polygons[i];
			List<LinearRing> inner = map.get(p);
			if (inner == null) {
				polygonsWithHoles[i] = p;
				continue;
			}
			LinearRing shell = outerRings.get(i);
			LinearRing[] holes = inner.toArray(new LinearRing[0]);
			polygonsWithHoles[i] = f.createPolygon(shell, holes);
		}
		return f.createMultiPolygon(polygonsWithHoles);
	}
}
