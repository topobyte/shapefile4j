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
import java.util.Arrays;

import de.topobyte.esri.shapefile.ValidationPreferences;
import de.topobyte.esri.shapefile.exception.InvalidShapeFileException;
import de.topobyte.esri.shapefile.shape.Const;
import de.topobyte.esri.shapefile.shape.ShapeHeader;
import de.topobyte.esri.shapefile.shape.ShapeType;
import de.topobyte.esri.shapefile.util.ISUtil;

public abstract class AbstractPolyZShape extends AbstractPolyShape
{

	private static final int BASE_CONTENT_LENGTH = (4 + 8 * 4 + 4 + 4 + 8 * 2 + 8 * 2) / 2;

	private double minZ;
	private double maxZ;
	private double[] z;

	private double minM;
	private double maxM;
	private double[] measures;

	public AbstractPolyZShape(final ShapeHeader shapeHeader,
			final ShapeType shapeType, final InputStream is,
			final ValidationPreferences rules) throws IOException,
			InvalidShapeFileException
	{

		super(shapeHeader, shapeType, is, rules);

		if (!rules.isAllowBadContentLength()) {
			int expectedLength = BASE_CONTENT_LENGTH //
					+ (this.numberOfParts * (4)) / 2 //
					+ (this.numberOfPoints * (8 * 2 + 8 + 8)) / 2;
			if (this.header.getContentLength() != expectedLength) {
				throw new InvalidShapeFileException("Invalid "
						+ getShapeTypeName()
						+ " shape header's content length. " + "Expected "
						+ expectedLength + " 16-bit words (for "
						+ this.numberOfParts + " parts and "
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

		this.measures = new double[this.numberOfPoints];
		for (int i = 0; i < this.numberOfPoints; i++) {
			this.measures[i] = ISUtil.readLeDouble(is);
		}

	}

	public double[] getMOfPart(final int i)
	{
		if (i < 0 || i >= this.numberOfParts) {
			throw new RuntimeException("Invalid part " + i
					+ ". Available parts [0:" + this.numberOfParts + "].");
		}
		int from = this.partFirstPoints[i];
		int to = i < this.numberOfParts - 1 ? this.partFirstPoints[i + 1]
				: this.points.length;

		if (from < 0 || from > this.points.length) {
			throw new RuntimeException("Malformed content. Part start (" + from
					+ ") is out of range. Valid range of points is [0:"
					+ this.points.length + "].");
		}

		if (to < 0 || to > this.points.length) {
			throw new RuntimeException("Malformed content. Part end (" + to
					+ ") is out of range. Valid range of points is [0:"
					+ this.points.length + "].");
		}

		return Arrays.copyOfRange(this.measures, from, to);
	}

	public double[] getZOfPart(final int i)
	{
		if (i < 0 || i >= this.numberOfParts) {
			throw new RuntimeException("Invalid part " + i
					+ ". Available parts [0:" + this.numberOfParts + "].");
		}
		int from = this.partFirstPoints[i];
		int to = i < this.numberOfParts - 1 ? this.partFirstPoints[i + 1]
				: this.points.length;

		if (from < 0 || from > this.points.length) {
			throw new RuntimeException("Malformed content. Part start (" + from
					+ ") is out of range. Valid range of points is [0:"
					+ this.points.length + "].");
		}

		if (to < 0 || to > this.points.length) {
			throw new RuntimeException("Malformed content. Part end (" + to
					+ ") is out of range. Valid range of points is [0:"
					+ this.points.length + "].");
		}

		return Arrays.copyOfRange(this.z, from, to);
	}

	// Accessors

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

	public double[] getMeasures()
	{
		return measures;
	}

}
