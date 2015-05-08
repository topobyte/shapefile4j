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

package de.topobyte.esri.shapefile.shape.shapes;

import java.io.IOException;
import java.io.InputStream;

import de.topobyte.esri.shapefile.ValidationPreferences;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.shape.Const;
import de.topobyte.esri.shapefile.shape.ShapeHeader;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.util.ISUtil;

/**
 * Represents a PointM Shape object, as defined by the ESRI Shape file
 * specification.
 * 
 */
public class PointMShape extends AbstractPointShape
{

	private static final int FIXED_CONTENT_LENGTH = (4 + 8 + 8 + 8) / 2;

	private double m;

	public PointMShape(final ShapeHeader shapeHeader,
			final ShapeType shapeType, final InputStream is,
			final ValidationPreferences rules) throws IOException,
			InvalidShapeFileException
	{
		super(shapeHeader, shapeType, is, rules);

		if (!rules.isAllowBadContentLength()
				&& this.header.getContentLength() != FIXED_CONTENT_LENGTH) {
			throw new InvalidShapeFileException(
					"Invalid PointM shape header's content length. "
							+ "Expected " + FIXED_CONTENT_LENGTH
							+ " 16-bit words but found "
							+ this.header.getContentLength() + ". "
							+ Const.PREFERENCES);
		}

		this.m = ISUtil.readLeDouble(is);
	}

	// Getters

	public double getM()
	{
		return m;
	}

}
