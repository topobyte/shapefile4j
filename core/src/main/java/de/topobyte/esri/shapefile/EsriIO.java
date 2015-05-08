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

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class EsriIO
{

	final static Logger logger = LoggerFactory.getLogger(EsriIO.class);

	/**
	 * Read in a ShapeFile and return all contained shapes as a list of
	 * Multipolygons.
	 * 
	 * @param filename
	 *            a filename to read from.
	 * @return a list of read multipolygons.
	 * @throws IOException
	 *             on failure of reading.
	 * @throws InvalidShapeFileException
	 */
	public static List<Geometry> readShapefile(String filename)
			throws IOException, InvalidShapeFileException
	{
		Shapefile shapeFile = new Shapefile(filename);
		ShapefileAccess sa = new ShapefileAccess(shapeFile);
		return sa.getAllGeometries();
	}

}
