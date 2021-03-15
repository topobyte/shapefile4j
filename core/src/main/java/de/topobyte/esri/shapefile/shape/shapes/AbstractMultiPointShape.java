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
import de.topobyte.esri.shapefile.shape.AbstractShape;
import de.topobyte.esri.shapefile.shape.Const;
import de.topobyte.esri.shapefile.shape.PointData;
import de.topobyte.esri.shapefile.shape.ShapeHeader;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.util.ISUtil;

public abstract class AbstractMultiPointShape extends AbstractShape
{

	protected double boxMinX;
	protected double boxMinY;
	protected double boxMaxX;
	protected double boxMaxY;

	protected int numberOfPoints;
	protected PointData[] points;

	public AbstractMultiPointShape(final ShapeHeader shapeHeader,
			final ShapeType shapeType, final InputStream is,
			final ValidationPreferences rules)
			throws IOException, InvalidShapeFileException
	{
		super(shapeHeader, shapeType, is, rules);

		this.boxMinX = ISUtil.readLeDouble(is);
		this.boxMinY = ISUtil.readLeDouble(is);
		this.boxMaxX = ISUtil.readLeDouble(is);
		this.boxMaxY = ISUtil.readLeDouble(is);

		this.numberOfPoints = ISUtil.readLeInt(is);

		if (!rules.isAllowUnlimitedNumberOfPointsPerShape()) {
			if (this.numberOfPoints > rules.getMaxNumberOfPointsPerShape()) {
				throw new InvalidShapeFileException("Invalid "
						+ getShapeTypeName() + " shape number of points. "
						+ "The allowed maximum number of points was "
						+ rules.getMaxNumberOfPointsPerShape() + " but found "
						+ this.numberOfPoints + ". " + Const.PREFERENCES);
			}
		}

		this.points = new PointData[this.numberOfPoints];
		for (int i = 0; i < this.numberOfPoints; i++) {
			double x = ISUtil.readLeDouble(is);
			double y = ISUtil.readLeDouble(is);
			this.points[i] = new PointData(x, y);
		}

	}

	protected abstract String getShapeTypeName();

	// Getters

	public double getBoxMinX()
	{
		return boxMinX;
	}

	public double getBoxMinY()
	{
		return boxMinY;
	}

	public double getBoxMaxX()
	{
		return boxMaxX;
	}

	public double getBoxMaxY()
	{
		return boxMaxY;
	}

	public int getNumberOfPoints()
	{
		return numberOfPoints;
	}

	public PointData[] getPoints()
	{
		return points;
	}

}
