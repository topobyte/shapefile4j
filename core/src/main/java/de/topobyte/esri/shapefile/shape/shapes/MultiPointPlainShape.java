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

/**
 * Represents a MultiPoint Shape object, as defined by the ESRI Shape file
 * specification.
 * 
 */
public class MultiPointPlainShape extends AbstractMultiPointShape
{

	private static final int BASE_CONTENT_LENGTH = (4 + 8 * 4 + 4) / 2;

	public MultiPointPlainShape(final ShapeHeader shapeHeader,
			final ShapeType shapeType, final InputStream is,
			final ValidationPreferences rules)
			throws IOException, InvalidShapeFileException
	{

		super(shapeHeader, shapeType, is, rules);

		if (!rules.isAllowBadContentLength()) {
			int expectedLength = BASE_CONTENT_LENGTH
					+ (this.numberOfPoints * (8 * 2)) / 2;
			if (this.header.getContentLength() != expectedLength) {
				throw new InvalidShapeFileException("Invalid "
						+ getShapeTypeName()
						+ " shape header's content length. " + "Expected "
						+ expectedLength + " 16-bit words (for "
						+ this.numberOfPoints + " points)" + " but found "
						+ this.header.getContentLength() + ". "
						+ Const.PREFERENCES);
			}
		}

	}

	@Override
	protected String getShapeTypeName()
	{
		return "MultiPoint";
	}

}
