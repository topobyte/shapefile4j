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
 * Represents a MultiPointZ Shape object, as defined by the ESRI Shape file
 * specification.
 * 
 */
public class MultiPointZShape extends AbstractMultiPointShape
{

	private static final int BASE_CONTENT_LENGTH = (4 + 8 * 4 + 4 + 8 * 2 + 8 * 2) / 2;

	private double minZ;
	private double maxZ;
	private double[] z;

	private double minM;
	private double maxM;
	private double[] m;

	public MultiPointZShape(final ShapeHeader shapeHeader,
			final ShapeType shapeType, final InputStream is,
			final ValidationPreferences rules) throws IOException,
			InvalidShapeFileException
	{
		super(shapeHeader, shapeType, is, rules);

		if (!rules.isAllowBadContentLength()) {
			int expectedLength = BASE_CONTENT_LENGTH
					+ (this.numberOfPoints * (8 * 2 + 8 + 8)) / 2;
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

		this.minZ = ISUtil.readLeDouble(is);
		this.maxZ = ISUtil.readLeDouble(is);

		this.z = new double[this.numberOfPoints];
		for (int i = 0; i < this.numberOfPoints; i++) {
			this.z[i] = ISUtil.readLeDouble(is);
		}

		this.minM = ISUtil.readLeDouble(is);
		this.maxM = ISUtil.readLeDouble(is);

		this.m = new double[this.numberOfPoints];
		for (int i = 0; i < this.numberOfPoints; i++) {
			this.m[i] = ISUtil.readLeDouble(is);
		}

	}

	@Override
	protected String getShapeTypeName()
	{
		return "MultiPointZ";
	}

	// Getters

	public double getMinZ()
	{
		return minZ;
	}

	public double getMaxZ()
	{
		return maxZ;
	}

	public double[] getZ()
	{
		return z;
	}

	public double getMinM()
	{
		return minM;
	}

	public double getMaxM()
	{
		return maxM;
	}

	public double[] getM()
	{
		return m;
	}

}
